import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BookingService } from '../../services/booking.service';
import { Booking, BookingAvailabilityResponse } from '../../../../core/models/booking.model';
import { Car } from '../../../../core/models/car.model';

/**
 * Booking Form Component
 * Handles booking creation and date selection with availability checking
 */
@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './booking-form.component.html',
  styleUrl: './booking-form.component.scss'
})
export class BookingFormComponent implements OnInit {
  @Input() car: Car | null = null;
  @Input() userId: number | null = null;
  @Output() bookingCreated = new EventEmitter<Booking>();
  @Output() formSubmitted = new EventEmitter<void>();

  bookingForm!: FormGroup;
  loading = false;
  error: string | null = null;
  success: string | null = null;
  availabilityInfo: BookingAvailabilityResponse | null = null;
  checkingAvailability = false;

  constructor(
    private fb: FormBuilder,
    private bookingService: BookingService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.bookingForm = this.fb.group({
      pickupDate: ['', [Validators.required]],
      dropoffDate: ['', [Validators.required]],
      pickupLocationId: ['', [Validators.required]],
      dropoffLocationId: ['', [Validators.required]],
      notes: ['']
    });
  }

  /**
   * Check availability when dates change
   */
  checkAvailability(): void {
    if (!this.car || !this.bookingForm.get('pickupDate')?.value || !this.bookingForm.get('dropoffDate')?.value) {
      return;
    }

    const pickupDate = this.bookingForm.get('pickupDate')?.value;
    const dropoffDate = this.bookingForm.get('dropoffDate')?.value;

    // Validate dates
    if (new Date(dropoffDate) <= new Date(pickupDate)) {
      this.error = 'Drop-off date must be after pick-up date';
      this.availabilityInfo = null;
      return;
    }

    if (new Date(pickupDate) < new Date()) {
      this.error = 'Pick-up date cannot be in the past';
      this.availabilityInfo = null;
      return;
    }

    this.error = null;
    this.checkingAvailability = true;

    this.bookingService.checkAvailability(
      this.car.id,
      pickupDate,
      dropoffDate
    ).subscribe({
      next: (response) => {
        this.availabilityInfo = response.data;
        if (!response.data.available) {
          this.error = 'This vehicle is not available for the selected dates';
        }
        this.checkingAvailability = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error checking availability';
        this.availabilityInfo = null;
        this.checkingAvailability = false;
      }
    });
  }

  /**
   * Submit the booking form
   */
  onSubmit(): void {
    if (!this.bookingForm.valid || !this.car || !this.availabilityInfo?.available) {
      this.error = 'Please complete the form and check availability';
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    const booking: Booking = {
      carId: this.car.id,
      pickupDate: this.bookingForm.get('pickupDate')?.value,
      dropoffDate: this.bookingForm.get('dropoffDate')?.value,
      pickupLocationId: this.bookingForm.get('pickupLocationId')?.value,
      dropoffLocationId: this.bookingForm.get('dropoffLocationId')?.value,
      notes: this.bookingForm.get('notes')?.value,
      bookingNumber: ''
    };

    this.bookingService.createBooking(booking).subscribe({
      next: (response) => {
        this.success = 'Booking created successfully!';
        this.bookingCreated.emit(response.data);
        this.bookingForm.reset();
        this.availabilityInfo = null;
        this.loading = false;
        this.formSubmitted.emit();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error creating booking';
        this.loading = false;
      }
    });
  }

  /**
   * Get minimum date (today)
   */
  getMinDate(): string {
    return new Date().toISOString().split('T')[0];
  }
}
