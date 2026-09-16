import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { BookingService } from '../../../booking/services/booking.service';
import { CarService } from '../../../car/services/car.service';
import { RouterLink } from '@angular/router';
import { Booking } from '../../../../core/models/booking.model';
import { Car } from '../../../../core/models/car.model';

interface BookingStats {
  totalBookings: number;
  pendingBookings: number;
  confirmedBookings: number;
  activeBookings: number;
  completedBookings: number;
  cancelledBookings: number;
  totalRevenue: number;
}

interface BookingWithCar extends Booking {
  carDetails?: Car;
}

/**
 * Admin Booking Dashboard Component
 * 
 * Displays all bookings with advanced filtering, search, and management capabilities.
 * Admin-only component for managing the entire booking lifecycle.
 */
@Component({
  selector: 'app-admin-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-bookings.component.html',
  styleUrls: ['./admin-bookings.component.scss']
})
export class AdminBookingsComponent implements OnInit, OnDestroy {

  // Data
  bookings: BookingWithCar[] = [];
  filteredBookings: BookingWithCar[] = [];
  cars: Map<number, Car> = new Map();
  stats: BookingStats = {
    totalBookings: 0,
    pendingBookings: 0,
    confirmedBookings: 0,
    activeBookings: 0,
    completedBookings: 0,
    cancelledBookings: 0,
    totalRevenue: 0
  };

  // Filters
  selectedStatus: string = 'ALL';
  searchTerm: string = '';
  dateRangeStart: string = '';
  dateRangeEnd: string = '';
  selectedCarId: number | null = null;

  // Pagination
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;
  paginatedBookings: BookingWithCar[] = [];

  // UI State
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  selectedBookingForAction: Booking | null = null;
  actionInProgress: boolean = false;

  // Status options
  bookingStatusOptions = [
    { value: 'ALL', label: 'All Bookings' },
    { value: 'PENDING', label: 'Pending' },
    { value: 'CONFIRMED', label: 'Confirmed' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'COMPLETED', label: 'Completed' },
    { value: 'CANCELLED', label: 'Cancelled' }
  ];

  // Sorting
  sortBy: string = 'createdAt';
  sortOrder: 'asc' | 'desc' = 'desc';
  readonly Math = Math;

  private destroy$ = new Subject<void>();

  constructor(
    private bookingService: BookingService,
    private carService: CarService
  ) {}

