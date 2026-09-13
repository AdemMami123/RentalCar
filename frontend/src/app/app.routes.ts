import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('./modules/car/pages/car-list/car-list.component')
      .then(m => m.CarListComponent)
  },
  {
    path: 'cars',
    loadComponent: () => import('./modules/car/pages/car-list/car-list.component')
      .then(m => m.CarListComponent)
  },
  {
    path: 'cars/:id',
    loadComponent: () => import('./modules/car/pages/car-details/car-details.component')
      .then(m => m.CarDetailsComponent)
  },
  {
    path: 'bookings',
    loadComponent: () => import('./modules/booking/pages/booking-list/booking-list.component')
      .then(m => m.BookingListComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./modules/user/pages/login/login.component')
      .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./modules/user/pages/register/register.component')
      .then(m => m.RegisterComponent)
  },
  {
    path: 'admin',
    loadComponent: () => import('./modules/admin/pages/admin-dashboard/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent)
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
