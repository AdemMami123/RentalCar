import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { InspectionService } from '../../services/inspection.service';
import { Booking } from '../../../../core/models/booking.model';
import { InspectionComparison, InspectionDamage, InspectionType, VehicleInspection } from '../../../../core/models/inspection.model';

@Component({
  selector: 'app-inspection',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './inspection.component.html',
  styleUrl: './inspection.component.scss'
})
export class InspectionComponent implements OnInit {
  booking: Booking | null = null;
  inspection: VehicleInspection | null = null;
  comparison: InspectionComparison | null = null;
  loading = true;
  saving = false;
  error: string | null = null;
  success: string | null = null;
  selectedFiles: File[] = [];
  readOnly = false;

  readonly form = this.fb.group({
    inspectionType: ['PICKUP' as InspectionType, Validators.required],
    odometerReading: [0, [Validators.required, Validators.min(0)]],
    fuelLevel: [100, [Validators.required, Validators.min(0), Validators.max(100)]],
    exteriorCondition: ['GOOD', Validators.required],
    interiorCondition: ['GOOD', Validators.required],
    notes: [''],
    damages: this.fb.array([])
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly bookingService: BookingService,
    private readonly inspectionService: InspectionService
  ) {}

  get damages(): FormArray { return this.form.controls.damages; }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.bookingService.getBookingById(id).subscribe({
      next: response => {
        this.booking = response.data;
        this.readOnly = this.booking.bookingStatus === 'COMPLETED';
        this.form.patchValue({ inspectionType: this.readOnly ? 'RETURN' : this.booking.bookingStatus === 'ACTIVE' ? 'RETURN' : 'PICKUP' });
        this.loading = false;
        if (this.readOnly) this.loadComparison();
      },
      error: error => { this.error = error.error?.message || 'Unable to load booking'; this.loading = false; }
    });
  }

  addDamage(): void {
    this.damages.push(this.fb.group({
      damageType: ['', Validators.required],
      locationOnVehicle: ['', Validators.required],
      description: ['', Validators.required],
      severity: ['MINOR', Validators.required]
    }));
  }

  removeDamage(index: number): void { this.damages.removeAt(index); }

  selectFiles(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFiles = input.files ? Array.from(input.files) : [];
  }

  submit(): void {
    this.error = null;
    this.success = null;
    if (!this.booking?.id || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    const value = this.form.getRawValue();
    const draft: VehicleInspection = {
      bookingId: this.booking.id,
      carId: this.booking.carId,
      inspectionType: value.inspectionType!,
      odometerReading: Number(value.odometerReading),
      fuelLevel: Number(value.fuelLevel),
      exteriorCondition: value.exteriorCondition!,
      interiorCondition: value.interiorCondition!,
      notes: value.notes || undefined,
      damages: (value.damages || []) as InspectionDamage[]
    };
    this.inspectionService.createDraft(draft).subscribe({
      next: response => this.uploadAndComplete(response.data),
      error: error => { this.error = error.error?.message || 'Unable to save inspection'; this.saving = false; }
    });
  }

  private uploadAndComplete(draft: VehicleInspection): void {
    if (!this.selectedFiles.length) {
      this.completeDraft(draft.id!);
      return;
    }
    forkJoin(this.selectedFiles.map(file => this.inspectionService.uploadPhoto(draft.id!, file, 'GENERAL'))).subscribe({
      next: () => undefined,
      error: error => { this.error = error.error?.message || 'Photo upload failed'; this.saving = false; },
      complete: () => this.completeDraft(draft.id!)
    });
  }

  private completeDraft(id: number): void {
    this.inspectionService.complete(id).subscribe({
      next: response => {
        this.inspection = response.data;
        this.success = 'Inspection completed successfully.';
        this.saving = false;
        if (this.inspection.inspectionType === 'RETURN') this.loadComparison();
      },
      error: error => { this.error = error.error?.message || 'Unable to complete inspection'; this.saving = false; }
    });
  }

  private loadComparison(): void {
    if (!this.booking?.id) return;
    this.inspectionService.compare(this.booking.id).subscribe({
      next: response => this.comparison = response.data,
      error: error => this.error = error.error?.message || 'Unable to generate comparison'
    });
  }
}
