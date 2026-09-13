/**
 * Car model
 */
export interface Car {
  id: number;
  make: string;
  model: string;
  year: number;
  registrationNumber: string;
  dailyRate: number;
  available: boolean;
  createdAt: string;
  updatedAt: string;
}
