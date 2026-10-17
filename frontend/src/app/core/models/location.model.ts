/**
 * Location model
 */
export interface Location {
  id: number;
  name: string;
  address?: string;
  city?: string;
  country?: string;
  createdAt?: string;
  updatedAt?: string;
  latitude?: number;
  longitude?: number;
  phone?: string;
  email?: string;
  openingTime?: string;
  closingTime?: string;
  locationType?: 'OFFICE' | 'AIRPORT' | 'OTHER';
  pickupEnabled?: boolean;
  returnEnabled?: boolean;
  deliveryEnabled?: boolean;
  active?: boolean;
  timezone?: string;
}
