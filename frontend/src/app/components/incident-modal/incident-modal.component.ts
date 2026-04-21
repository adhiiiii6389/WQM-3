import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Incident } from '../../core/models/wqm.models';

@Component({
  selector: 'app-incident-modal',
  standalone: true,
  imports: [],
  templateUrl: './incident-modal.component.html',
  styleUrl: './incident-modal.component.css',
})
export class IncidentModalComponent {
  @Input() incident: Incident | null = null;
  @Input() busy = false;

  @Output() closed = new EventEmitter<void>();
  @Output() resolved = new EventEmitter<number>();

  close(): void {
    this.closed.emit();
  }

  resolveIncident(): void {
    if (!this.incident) {
      return;
    }
    this.resolved.emit(this.incident.id);
  }

  formatDate(value: string | undefined): string {
    if (!value) {
      return '-';
    }

    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? '-' : date.toLocaleString();
  }

  formatMetric(value: number | undefined): string {
    if (value === undefined || value === null || Number.isNaN(value)) {
      return '-';
    }
    return value.toFixed(2);
  }
}
