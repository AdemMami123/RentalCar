# Car Rental Frontend

Angular frontend for the Car Rental application.

## Prerequisites

- Node.js 18+ and npm 9+
- Angular CLI 18+
- Visual Studio Code (recommended)

## Setup

### 1. Install Dependencies
```bash
npm install
```

### 2. Configuration
Edit `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### 3. Start Development Server
```bash
npm start
```

Application will be available at `http://localhost:4200`

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/                       # Core services & guards
│   │   │   ├── services/               # Singleton services
│   │   │   ├── guards/                 # Route guards
│   │   │   ├── interceptors/           # HTTP interceptors
│   │   │   └── models/                 # Core models
│   │   ├── shared/                     # Shared components
│   │   │   ├── components/             # Reusable components
│   │   │   ├── pipes/                  # Custom pipes
│   │   │   ├── directives/             # Custom directives
│   │   │   └── services/               # Shared services
│   │   ├── layout/                     # Layout components
│   │   │   ├── header/
│   │   │   ├── footer/
│   │   │   └── sidebar/
│   │   ├── modules/                    # Feature modules
│   │   │   ├── car/                    # Car management
│   │   │   │   ├── pages/
│   │   │   │   ├── components/
│   │   │   │   ├── services/
│   │   │   │   └── models/
│   │   │   ├── booking/                # Booking management
│   │   │   ├── payment/                # Payment handling
│   │   │   ├── user/                   # Authentication & profile
│   │   │   └── admin/                  # Admin dashboard
│   │   ├── app.component.ts            # Root component
│   │   ├── app.config.ts               # App configuration
│   │   └── app.routes.ts               # Routing configuration
│   ├── assets/                         # Static assets
│   ├── environments/                   # Environment configs
│   ├── index.html                      # HTML template
│   ├── main.ts                         # Application entry point
│   └── styles.scss                     # Global styles
├── package.json
├── angular.json
├── tsconfig.json
└── README.md
```

## Available Scripts

### Development
```bash
npm start              # Start dev server
npm run build          # Build for development
npm run watch          # Build with watch mode
```

### Production
```bash
npm run build:prod     # Production build
```

### Testing
```bash
npm test               # Run tests
npm run lint           # Run linter
```

## Features

### Car Module
- List available cars
- View car details
- Search and filter
- Availability calendar

### Booking Module
- Create bookings
- View booking history
- Modify bookings
- Cancel bookings

### Payment Module
- Process payments
- View invoices
- Payment history

### User Module
- User registration
- Login/Logout
- Profile management
- License management

### Admin Module
- Dashboard with analytics
- Car management
- User management
- Booking management

## Code Style

- Follow Angular style guide
- Use TypeScript strict mode
- Use RxJS reactive patterns
- Implement OnDestroy for cleanup

## Lazy Loading

Feature modules are lazy-loaded for better performance:
```typescript
{
  path: 'cars',
  loadComponent: () => import('./modules/car/pages/car-list/car-list.component')
    .then(m => m.CarListComponent)
}
```

## HTTP Interceptors

Global interceptors for:
- Authorization header injection
- Error handling
- Loading state management

## Authentication

JWT token-based authentication:
- Stored in localStorage
- Automatically attached to requests
- Refreshed on expiry

## Styling

- SCSS for component styles
- Global styles in `styles.scss`
- CSS custom properties for theming
- Responsive design with media queries

## Build & Deployment

### Development Build
```bash
ng build
```

### Production Build
```bash
ng build --configuration production
```

### Deploy to Server
```bash
# Build production bundle
npm run build:prod

# Copy dist folder to your web server
cp -r dist/car-rental-frontend /var/www/
```

## Troubleshooting

### Port Already in Use
```bash
ng serve --port 4201
```

### Module Not Found
```bash
npm install
```

### Build Errors
```bash
rm -rf node_modules dist
npm install
npm run build
```

## Performance Optimization

- Lazy loading feature modules
- Change detection strategy: OnPush
- Unsubscribe in ngOnDestroy
- Use trackBy in *ngFor
- Minimize bundle size

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

MIT

## Support

For issues, please open a GitHub issue or contact the development team.
