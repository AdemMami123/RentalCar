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
}
