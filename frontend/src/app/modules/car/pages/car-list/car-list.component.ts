import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CarService } from '../../services/car.service';
import { Car } from '../../../../core/models/car.model';

@Component({
	selector: 'app-car-list',
	standalone: true,
	imports: [CommonModule, FormsModule, RouterLink],
	templateUrl: './car-list.component.html',
	styleUrl: './car-list.component.scss'
})
export class CarListComponent implements OnInit {
	cars: Car[] = [];
	filteredCars: Car[] = [];
	searchTerm = '';
	selectedType = 'ALL';
	loading = true;
	error = '';

	constructor(private carService: CarService) {}

	ngOnInit(): void {
		this.carService.getAllCars().subscribe({
			next: response => {
				this.cars = response.data || [];
				this.applyFilters();
				this.loading = false;
			},
			error: error => {
				this.error = error.error?.message || 'Unable to load the fleet.';
				this.loading = false;
			}
		});
	}

	applyFilters(): void {
		const term = this.searchTerm.trim().toLowerCase();
		this.filteredCars = this.cars.filter(car =>
			(this.selectedType === 'ALL' || car.carType === this.selectedType) &&
			(!term || `${car.make} ${car.model}`.toLowerCase().includes(term))
		);
	}

	carTypes(): string[] {
		return [...new Set(this.cars.map(car => car.carType).filter(Boolean))];
	}
}