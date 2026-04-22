import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';

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
export class DashboardComponent implements OnInit, OnDestroy {
  private readonly refreshIntervalMs = 10000;
  private refreshTimer: ReturnType<typeof setInterval> | null = null;
  private initialRetryTimer: ReturnType<typeof setTimeout> | null = null;

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
    this.triggerInitialRetry();
    this.startAutoRefresh();
  }

  ngOnDestroy(): void {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer);
      this.refreshTimer = null;
    }

    if (this.initialRetryTimer) {
      clearTimeout(this.initialRetryTimer);
      this.initialRetryTimer = null;
    }
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

  retryLoad(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    this.loading = true;
    this.errorMessage = '';
    const warnings: string[] = [];

    forkJoin({
      lines: this.lineApi.getAll().pipe(
        catchError((error: unknown) => {
          warnings.push(this.buildErrorMessage('lines', error));
          return of<Line[]>([]);
        }),
      ),
      sensors: this.sensorApi.getAll().pipe(
        catchError((error: unknown) => {
          warnings.push(this.buildErrorMessage('sensors', error));
          return of<WaterSensor[]>([]);
        }),
      ),
      incidents: this.incidentApi.getAll().pipe(
        catchError((error: unknown) => {
          warnings.push(this.buildErrorMessage('incidents', error));
          return of<Incident[]>([]);
        }),
      ),
      outOfSpec: this.processApi.getOutOfSpecLines().pipe(
        catchError((error: unknown) => {
          warnings.push(this.buildErrorMessage('out-of-spec lines', error));
          return of<Line[]>([]);
        }),
      ),
    }).subscribe({
      next: ({ lines, sensors, incidents, outOfSpec }) => {
        this.lines = lines;
        this.sensors = sensors;
        this.allIncidents = incidents;
        this.outOfSpecLineIds = outOfSpec.map((line) => line.id);
        this.lastUpdated = new Date().toISOString();
        this.loading = false;
        this.errorMessage =
          warnings.length > 0
            ? `Partial data loaded. ${warnings.join(' | ')}`
            : '';

        if (!this.selectedLineId && this.lines.length > 0) {
          this.selectedLineId = this.lines[0].id;
        }

        if (this.selectedLineId) {
          this.loadLineData(this.selectedLineId);
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = this.buildErrorMessage('dashboard', error);
      },
    });
  }

  private loadLineData(lineId: number): void {
    const now = new Date();
    const start = new Date(now.getTime() - 6 * 60 * 60 * 1000).toISOString();
    const end = now.toISOString();
    const warnings: string[] = [];

    forkJoin({
      readings: this.readingApi.getByLineAndWindow(lineId, start, end).pipe(
        catchError((error: unknown) => {
          warnings.push(this.buildErrorMessage('readings', error));
          return of<QualityReading[]>([]);
        }),
      ),
      thresholds: this.thresholdApi.getLatestByLine(lineId).pipe(
        catchError((error: unknown) => {
          warnings.push(this.buildErrorMessage('thresholds', error));
          return of<Threshold[]>([]);
        }),
      ),
      incidents: this.incidentApi.getByLine(lineId).pipe(
        catchError((error: unknown) => {
          warnings.push(this.buildErrorMessage('line incidents', error));
          return of<Incident[]>([]);
        }),
      ),
    }).subscribe({
      next: ({ readings, thresholds, incidents }) => {
        this.readings = readings;
        this.selectedThreshold = thresholds[0] ?? null;
        this.lineIncidents = incidents;
        if (warnings.length > 0) {
          this.errorMessage = `Partial line data loaded. ${warnings.join(' | ')}`;
        }
      },
      error: (error) => {
        this.errorMessage = this.buildErrorMessage('line details', error);
      },
    });
  }

  private buildErrorMessage(sourceName: string, error: unknown): string {
    const detail = error instanceof Error ? error.message : 'Unknown error';
    return `Failed to load ${sourceName}: ${detail}`;
  }

  private startAutoRefresh(): void {
    this.refreshTimer = setInterval(() => {
      this.loadDashboard();
    }, this.refreshIntervalMs);
  }

  private triggerInitialRetry(): void {
    this.initialRetryTimer = setTimeout(() => {
      this.retryLoad();
    }, 1200);
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
