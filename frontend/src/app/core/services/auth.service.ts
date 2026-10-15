import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { IdentityDocument, User } from '../models/user.model';

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

  uploadProfileImage(image: File): Observable<ApiResponse<User>> {
    const formData = new FormData();
    formData.append('image', image, image.name);
    return this.http.post<ApiResponse<User>>(`${environment.apiUrl}/profile/image`, formData).pipe(
      tap(response => {
        sessionStorage.setItem('rentalcar_user', JSON.stringify(response.data));
        this.userSubject.next(response.data);
      })
    );
  }

  updateProfile(profile: User): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${environment.apiUrl}/profile`, profile).pipe(
      tap(response => {
        sessionStorage.setItem('rentalcar_user', JSON.stringify(response.data));
        this.userSubject.next(response.data);
      })
    );
  }

  uploadIdentityDocument(type: 'DRIVERS_LICENSE' | 'IDENTIFICATION', document: File): Observable<ApiResponse<IdentityDocument>> {
    const formData = new FormData();
    formData.append('type', type);
    formData.append('document', document, document.name);
    return this.http.post<ApiResponse<IdentityDocument>>(`${environment.apiUrl}/profile/documents`, formData);
  }

  getIdentityDocuments(): Observable<ApiResponse<IdentityDocument[]>> {
    return this.http.get<ApiResponse<IdentityDocument[]>>(`${environment.apiUrl}/profile/documents`);
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
  getCurrentUser(): User | null { return this.userSubject.value; }

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