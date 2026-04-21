import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Threshold } from '../../core/models/wqm.models';
import {
  ThresholdApiService,
  ThresholdUpsert,
} from '../../core/services/threshold-api.service';

@Component({
  selector: 'app-threshold-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './threshold-form.component.html',
  styleUrl: './threshold-form.component.css',
})
export class ThresholdFormComponent implements OnChanges {
  @Input() selectedLineId: number | null = null;
  @Output() thresholdUpdated = new EventEmitter<Threshold>();

  thresholdId: number | null = null;
  minPh = 0;
  maxPh = 14;
  minTurbidity = 0;
  maxTurbidity = 10;
  minConductivity = 0;
  maxConductivity = 500;

  loading = false;
  saving = false;
  errorMessage = '';

  constructor(private readonly thresholdApi: ThresholdApiService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedLineId']) {
      this.loadThreshold();
    }
  }

  save(): void {
    if (!this.selectedLineId) {
      return;
    }

    this.saving = true;
    this.errorMessage = '';

    const payload: ThresholdUpsert = {
      line: { id: this.selectedLineId },
      minPh: this.minPh,
      maxPh: this.maxPh,
      minTurbidity: this.minTurbidity,
      maxTurbidity: this.maxTurbidity,
      minConductivity: this.minConductivity,
      maxConductivity: this.maxConductivity,
    };

    const request$ = this.thresholdId
      ? this.thresholdApi.update(this.thresholdId, payload)
      : this.thresholdApi.create(payload);

    request$.subscribe({
      next: (threshold) => {
        this.thresholdId = threshold.id;
        this.saving = false;
        this.thresholdUpdated.emit(threshold);
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'Unable to save threshold.';
      },
    });
  }

  private loadThreshold(): void {
    if (!this.selectedLineId) {
      this.thresholdId = null;
      this.errorMessage = '';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.thresholdApi.getLatestByLine(this.selectedLineId).subscribe({
      next: (thresholds) => {
        const threshold = thresholds[0];
        this.loading = false;
        if (!threshold) {
          this.thresholdId = null;
          return;
        }

        this.thresholdId = threshold.id;
        this.minPh = threshold.minPh ?? 0;
        this.maxPh = threshold.maxPh ?? 14;
        this.minTurbidity = threshold.minTurbidity ?? 0;
        this.maxTurbidity = threshold.maxTurbidity ?? 10;
        this.minConductivity = threshold.minConductivity ?? 0;
        this.maxConductivity = threshold.maxConductivity ?? 500;
        this.thresholdUpdated.emit(threshold);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Unable to load threshold.';
      },
    });
  }
}
