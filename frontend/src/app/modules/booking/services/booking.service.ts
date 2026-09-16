import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Booking, BookingAvailabilityRequest, BookingAvailabilityResponse } from '../../../core/models/booking.model';
import { ApiResponse } from '../../../core/models/api-response.model';

/**
 * Service for managing bookings
 */
@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private readonly apiUrl = `${environment.apiUrl}/bookings`;

  constructor(private http: HttpClient) { }

  /**
   * Get all bookings (admin only)
   */
  getAllBookings(): Observable<ApiResponse<Booking[]>> {
    return this.http.get<ApiResponse<Booking[]>>(this.apiUrl);
  }

  /**
   * Get booking by ID
   */
  getBookingById(id: number): Observable<ApiResponse<Booking>> {
    return this.http.get<ApiResponse<Booking>>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get all bookings for current user
   */
  getMyBookings(): Observable<ApiResponse<Booking[]>> {
    // This would need the userId from auth service
    return this.http.get<ApiResponse<Booking[]>>(`${this.apiUrl}/user/current`);
  }

  /**
   * Get bookings by user ID
   */
  getBookingsByUserId(userId: number): Observable<ApiResponse<Booking[]>> {
    return this.http.get<ApiResponse<Booking[]>>(`${this.apiUrl}/user/${userId}`);
  }

  /**
   * Get bookings by user ID and status
   */
  getBookingsByUserIdAndStatus(userId: number, status: string): Observable<ApiResponse<Booking[]>> {
    return this.http.get<ApiResponse<Booking[]>>(`${this.apiUrl}/user/${userId}/status/${status}`);
  }

  /**
   * Get bookings by car ID
   */
  getBookingsByCarId(carId: number): Observable<ApiResponse<Booking[]>> {
    return this.http.get<ApiResponse<Booking[]>>(`${this.apiUrl}/car/${carId}`);
  }

  /**
   * Get bookings by status
   */
  getBookingsByStatus(status: string): Observable<ApiResponse<Booking[]>> {
    return this.http.get<ApiResponse<Booking[]>>(`${this.apiUrl}/status/${status}`);
  }

  /**
   * Check if a car is available for the requested dates
   */
  checkAvailability(carId: number, pickupDate: string, dropoffDate: string): Observable<ApiResponse<BookingAvailabilityResponse>> {
    const params = {
      carId: carId.toString(),
      pickupDate: pickupDate,
      dropoffDate: dropoffDate
    };
    return this.http.get<ApiResponse<BookingAvailabilityResponse>>(`${this.apiUrl}/availability/check`, { params });
  }

  /**
   * Create a new booking
   */
  createBooking(booking: Booking): Observable<ApiResponse<Booking>> {
    return this.http.post<ApiResponse<Booking>>(this.apiUrl, booking);
  }

  /**
   * Update a booking (PENDING only)
   */
  updateBooking(id: number, booking: Booking): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.apiUrl}/${id}`, booking);
  }

  /**
   * Confirm a booking (PENDING to CONFIRMED)
   */
  confirmBooking(id: number): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.apiUrl}/${id}/confirm`, {});
  }

  /**
   * Activate a booking (CONFIRMED to ACTIVE)
   */
  activateBooking(id: number): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.apiUrl}/${id}/activate`, {});
  }

  /**
   * Complete a booking (ACTIVE to COMPLETED)
   */
  completeBooking(id: number): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.apiUrl}/${id}/complete`, {});
  }

  /**
   * Cancel a booking
   */
  cancelBooking(id: number): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.apiUrl}/${id}/cancel`, {});
  }

  /**
   * Delete a booking (CANCELLED only)
   */
  deleteBooking(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
