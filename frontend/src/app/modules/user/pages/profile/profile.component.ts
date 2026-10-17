import { AsyncPipe, DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { AuthService } from '../../../../core/services/auth.service';
import { Booking } from '../../../../core/models/booking.model';
import { IdentityDocument, User } from '../../../../core/models/user.model';
import { BookingService } from '../../../booking/services/booking.service';
import { CarService } from '../../../car/services/car.service';
import { Car } from '../../../../core/models/car.model';

@Component({ selector: 'app-profile', standalone: true, imports: [AsyncPipe, DatePipe, DecimalPipe, NgIf, NgFor, FormsModule, RouterLink], templateUrl: './profile.component.html', styleUrl: './profile.component.scss' })
export class ProfileComponent implements OnInit {
  readonly user$ = this.authService.user$;
  profile: Partial<User> = {};
  bookings: Booking[] = [];
  favoriteCars: Car[] = [];
  documents: IdentityDocument[] = [];
  uploading = false;
  saving = false;
  saved = false;
  error = '';
  selectedDocument: File | null = null;
  selectedDocumentType: 'DRIVERS_LICENSE' | 'IDENTIFICATION' | null = null;
  selectedDocumentPreview = '';
  selectedDocumentIsPdf = false;
  documentPreviewZoom = 1;
  viewingDocumentId: number | null = null;
  viewingDocumentUrl = '';
  viewingDocumentSafeUrl: SafeResourceUrl | null = null;
  viewingDocumentIsPdf = false;
  documentLoading = false;
  constructor(
    private readonly authService: AuthService,
    private readonly bookingService: BookingService,
    private readonly carService: CarService,
    private readonly sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.authService.user$.subscribe(user => { if (user) this.profile = { ...user }; });
    this.bookingService.getMyBookings().subscribe(response => this.bookings = response.data);
    this.carService.getFavorites().subscribe({
      next: response => this.favoriteCars = response.data,
      error: error => this.error = error.error?.message || 'Unable to load favorite vehicles.'
    });
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
    this.error = '';
    this.selectedDocument = file;
    this.selectedDocumentType = type;
    this.selectedDocumentIsPdf = file.type === 'application/pdf';
    this.documentPreviewZoom = 1;
    this.selectedDocumentPreview = URL.createObjectURL(file);
  }

  closeDocumentUpload(): void {
    if (this.selectedDocumentPreview) URL.revokeObjectURL(this.selectedDocumentPreview);
    this.selectedDocument = null;
    this.selectedDocumentType = null;
    this.selectedDocumentPreview = '';
  }

  uploadSelectedDocument(): void {
    if (!this.selectedDocument || !this.selectedDocumentType) return;
    this.documentLoading = true;
    this.authService.uploadIdentityDocument(this.selectedDocumentType, this.selectedDocument).subscribe({
      next: response => {
        this.documents = [response.data, ...this.documents];
        this.documentLoading = false;
        this.closeDocumentUpload();
      },
      error: error => {
        this.documentLoading = false;
        this.error = error.error?.message || 'Unable to upload document.';
      }
    });
  }

  viewDocument(document: IdentityDocument): void {
    if (this.viewingDocumentId === document.id) {
      this.closeDocumentViewer();
      return;
    }
    this.documentLoading = true;
    this.authService.getIdentityDocumentPreview(document.id).subscribe({
      next: response => {
        this.viewingDocumentId = document.id;
        this.viewingDocumentUrl = response.data;
        this.viewingDocumentIsPdf = document.contentType === 'application/pdf';
        this.viewingDocumentSafeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(response.data);
        this.documentLoading = false;
      },
      error: error => {
        this.documentLoading = false;
        this.error = error.error?.message || 'Unable to open this document.';
      }
    });
  }

  closeDocumentViewer(): void {
    this.viewingDocumentId = null;
    this.viewingDocumentUrl = '';
    this.viewingDocumentSafeUrl = null;
  }

  removeFavorite(car: Car): void {
    this.carService.removeFavorite(car.id).subscribe({
      next: () => this.favoriteCars = this.favoriteCars.filter(item => item.id !== car.id),
      error: error => this.error = error.error?.message || 'Unable to remove favorite vehicle.'
    });
  }
}