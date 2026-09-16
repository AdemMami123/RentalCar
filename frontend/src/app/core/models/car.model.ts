/**
 * Car model
 */
export interface Car {
  id: number;
  make: string;
  model: string;
  year: number;
  registrationNumber: string;
  licensePlate: string;
  vin: string;
  carType: string;
  seats: number;
  transmission?: string;
  fuelType?: string;
  dailyRate: number;
  status?: string;
  color?: string;
  mileage?: number;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}
