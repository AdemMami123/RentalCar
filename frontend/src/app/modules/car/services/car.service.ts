import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Car } from '../../../core/models/car.model';
import { ApiResponse } from '../../../core/models/api-response.model';

/**
 * Car service for car-related operations
 */
@Injectable({
  providedIn: 'root'
})
export class CarService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getAllCars(): Observable<ApiResponse<Car[]>> {
    return this.http.get<ApiResponse<Car[]>>(`${this.apiUrl}/cars`);
  }

  getCarById(id: number): Observable<ApiResponse<Car>> {
    return this.http.get<ApiResponse<Car>>(`${this.apiUrl}/cars/${id}`);
  }

  createCar(car: Car): Observable<ApiResponse<Car>> {
    return this.http.post<ApiResponse<Car>>(`${this.apiUrl}/cars`, car);
  }

  updateCar(id: number, car: Car): Observable<ApiResponse<Car>> {
    return this.http.put<ApiResponse<Car>>(`${this.apiUrl}/cars/${id}`, car);
  }

  deleteCar(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/cars/${id}`);
  }

  updateCarStatus(id: number, status: string): Observable<ApiResponse<Car>> {
    return this.http.patch<ApiResponse<Car>>(`${this.apiUrl}/cars/${id}/status`, null, {
      params: { status }
    });
  }
}
