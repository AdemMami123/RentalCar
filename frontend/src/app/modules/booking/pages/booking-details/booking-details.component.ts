import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../../../core/models/booking.model';

/**
 * Booking Details Component - displays detailed information about a single booking
 */
@Component({
  selector: 'app-booking-details',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './booking-details.component.html',
  styleUrl: './booking-details.component.scss'
})
export class BookingDetailsComponent implements OnInit {
  booking: Booking | null = null;
  loading = true;
  error: string | null = null;

  statusSteps = ['PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED'];

  constructor(
    private route: ActivatedRoute,
    private bookingService: BookingService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const bookingId = params['id'];
      if (bookingId) {
        this.loadBookingDetails(bookingId);
      }
    });
  }

  loadBookingDetails(bookingId: number): void {
    this.loading = true;
    this.error = null;

    this.bookingService.getBookingById(bookingId).subscribe({
      next: (response) => {
        this.booking = response.data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error loading booking details';
        this.loading = false;
      }
    });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  cancelBooking(): void {
    if (this.booking && confirm('Are you sure you want to cancel this booking?')) {
      this.bookingService.cancelBooking(this.booking.id!).subscribe({
        next: () => {
          this.loadBookingDetails(this.booking!.id!);
        },
        error: (err) => {
          this.error = err.error?.message || 'Error cancelling booking';
        }
      });
    }
  }

  canCancelBooking(): boolean {
    return this.booking?.bookingStatus === 'PENDING' || this.booking?.bookingStatus === 'CONFIRMED';
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'warning';
      case 'CONFIRMED':
        return 'info';
      case 'ACTIVE':
        return 'success';
      case 'COMPLETED':
        return 'secondary';
      case 'CANCELLED':
        return 'danger';
      default:
        return 'light';
    }
  }

  getStatusColorSafe(status: string | undefined): string {
    return this.getStatusColor(status || '');
  }

  isStatusCompleted(status: string): boolean {
    const completedStatuses = ['COMPLETED', 'CANCELLED'];
    return completedStatuses.includes(status);
  }

  isStatusActive(status: string): boolean {
    return status === this.booking?.bookingStatus;
  }

  isCancelled(): boolean {
    return this.booking?.bookingStatus === 'CANCELLED';
  }
}
