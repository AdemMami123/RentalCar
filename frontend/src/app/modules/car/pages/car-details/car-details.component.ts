import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CarService } from '../../services/car.service';
import { BookingFormComponent } from '../../../booking/components/booking-form/booking-form.component';
import { Car } from '../../../../core/models/car.model';
import { Booking } from '../../../../core/models/booking.model';

@Component({
  selector: 'app-car-details',
  standalone: true,
  imports: [CommonModule, RouterLink, BookingFormComponent],
  templateUrl: './car-details.component.html',
  styleUrl: './car-details.component.scss'
})
export class CarDetailsComponent implements OnInit {
  car: Car | null = null;
  loading = true;
  error: string | null = null;
  bookingSuccessful = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private carService: CarService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const carId = params['id'];
      if (carId) {
        this.loadCarDetails(carId);
      }
    });
  }

  loadCarDetails(carId: number): void {
    this.loading = true;
    this.error = null;

    this.carService.getCarById(carId).subscribe({
      next: (response) => {
        this.car = response.data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error loading car details';
        this.loading = false;
      }
    });
  }

  onBookingCreated(booking: Booking): void {
    this.bookingSuccessful = true;
    
    // Redirect to booking confirmation page after a short delay
    setTimeout(() => {
      this.router.navigate(['/bookings', booking.id]);
    }, 2000);
  }

  handleFormSubmitted(): void {
    // Handle form submission if needed
  }
}