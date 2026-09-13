/**
 * Booking model
 */
export interface Booking {
  id: number;
  carId: number;
  userId: number;
  pickupDate: string;
  dropoffDate: string;
  totalCost: number;
  status: string;
  createdAt: string;
  updatedAt: string;
}
