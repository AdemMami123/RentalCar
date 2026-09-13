import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

interface FeaturedCar {
  brand: string;
  model: string;
  year: number;
  category: string;
  transmission: string;
  fuel: string;
  seats: number;
  rating: number;
  price: number;
  image: string;
  accent: string;
}

interface Category {
  name: string;
  description: string;
  count: number;
  image: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  pickupLocation = 'Tunis Centre';
  pickupDate = '';
  returnDate = '';
  category = 'All categories';
  searchMessage = '';

  readonly featuredCars: FeaturedCar[] = [
    { brand: 'BMW', model: 'M4 Competition', year: 2024, category: 'Sports', transmission: 'Automatic', fuel: 'Petrol', seats: 4, rating: 4.9, price: 145, image: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&w=1000&q=85', accent: 'performance' },
    { brand: 'Mercedes-Benz', model: 'E-Class Sedan', year: 2024, category: 'Luxury', transmission: 'Automatic', fuel: 'Hybrid', seats: 5, rating: 4.8, price: 118, image: 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&w=1000&q=85', accent: 'executive' },
    { brand: 'Audi', model: 'Q5 S line', year: 2023, category: 'SUV', transmission: 'Automatic', fuel: 'Petrol', seats: 5, rating: 4.7, price: 96, image: 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?auto=format&fit=crop&w=1000&q=85', accent: 'adventure' }
  ];

  readonly categories: Category[] = [
    { name: 'Economy', description: 'Smart city travel', count: 24, image: 'https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&w=700&q=80' },
    { name: 'SUV', description: 'Room for every route', count: 18, image: 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=700&q=80' },
    { name: 'Luxury', description: 'Arrive in distinction', count: 12, image: 'https://images.unsplash.com/photo-1563720223185-11003d516935?auto=format&fit=crop&w=700&q=80' },
    { name: 'Electric', description: 'Quietly go further', count: 9, image: 'https://images.unsplash.com/photo-1593941707882-a5bba14938c7?auto=format&fit=crop&w=700&q=80' }
  ];

  submitSearch(): void {
    this.searchMessage = this.pickupDate && this.returnDate ? `Showing cars near ${this.pickupLocation} for your dates.` : 'Choose both dates to see the best available cars.';
  }
}