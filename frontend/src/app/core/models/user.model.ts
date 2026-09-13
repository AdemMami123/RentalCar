/**
 * User model
 */
export interface User {
  id: number;
  email: string;
  role?: string;
  phone?: string;
  firstName: string;
  lastName: string;
  licenseNumber: string;
  licenseExpiry: string;
  createdAt: string;
  updatedAt: string;
}
