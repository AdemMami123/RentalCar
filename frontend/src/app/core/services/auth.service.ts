import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private readonly accessTokenKey = 'rentalcar_access_token';
  private readonly refreshTokenKey = 'rentalcar_refresh_token';
  private readonly userSubject = new BehaviorSubject<User | null>(this.readUser());
  readonly user$ = this.userSubject.asObservable();

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  register(request: RegisterRequest): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.baseUrl}/register`, request);
  }

  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, request).pipe(
      tap(response => this.storeAuthentication(response.data))
    );
  }

  refreshToken(): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/refresh`, {
      refreshToken: this.getRefreshToken()
    }).pipe(tap(response => this.storeAuthentication(response.data)));
  }

  loadCurrentUser(): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.baseUrl}/me`).pipe(
      tap(response => this.userSubject.next(response.data))
    );
  }

  logout(): void {
    const refreshToken = this.getRefreshToken();
    if (refreshToken) {
      this.http.post(`${this.baseUrl}/logout`, { refreshToken }).subscribe({ next: () => this.clearAuthentication(), error: () => this.clearAuthentication() });
    } else {
      this.clearAuthentication();
    }
  }

  getAccessToken(): string | null { return sessionStorage.getItem(this.accessTokenKey); }
  getRefreshToken(): string | null { return sessionStorage.getItem(this.refreshTokenKey); }
  isAuthenticated(): boolean { return !!this.getAccessToken(); }
  isAdmin(): boolean { return this.userSubject.value?.role === 'ADMIN'; }

  clearAuthentication(): void {
    sessionStorage.removeItem(this.accessTokenKey);
    sessionStorage.removeItem(this.refreshTokenKey);
    sessionStorage.removeItem('rentalcar_user');
    this.userSubject.next(null);
    void this.router.navigate(['/login']);
  }

  private storeAuthentication(authentication: AuthResponse): void {
    sessionStorage.setItem(this.accessTokenKey, authentication.accessToken);
    sessionStorage.setItem(this.refreshTokenKey, authentication.refreshToken);
    sessionStorage.setItem('rentalcar_user', JSON.stringify(authentication.user));
    this.userSubject.next(authentication.user);
  }

  private readUser(): User | null {
    const value = sessionStorage.getItem('rentalcar_user');
    try { return value ? JSON.parse(value) as User : null; } catch { return null; }
  }
}