import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getAccessToken();
    const isAuthRequest = request.url.includes('/auth/login') || request.url.includes('/auth/register') || request.url.includes('/auth/refresh') || request.url.includes('/auth/logout');
    const requestWithToken = token && !isAuthRequest ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : request;

    return next.handle(requestWithToken).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status !== 401 || isAuthRequest || !this.authService.getRefreshToken()) return throwError(() => error);
        return this.authService.refreshToken().pipe(
          switchMap(response => next.handle(request.clone({ setHeaders: { Authorization: `Bearer ${response.data.accessToken}` } }))),
          catchError(refreshError => { this.authService.clearAuthentication(); return throwError(() => refreshError); })
        );
      })
    );
  }
}