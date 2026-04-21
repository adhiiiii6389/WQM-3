import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Incident, Line } from '../../core/models/wqm.models';

@Component({
  selector: 'app-line-status',
  standalone: true,
  imports: [],
  templateUrl: './line-status.component.html',
  styleUrl: './line-status.component.css',
})
export class LineStatusComponent {
  @Input({ required: true }) lines: Line[] = [];
  @Input({ required: true }) incidents: Incident[] = [];
  @Input({ required: true }) outOfSpecLineIds: number[] = [];
  @Input() selectedLineId: number | null = null;

  @Output() lineSelected = new EventEmitter<number>();

  selectLine(lineId: number): void {
    this.lineSelected.emit(lineId);
  }

  isOutOfSpec(lineId: number): boolean {
    return this.outOfSpecLineIds.includes(lineId);
  }

  activeIncidents(lineId: number): number {
    return this.incidents.filter(
      (incident) => incident.line.id === lineId && incident.status === 'ACTIVE',
    ).length;
  }
}
