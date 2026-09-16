/**
 * Booking model
 */
export interface Booking {
  id?: number;
  bookingNumber: string;
  carId: number;
  userId?: number;
  pickupDate: string;
  dropoffDate: string;
  pickupLocationId: number;
  dropoffLocationId: number;
  totalCost?: number;
  bookingStatus?: string; // PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BookingAvailabilityRequest {
  carId: number;
  pickupDate: string;
  dropoffDate: string;
}

export interface BookingAvailabilityResponse {
  available: boolean;
  rentalDays: number;
  totalCost?: number;
}
