import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Location } from '../../../../core/models/location.model';
import { LocationService } from '../../../../core/services/location.service';

@Component({
  selector: 'app-location-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './location-list.component.html',
  styleUrl: './location-list.component.scss'
})
export class LocationListComponent implements OnInit {
  locations: Location[] = [];
  filteredLocations: Location[] = [];
  selectedLocation: Location | null = null;
  searchTerm = '';
  loading = true;
  error = '';

  constructor(private readonly locationService: LocationService) {}

  ngOnInit(): void {
    this.locationService.getActiveLocations().subscribe({
      next: response => {
        this.locations = response.data || [];
        this.filteredLocations = this.locations;
        this.selectedLocation = this.locations[0] || null;
        this.loading = false;
      },
      error: error => {
        this.error = error.error?.message || 'Unable to load rental locations.';
        this.loading = false;
      }
    });
  }

  search(): void {
    const term = this.searchTerm.trim().toLowerCase();
    this.filteredLocations = term
      ? this.locations.filter(location => `${location.name} ${location.city} ${location.address}`.toLowerCase().includes(term))
      : this.locations;
    if (this.selectedLocation && !this.filteredLocations.includes(this.selectedLocation)) {
      this.selectedLocation = this.filteredLocations[0] || null;
    }
  }

  selectLocation(location: Location): void {
    this.selectedLocation = location;
  }

  directions(location: Location): string {
    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(`${location.address || ''}, ${location.city || ''}`)}`;
  }
}
