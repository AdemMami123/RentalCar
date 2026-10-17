export type InspectionType = 'PICKUP' | 'RETURN';
export type InspectionStatus = 'DRAFT' | 'COMPLETED';

export interface InspectionDamage {
  id?: number;
  damageType: string;
  locationOnVehicle: string;
  description: string;
  severity: string;
}

export interface InspectionPhoto {
  id?: number;
  damageId?: number;
  photoUrl?: string;
  storageKey?: string;
  category: string;
  description?: string;
  uploadedAt?: string;
}

export interface VehicleInspection {
  id?: number;
  bookingId: number;
  carId?: number;
  inspectionType: InspectionType;
  status?: InspectionStatus;
  inspectedBy?: number;
  inspectedAt?: string;
  odometerReading: number;
  fuelLevel: number;
  exteriorCondition: string;
  interiorCondition: string;
  notes?: string;
  customerAcknowledgedAt?: string;
  damages: InspectionDamage[];
  photos?: InspectionPhoto[];
}

export interface InspectionComparison {
  bookingId: number;
  pickupOdometer: number;
  returnOdometer: number;
  distanceDriven: number;
  pickupFuelLevel: number;
  returnFuelLevel: number;
  fuelDifference: number;
  existingDamage: InspectionDamage[];
  potentialNewDamage: InspectionDamage[];
  hasDiscrepancies: boolean;
}
