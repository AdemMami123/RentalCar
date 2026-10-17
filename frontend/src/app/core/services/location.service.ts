import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Location } from '../models/location.model';

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  private readonly apiUrl = `${environment.apiUrl}/locations`;

  constructor(private http: HttpClient) { }

  getLocations(): Observable<ApiResponse<Location[]>> {
    return this.http.get<ApiResponse<Location[]>>(this.apiUrl);
  }

  getActiveLocations(): Observable<ApiResponse<Location[]>> {
    return this.http.get<ApiResponse<Location[]>>(`${this.apiUrl}/active`);
  }

  createLocation(location: Location): Observable<ApiResponse<Location>> {
    return this.http.post<ApiResponse<Location>>(this.apiUrl, location);
  }

  updateLocation(id: number, location: Location): Observable<ApiResponse<Location>> {
    return this.http.put<ApiResponse<Location>>(`${this.apiUrl}/${id}`, location);
  }

  deleteLocation(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
