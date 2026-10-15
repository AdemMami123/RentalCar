import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';

@Component({ selector: 'app-profile', standalone: true, imports: [AsyncPipe, NgIf], templateUrl: './profile.component.html', styleUrl: './profile.component.scss' })
export class ProfileComponent {
  readonly user$ = this.authService.user$;
  uploading = false;
  error = '';
  constructor(private readonly authService: AuthService) {}

  onProfileImageSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.uploading = true;
    this.error = '';
    this.authService.uploadProfileImage(file).subscribe({
      next: () => this.uploading = false,
      error: error => {
        this.error = error.error?.message || 'Unable to upload profile image.';
        this.uploading = false;
      }
    });
  }
}