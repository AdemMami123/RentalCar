import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../core/models/api-response.model';
import { InspectionComparison, VehicleInspection } from '../../../core/models/inspection.model';

@Injectable({ providedIn: 'root' })
export class InspectionService {
  private readonly apiUrl = `${environment.apiUrl}/inspections`;

  constructor(private readonly http: HttpClient) {}

  createDraft(inspection: VehicleInspection): Observable<ApiResponse<VehicleInspection>> {
    return this.http.post<ApiResponse<VehicleInspection>>(this.apiUrl, inspection);
  }

  complete(id: number): Observable<ApiResponse<VehicleInspection>> {
    return this.http.put<ApiResponse<VehicleInspection>>(`${this.apiUrl}/${id}/complete`, {});
  }

  uploadPhoto(id: number, file: File, category: string, description?: string, damageId?: number): Observable<ApiResponse<VehicleInspection>> {
    const form = new FormData();
    form.append('file', file, file.name);
    form.append('category', category);
    if (description) form.append('description', description);
    if (damageId !== undefined) form.append('damageId', String(damageId));
    return this.http.post<ApiResponse<VehicleInspection>>(`${this.apiUrl}/${id}/photos`, form);
  }

  get(id: number): Observable<ApiResponse<VehicleInspection>> {
    return this.http.get<ApiResponse<VehicleInspection>>(`${this.apiUrl}/${id}`);
  }

  compare(bookingId: number): Observable<ApiResponse<InspectionComparison>> {
    return this.http.get<ApiResponse<InspectionComparison>>(`${this.apiUrl}/bookings/${bookingId}/comparison`);
  }
}
