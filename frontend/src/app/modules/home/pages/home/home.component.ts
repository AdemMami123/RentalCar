import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CarService } from '../../../car/services/car.service';
import { Car } from '../../../../core/models/car.model';

interface Category {
  type: string;
  name: string;
  description: string;
  count: number;
  image?: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  pickupLocation = 'Tunis Centre';
  pickupDate = '';
  returnDate = '';
  category = 'All categories';
  searchMessage = '';
  cars: Car[] = [];
  featuredCars: Car[] = [];
  categories: Category[] = [];
  loading = true;
  error = '';

  constructor(private carService: CarService, private router: Router) {}

  ngOnInit(): void {
    this.carService.getAllCars().subscribe({
      next: response => {
        this.cars = response.data || [];
        this.featuredCars = this.cars.slice(0, 3);
        this.categories = this.buildCategories(this.cars);
        this.loading = false;
      },
      error: error => {
        this.error = error.error?.message || 'Unable to load the fleet.';
        this.loading = false;
      }
    });
  }

  private buildCategories(cars: Car[]): Category[] {
    const grouped = new Map<string, Car[]>();
    cars.forEach(car => {
      if (!car.carType) return;
      const type = car.carType.toUpperCase();
      grouped.set(type, [...(grouped.get(type) || []), car]);
    });

    return [...grouped.entries()].map(([type, carsInCategory]) => ({
      type,
      name: this.displayType(type),
      description: `${this.displayType(type)} cars for your next journey`,
      count: carsInCategory.length,
      image: carsInCategory.find(car => car.imageUrls?.[0])?.imageUrls?.[0]
    }));
  }

  private displayType(type: string): string {
    return type.charAt(0) + type.slice(1).toLowerCase();
  }

  submitSearch(): void {
    if (this.category !== 'All categories') {
      this.router.navigate(['/cars'], { queryParams: { type: this.category } });
      return;
    }
    this.searchMessage = this.pickupDate && this.returnDate ? `Showing cars near ${this.pickupLocation} for your dates.` : 'Choose both dates to see the best available cars.';
  }
}