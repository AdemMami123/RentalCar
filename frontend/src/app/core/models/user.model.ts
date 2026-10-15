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
  profileImageUrl?: string;
  licenseNumber: string;
  licenseExpiry: string;
  createdAt: string;
  updatedAt: string;
  address?: string;
  city?: string;
  country?: string;
}

export interface IdentityDocument {
  id: number;
  documentType: string;
  originalFilename: string;
  contentType: string;
  uploadedAt: string;
}
