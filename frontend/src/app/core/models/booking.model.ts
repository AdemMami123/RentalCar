/**
 * Booking model
 */
export interface Booking {
  id?: number;
  bookingNumber?: string;
  carId: number;
  userId?: number;
  customer?: BookingCustomer;
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

export interface BookingCustomer {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  licenseNumber?: string;
  licenseExpiry?: string;
  address?: string;
  city?: string;
  country?: string;
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
  totalCarCount: number;
  availableCarCount: number;
  suggestedDateRanges?: BookingDateRangeSuggestion[];
}

export interface BookingDateRangeSuggestion {
  pickupDate: string;
  dropoffDate: string;
}