  ngOnInit(): void {
    this.loadBookings();
    this.loadCars();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load all bookings from backend
   */
  loadBookings(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.bookingService.getAllBookings()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.bookings = response.data || [];
          this.enrichBookingsWithCarDetails();
          this.calculateStats();
          this.applyFilters();
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Failed to load bookings';
          this.isLoading = false;
        }
      });
  }

  /**
   * Load all cars for car details enrichment
   */
  loadCars(): void {
    this.carService.getAllCars()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          const carList = response.data || [];
          carList.forEach(car => {
            this.cars.set(car.id!, car);
          });
          this.enrichBookingsWithCarDetails();
        },
        error: (error) => {
          console.error('Failed to load cars', error);
        }
      });
  }

  /**
   * Enrich bookings with car details for display
   */
  enrichBookingsWithCarDetails(): void {
    this.bookings.forEach(booking => {
      if (booking.carId && this.cars.has(booking.carId)) {
        booking.carDetails = this.cars.get(booking.carId);
      }
    });
  }

  /**
   * Calculate booking statistics
   */
  calculateStats(): void {
    this.stats = {
      totalBookings: this.bookings.length,
      pendingBookings: this.bookings.filter(b => b.bookingStatus === 'PENDING').length,
      confirmedBookings: this.bookings.filter(b => b.bookingStatus === 'CONFIRMED').length,
      activeBookings: this.bookings.filter(b => b.bookingStatus === 'ACTIVE').length,
      completedBookings: this.bookings.filter(b => b.bookingStatus === 'COMPLETED').length,
      cancelledBookings: this.bookings.filter(b => b.bookingStatus === 'CANCELLED').length,
      totalRevenue: this.bookings
        .filter(b => b.bookingStatus === 'COMPLETED')
        .reduce((sum, b) => sum + (b.totalCost || 0), 0)
    };
  }

  /**
   * Apply all active filters to bookings
   */
  applyFilters(): void {
    let filtered = [...this.bookings];

    // Filter by status
    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(b => b.bookingStatus === this.selectedStatus);
    }

    // Filter by car
    if (this.selectedCarId !== null) {
      filtered = filtered.filter(b => b.carId === this.selectedCarId);
    }

    // Filter by search term (booking number or user id)
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(b =>
        b.bookingNumber?.toLowerCase().includes(term) ||
        b.userId?.toString().includes(term)
      );
    }

    // Filter by date range
    if (this.dateRangeStart) {
      const startDate = new Date(this.dateRangeStart);
      filtered = filtered.filter(b => new Date(b.pickupDate!) >= startDate);
    }

    if (this.dateRangeEnd) {
      const endDate = new Date(this.dateRangeEnd);
      filtered = filtered.filter(b => new Date(b.dropoffDate!) <= endDate);
    }

    // Sort
    this.sortBookings(filtered);

    this.filteredBookings = filtered;
    this.currentPage = 1;
    this.updatePagination();
  }

  /**
   * Sort bookings by selected field
   */
  sortBookings(bookings: BookingWithCar[]): void {
    bookings.sort((a, b) => {
      let aVal: any;
      let bVal: any;

      switch (this.sortBy) {
        case 'bookingNumber':
          aVal = a.bookingNumber || '';
          bVal = b.bookingNumber || '';
          break;
        case 'userId':
          aVal = a.userId || 0;
          bVal = b.userId || 0;
          break;
        case 'pickupDate':
          aVal = new Date(a.pickupDate!).getTime();
          bVal = new Date(b.pickupDate!).getTime();
          break;
        case 'totalCost':
          aVal = a.totalCost || 0;
          bVal = b.totalCost || 0;
          break;
        case 'bookingStatus':
          aVal = a.bookingStatus || '';
          bVal = b.bookingStatus || '';
          break;
        default:
          aVal = new Date(a.createdAt!).getTime();
          bVal = new Date(b.createdAt!).getTime();
      }

      if (aVal < bVal) return this.sortOrder === 'asc' ? -1 : 1;
      if (aVal > bVal) return this.sortOrder === 'asc' ? 1 : -1;
      return 0;
    });
  }

  /**
   * Update pagination based on filtered bookings
   */
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredBookings.length / this.pageSize);
    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages || 1;
    }

    const startIdx = (this.currentPage - 1) * this.pageSize;
    const endIdx = startIdx + this.pageSize;
    this.paginatedBookings = this.filteredBookings.slice(startIdx, endIdx);
  }

  /**
   * Handle status filter change
   */
  onStatusFilterChange(): void {
    this.applyFilters();
  }

  /**
   * Handle search input
   */
  onSearchChange(): void {
    this.applyFilters();
  }

  /**
   * Handle date range change
   */
  onDateRangeChange(): void {
    this.applyFilters();
  }

  /**
   * Handle car filter change
   */
  onCarFilterChange(): void {
    this.applyFilters();
  }

  /**
   * Handle sort change
   */
  onSortChange(field: string): void {
    if (this.sortBy === field) {
      this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = field;
      this.sortOrder = 'desc';
    }
    this.applyFilters();
  }

  /**
   * Change page
   */
  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  /**
   * Clear all filters
   */
  clearFilters(): void {
    this.selectedStatus = 'ALL';
    this.searchTerm = '';
    this.dateRangeStart = '';
    this.dateRangeEnd = '';
    this.selectedCarId = null;
    this.sortBy = 'createdAt';
    this.sortOrder = 'desc';
    this.applyFilters();
  }

  /**
   * Confirm a pending booking
   */
  confirmBooking(bookingId: number): void {
    if (!confirm('Are you sure you want to confirm this booking?')) {
      return;
    }

    this.actionInProgress = true;
    this.bookingService.confirmBooking(bookingId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Booking confirmed successfully';
          this.loadBookings();
          setTimeout(() => this.successMessage = '', 3000);
          this.actionInProgress = false;
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Failed to confirm booking';
          this.actionInProgress = false;
        }
      });
  }

  /**
   * Activate a confirmed booking
   */
  activateBooking(bookingId: number): void {
    if (!confirm('Are you sure you want to activate this booking?')) {
      return;
    }

    this.actionInProgress = true;
    this.bookingService.activateBooking(bookingId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Booking activated successfully';
          this.loadBookings();
          setTimeout(() => this.successMessage = '', 3000);
          this.actionInProgress = false;
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Failed to activate booking';
          this.actionInProgress = false;
        }
      });
  }

  /**
   * Complete an active booking
   */
  completeBooking(bookingId: number): void {
    if (!confirm('Are you sure you want to complete this booking?')) {
      return;
    }

    this.actionInProgress = true;
    this.bookingService.completeBooking(bookingId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Booking completed successfully';
          this.loadBookings();
          setTimeout(() => this.successMessage = '', 3000);
          this.actionInProgress = false;
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Failed to complete booking';
          this.actionInProgress = false;
        }
      });
  }

  /**
   * Cancel a booking
   */
  cancelBooking(bookingId: number): void {
    if (!confirm('Are you sure you want to cancel this booking?')) {
      return;
    }

    this.actionInProgress = true;
    this.bookingService.cancelBooking(bookingId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Booking cancelled successfully';
          this.loadBookings();
          setTimeout(() => this.successMessage = '', 3000);
          this.actionInProgress = false;
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Failed to cancel booking';
          this.actionInProgress = false;
        }
      });
  }

  /**
   * Export bookings to CSV
   */
  exportToCSV(): void {
    const headers = ['Booking #', 'User ID', 'Car', 'Pickup Date', 'Dropoff Date', 'Status', 'Total Cost'];
    const rows = this.filteredBookings.map(b => [
      b.bookingNumber,
      b.userId,
      b.carDetails ? `${b.carDetails.make} ${b.carDetails.model}` : 'N/A',
      new Date(b.pickupDate!).toLocaleDateString(),
      new Date(b.dropoffDate!).toLocaleDateString(),
      b.bookingStatus,
      `$${b.totalCost?.toFixed(2)}`
    ]);

    let csvContent = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `bookings-${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);

    this.successMessage = 'Bookings exported to CSV';
    setTimeout(() => this.successMessage = '', 3000);
  }

  /**
   * Refresh data
   */
  refreshData(): void {
    this.loadBookings();
  }

  /**
   * Get status badge class
   */
  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'badge-warning';
      case 'CONFIRMED':
        return 'badge-info';
      case 'ACTIVE':
        return 'badge-primary';
      case 'COMPLETED':
        return 'badge-success';
      case 'CANCELLED':
        return 'badge-danger';
      default:
        return 'badge-secondary';
    }
  }

  /**
   * Check if booking can transition to next status
   */
  canTransition(booking: Booking): string[] {
    const transitions: { [key: string]: string[] } = {
      'PENDING': ['CONFIRMED', 'CANCELLED'],
      'CONFIRMED': ['ACTIVE', 'CANCELLED'],
      'ACTIVE': ['COMPLETED'],
      'COMPLETED': [],
      'CANCELLED': []
    };
    return transitions[booking.bookingStatus!] || [];
  }

  /**
   * Format currency
   */
  formatCurrency(amount: number | undefined): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount || 0);
  }

  /**
   * Format date
   */
  formatDate(date: string | Date | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}