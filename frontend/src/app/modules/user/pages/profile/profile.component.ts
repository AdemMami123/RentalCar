import { AsyncPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { Booking } from '../../../../core/models/booking.model';
import { IdentityDocument, User } from '../../../../core/models/user.model';
import { BookingService } from '../../../booking/services/booking.service';

@Component({ selector: 'app-profile', standalone: true, imports: [AsyncPipe, DatePipe, NgIf, NgFor, FormsModule], templateUrl: './profile.component.html', styleUrl: './profile.component.scss' })
export class ProfileComponent implements OnInit {
  readonly user$ = this.authService.user$;
  profile: Partial<User> = {};
  bookings: Booking[] = [];
  documents: IdentityDocument[] = [];
  uploading = false;
  saving = false;
  saved = false;
  error = '';
  constructor(private readonly authService: AuthService, private readonly bookingService: BookingService) {}

  ngOnInit(): void {
    this.authService.user$.subscribe(user => { if (user) this.profile = { ...user }; });
    this.bookingService.getMyBookings().subscribe(response => this.bookings = response.data);
    this.authService.getIdentityDocuments().subscribe(response => this.documents = response.data);
  }

  saveProfile(): void {
    this.saving = true;
    this.saved = false;
    this.error = '';
    this.authService.updateProfile(this.profile as User).subscribe({
      next: () => {
        this.saving = false;
        this.saved = true;
      },
      error: error => {
        this.saving = false;
        this.error = error.error?.message || 'Unable to update profile.';
      }
    });
  }

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

  onDocumentSelected(event: Event, type: 'DRIVERS_LICENSE' | 'IDENTIFICATION'): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.authService.uploadIdentityDocument(type, file).subscribe({
      next: response => this.documents = [response.data, ...this.documents],
      error: error => this.error = error.error?.message || 'Unable to upload document.'
    });
  }
}