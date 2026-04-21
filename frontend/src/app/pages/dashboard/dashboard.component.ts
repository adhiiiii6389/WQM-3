import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { forkJoin } from 'rxjs';

import { IncidentModalComponent } from '../../components/incident-modal/incident-modal.component';
import { LineStatusComponent } from '../../components/line-status/line-status.component';
import { QualityChartComponent } from '../../components/quality-chart/quality-chart.component';
import { ThresholdFormComponent } from '../../components/threshold-form/threshold-form.component';
import {
  Incident,
  Line,
  QualityReading,
  Threshold,
  WaterSensor,
} from '../../core/models/wqm.models';
import { IncidentApiService } from '../../core/services/incident-api.service';
import { LineApiService } from '../../core/services/line-api.service';
import { ProcessApiService } from '../../core/services/process-api.service';
import { ReadingApiService } from '../../core/services/reading-api.service';
import { SensorApiService } from '../../core/services/sensor-api.service';
import { ThresholdApiService } from '../../core/services/threshold-api.service';

interface SummaryCard {
  label: string;
  value: string;
  trend: string;
  tone: 'ok' | 'warn' | 'danger';
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    LineStatusComponent,
    QualityChartComponent,
    ThresholdFormComponent,
    IncidentModalComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  lines: Line[] = [];
  sensors: WaterSensor[] = [];
  allIncidents: Incident[] = [];
  lineIncidents: Incident[] = [];
  outOfSpecLineIds: number[] = [];
  readings: QualityReading[] = [];
  selectedThreshold: Threshold | null = null;
  selectedLineId: number | null = null;
  selectedIncident: Incident | null = null;

  loading = false;
  resolvingIncident = false;
  errorMessage = '';
  lastUpdated = '';

  readonly navItems = [
    'Overview',
    'Quality Readings',
    'Incidents',
    'Thresholds',
    'Sensors',
  ];

  constructor(
    private readonly lineApi: LineApiService,
    private readonly sensorApi: SensorApiService,
    private readonly incidentApi: IncidentApiService,
    private readonly processApi: ProcessApiService,
    private readonly readingApi: ReadingApiService,
    private readonly thresholdApi: ThresholdApiService,
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  get summaryCards(): SummaryCard[] {
    const activeIncidents = this.allIncidents.filter(
      (incident) => incident.status === 'ACTIVE',
    ).length;
    const healthySensors = this.sensors.length - this.outOfSpecLineIds.length;
    const healthyPercent =
      this.sensors.length === 0
        ? 0
        : Math.max(0, Math.round((healthySensors / this.sensors.length) * 100));

    return [
      {
        label: 'Lines Monitored',
        value: String(this.lines.length).padStart(2, '0'),
        trend: `${this.outOfSpecLineIds.length} out of spec`,
        tone: this.outOfSpecLineIds.length > 0 ? 'warn' : 'ok',
      },
      {
        label: 'Active Incidents',
        value: String(activeIncidents).padStart(2, '0'),
        trend: activeIncidents > 0 ? 'Needs attention' : 'No active incidents',
        tone: activeIncidents > 0 ? 'danger' : 'ok',
      },
      {
        label: 'Healthy Sensors',
        value: `${healthyPercent}%`,
        trend: `${this.sensors.length} total sensors`,
        tone: healthyPercent < 80 ? 'warn' : 'ok',
      },
      {
        label: 'Threshold Alerts',
        value: String(this.outOfSpecLineIds.length).padStart(2, '0'),
        trend: 'Based on process endpoint',
        tone: this.outOfSpecLineIds.length > 0 ? 'warn' : 'ok',
      },
    ];
  }

  selectLine(lineId: number): void {
    this.selectedLineId = lineId;
    this.loadLineData(lineId);
  }

  onThresholdUpdated(threshold: Threshold): void {
    this.selectedThreshold = threshold;
  }

  openIncident(incident: Incident): void {
    this.selectedIncident = incident;
  }

  closeIncidentModal(): void {
    this.selectedIncident = null;
  }

  resolveIncident(incidentId: number): void {
    this.resolvingIncident = true;
    this.incidentApi.updateStatus(incidentId, 'RESOLVED').subscribe({
      next: () => {
        this.resolvingIncident = false;
        this.selectedIncident = null;
        this.refreshIncidents();
      },
      error: () => {
        this.resolvingIncident = false;
        this.errorMessage = 'Failed to resolve incident.';
      },
    });
  }

  private loadDashboard(): void {
    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      lines: this.lineApi.getAll(),
      sensors: this.sensorApi.getAll(),
      incidents: this.incidentApi.getAll(),
      outOfSpec: this.processApi.getOutOfSpecLines(),
    }).subscribe({
      next: ({ lines, sensors, incidents, outOfSpec }) => {
        this.lines = lines;
        this.sensors = sensors;
        this.allIncidents = incidents;
        this.outOfSpecLineIds = outOfSpec.map((line) => line.id);
        this.lastUpdated = new Date().toISOString();
        this.loading = false;

        if (!this.selectedLineId && this.lines.length > 0) {
          this.selectedLineId = this.lines[0].id;
        }

        if (this.selectedLineId) {
          this.loadLineData(this.selectedLineId);
        }
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to load dashboard data.';
      },
    });
  }

  private loadLineData(lineId: number): void {
    const now = new Date();
    const start = new Date(now.getTime() - 6 * 60 * 60 * 1000).toISOString();
    const end = now.toISOString();

    forkJoin({
      readings: this.readingApi.getByLineAndWindow(lineId, start, end),
      thresholds: this.thresholdApi.getLatestByLine(lineId),
      incidents: this.incidentApi.getByLine(lineId),
    }).subscribe({
      next: ({ readings, thresholds, incidents }) => {
        this.readings = readings;
        this.selectedThreshold = thresholds[0] ?? null;
        this.lineIncidents = incidents;
      },
      error: () => {
        this.errorMessage = 'Failed to load line details.';
      },
    });
  }

  private refreshIncidents(): void {
    const requests = {
      allIncidents: this.incidentApi.getAll(),
    };

    if (!this.selectedLineId) {
      forkJoin(requests).subscribe(({ allIncidents }) => {
        this.allIncidents = allIncidents;
      });
      return;
    }

    forkJoin({
      ...requests,
      lineIncidents: this.incidentApi.getByLine(this.selectedLineId),
    }).subscribe(({ allIncidents, lineIncidents }) => {
      this.allIncidents = allIncidents;
      this.lineIncidents = lineIncidents;
    });
  }

  formatDate(value: string | undefined): string {
    if (!value) {
      return 'loading...';
    }

    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? 'loading...' : date.toLocaleString();
  }

  formatMetric(value: number | undefined): string {
    if (value === undefined || value === null || Number.isNaN(value)) {
      return '-';
    }
    return value.toFixed(2);
  }
}
