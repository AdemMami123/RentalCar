import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Booking } from '../../../../core/models/booking.model';
import { Car } from '../../../../core/models/car.model';
import { BookingService } from '../../../booking/services/booking.service';
import { CarService } from '../../../car/services/car.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent implements OnInit {
  bookings: Booking[] = [];
  cars: Car[] = [];
  loading = true;
  error = '';

  constructor(
    private readonly bookingService: BookingService,
    private readonly carService: CarService
  ) {}

  ngOnInit(): void {
    forkJoin({
      bookings: this.bookingService.getAllBookings(),
      cars: this.carService.getAllCars()
    }).subscribe({
      next: result => {
        this.bookings = result.bookings.data || [];
        this.cars = result.cars.data || [];
        this.loading = false;
      },
      error: error => {
        this.error = error.error?.message || 'Unable to load dashboard data.';
        this.loading = false;
      }
    });
  }

  get pendingBookings(): number { return this.bookings.filter(booking => booking.bookingStatus === 'PENDING').length; }
  get confirmedBookings(): number { return this.bookings.filter(booking => booking.bookingStatus === 'CONFIRMED').length; }
  get activeRentals(): number { return this.bookings.filter(booking => booking.bookingStatus === 'ACTIVE').length; }
  get completedBookings(): number { return this.bookings.filter(booking => booking.bookingStatus === 'COMPLETED').length; }
  get availableCars(): number { return this.cars.filter(car => car.status === 'AVAILABLE').length; }
  get rentedCars(): number { return this.cars.filter(car => car.status === 'RENTED').length; }
  get maintenanceCars(): number { return this.cars.filter(car => car.status === 'MAINTENANCE').length; }
  get revenue(): number {
    return this.bookings
      .filter(booking => booking.bookingStatus === 'COMPLETED')
      .reduce((total, booking) => total + (booking.totalCost || 0), 0);
  }
  get utilization(): number {
    return this.cars.length ? Math.round((this.rentedCars / this.cars.length) * 100) : 0;
  }
  get recentBookings(): Booking[] {
    return [...this.bookings]
      .sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime())
      .slice(0, 5);
  }
  get attentionBookings(): Booking[] {
    return this.bookings
      .filter(booking => booking.bookingStatus === 'PENDING' || booking.bookingStatus === 'ACTIVE')
      .sort((a, b) => new Date(a.pickupDate).getTime() - new Date(b.pickupDate).getTime())
      .slice(0, 4);
  }

  formatDate(value?: string): string {
    return value ? new Date(value).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) : '—';
  }

  getStatusClass(status?: string): string {
    return `status-${(status || 'unknown').toLowerCase()}`;
  }
}