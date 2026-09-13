import { AsyncPipe, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';

@Component({ selector: 'app-profile', standalone: true, imports: [AsyncPipe, NgIf], templateUrl: './profile.component.html', styleUrl: './profile.component.scss' })
export class ProfileComponent {
  readonly user$ = this.authService.user$;
  constructor(private readonly authService: AuthService) {}
}