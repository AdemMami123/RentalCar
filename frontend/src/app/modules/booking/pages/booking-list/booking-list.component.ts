import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../../../core/models/booking.model';
import { AuthService } from '../../../../core/services/auth.service';
import { User } from '../../../../core/models/user.model';

/**
 * Booking List Component - for clients to view their bookings
 */
@Component({
  selector: 'app-booking-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './booking-list.component.html',
  styleUrl: './booking-list.component.scss'
})
export class BookingListComponent implements OnInit {
  bookings: Booking[] = [];
  loading = true;
  error: string | null = null;
  statusFilter: string = 'all';
  currentUser: User | null = null;

  bookingStatuses = ['all', 'PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED'];

  constructor(
    private bookingService: BookingService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Subscribe to the current user
    this.authService.user$.subscribe((user) => {
      this.currentUser = user;
      if (user?.id) {
        this.loadBookings();
      }
    });
  }

  loadBookings(): void {
    if (!this.currentUser?.id) {
      this.error = 'User information not available';
      this.loading = false;
      return;
    }

    this.loading = true;
    this.error = null;

    if (this.statusFilter === 'all') {
      this.bookingService.getBookingsByUserId(this.currentUser.id).subscribe({
        next: (response) => {
          this.bookings = response.data;
          this.loading = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Error loading bookings';
          this.loading = false;
        }
      });
    } else {
      this.bookingService.getBookingsByUserIdAndStatus(this.currentUser.id, this.statusFilter).subscribe({
        next: (response) => {
          this.bookings = response.data;
          this.loading = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Error loading bookings';
          this.loading = false;
        }
      });
    }
  }

  setStatusFilter(status: string): void {
    this.statusFilter = status;
    this.loadBookings();
  }

  cancelBooking(bookingId: number | undefined): void {
    if (!bookingId || !confirm('Are you sure you want to cancel this booking?')) {
      return;
    }

    this.bookingService.cancelBooking(bookingId).subscribe({
      next: () => {
        this.loadBookings();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error cancelling booking';
      }
    });
  }

  getStatusBadgeClass(status: string | undefined): string {
    switch (status?.toUpperCase()) {
      case 'PENDING':
        return 'badge-warning';
      case 'CONFIRMED':
        return 'badge-info';
      case 'ACTIVE':
        return 'badge-success';
      case 'COMPLETED':
        return 'badge-secondary';
      case 'CANCELLED':
        return 'badge-danger';
      default:
        return 'badge-light';
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  canCancelBooking(booking: Booking): boolean {
    return booking.bookingStatus === 'PENDING' || booking.bookingStatus === 'CONFIRMED';
  }
}