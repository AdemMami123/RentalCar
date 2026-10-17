import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Location } from '../../../../core/models/location.model';
import { LocationService } from '../../../../core/services/location.service';

@Component({
  selector: 'app-admin-locations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-locations.component.html',
  styleUrl: './admin-locations.component.scss'
})
export class AdminLocationsComponent implements OnInit {
  locations: Location[] = [];
  editing: Location | null = null;
  loading = true;
  saving = false;
  error = '';
  success = '';

  constructor(private readonly locationService: LocationService) {}

  ngOnInit(): void { this.load(); }

  load(): void {
    this.locationService.getLocations().subscribe({
      next: response => { this.locations = response.data || []; this.loading = false; },
      error: error => { this.error = error.error?.message || 'Unable to load locations.'; this.loading = false; }
    });
  }

  startCreate(): void {
    this.editing = { name: '', address: '', city: '', country: '', pickupEnabled: true, returnEnabled: true, deliveryEnabled: false, active: true, locationType: 'OFFICE', timezone: 'UTC' } as Location;
    this.error = '';
  }

  startEdit(location: Location): void { this.editing = { ...location }; this.error = ''; }
  cancel(): void { this.editing = null; }

  save(): void {
    if (!this.editing?.name || !this.editing.address || !this.editing.city || !this.editing.country) {
      this.error = 'Name, address, city, and country are required.';
      return;
    }
    this.saving = true;
    const request = this.editing.id ? this.locationService.updateLocation(this.editing.id, this.editing) : this.locationService.createLocation(this.editing);
    request.subscribe({
      next: response => {
        this.success = response.message;
        this.editing = null;
        this.saving = false;
        this.load();
      },
      error: error => { this.error = error.error?.message || 'Unable to save location.'; this.saving = false; }
    });
  }

  remove(location: Location): void {
    if (!location.id || !confirm(`Delete ${location.name}?`)) return;
    this.locationService.deleteLocation(location.id).subscribe({
      next: () => this.load(),
      error: error => this.error = error.error?.message || 'Unable to delete location.'
    });
  }
}
