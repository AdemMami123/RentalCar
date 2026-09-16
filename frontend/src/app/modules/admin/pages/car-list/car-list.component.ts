import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CarService } from '../../../car/services/car.service';
import { Car } from '../../../../core/models/car.model';

@Component({
	selector: 'app-admin-car-list',
	standalone: true,
	imports: [CommonModule, FormsModule, RouterLink],
	templateUrl: './car-list.component.html',
	styleUrl: './car-list.component.scss'
})
export class CarListComponent implements OnInit {
	cars: Car[] = [];
	editingCarId: number | null = null;
	loading = false;
	saving = false;
	error = '';
	success = '';
	form: Car = this.emptyCar();

	readonly carTypes = ['SEDAN', 'SUV', 'VAN', 'TRUCK', 'COUPE', 'HATCHBACK'];
	readonly transmissions = ['MANUAL', 'AUTOMATIC', 'CVT'];
	readonly fuelTypes = ['PETROL', 'DIESEL', 'HYBRID', 'ELECTRIC'];
	readonly statuses = ['AVAILABLE', 'RENTED', 'MAINTENANCE', 'RETIRED'];

	constructor(private carService: CarService) {}

	ngOnInit(): void { this.loadCars(); }

	loadCars(): void {
		this.loading = true;
		this.carService.getAllCars().subscribe({
			next: response => { this.cars = response.data || []; this.loading = false; },
			error: error => { this.error = error.error?.message || 'Unable to load cars.'; this.loading = false; }
		});
	}

	saveCar(): void {
		this.error = '';
		this.success = '';
		this.saving = true;
		const request = this.editingCarId
			? this.carService.updateCar(this.editingCarId, this.form)
			: this.carService.createCar(this.form);
		request.subscribe({
			next: () => {
				this.success = this.editingCarId ? 'Car updated successfully.' : 'Car created successfully.';
				this.form = this.emptyCar();
				this.editingCarId = null;
				this.saving = false;
				this.loadCars();
			},
			error: error => { this.error = error.error?.message || 'Unable to save car.'; this.saving = false; }
		});
	}

	editCar(car: Car): void {
		this.editingCarId = car.id || null;
		this.form = { ...car };
		window.scrollTo({ top: 0, behavior: 'smooth' });
	}

	cancelEdit(): void { this.editingCarId = null; this.form = this.emptyCar(); }

	deleteCar(car: Car): void {
		if (!car.id || !confirm(`Delete ${car.make} ${car.model}?`)) return;
		this.carService.deleteCar(car.id).subscribe({
			next: () => { this.success = 'Car deleted successfully.'; this.loadCars(); },
			error: error => { this.error = error.error?.message || 'Unable to delete car.'; }
		});
	}

	updateStatus(car: Car, status: string): void {
		if (!car.id) return;
		this.carService.updateCarStatus(car.id, status).subscribe({
			next: updated => { Object.assign(car, updated.data); this.success = 'Car status updated.'; },
			error: error => { this.error = error.error?.message || 'Unable to update status.'; }
		});
	}

	private emptyCar(): Car {
		return { make: '', model: '', year: new Date().getFullYear(), registrationNumber: '', licensePlate: '', vin: '', carType: 'SEDAN', seats: 5, transmission: 'AUTOMATIC', fuelType: 'PETROL', dailyRate: 0, status: 'AVAILABLE', mileage: 0, color: '', description: '' } as Car;
	}
}
