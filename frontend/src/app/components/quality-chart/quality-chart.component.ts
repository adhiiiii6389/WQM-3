import { Component, Input } from '@angular/core';

import { QualityReading, Threshold } from '../../core/models/wqm.models';

interface ChartPoint {
  x: number;
  y: number;
}

interface SeriesDot {
  x: number;
  y: number;
  tooltip: string;
}

@Component({
  selector: 'app-quality-chart',
  standalone: true,
  imports: [],
  templateUrl: './quality-chart.component.html',
  styleUrl: './quality-chart.component.css',
})
export class QualityChartComponent {
  @Input({ required: true }) readings: QualityReading[] = [];
  @Input() threshold: Threshold | null = null;

  readonly width = 720;
  readonly height = 260;
  readonly padding = 24;

  get sortedReadings(): QualityReading[] {
    return [...this.readings].sort((a, b) =>
      new Date(a.ts).getTime() - new Date(b.ts).getTime(),
    );
  }

  get hasData(): boolean {
    return this.sortedReadings.length > 1;
  }

  get phPoints(): string {
    return this.toPolyline(this.sortedReadings.map((reading) => reading.ph));
  }

  get turbidityPoints(): string {
    return this.toPolyline(this.sortedReadings.map((reading) => reading.turbidity));
  }

  get conductivityPoints(): string {
    return this.toPolyline(this.sortedReadings.map((reading) => reading.conductivity));
  }

  get phMinBandY(): number | null {
    if (!this.hasValue(this.threshold?.minPh)) {
      return null;
    }
    return this.mapY(this.threshold!.minPh!);
  }

  get phMaxBandY(): number | null {
    if (!this.hasValue(this.threshold?.maxPh)) {
      return null;
    }
    return this.mapY(this.threshold!.maxPh!);
  }

  get turbidityMinBandY(): number | null {
    if (!this.hasValue(this.threshold?.minTurbidity)) {
      return null;
    }
    return this.mapY(this.threshold!.minTurbidity!);
  }

  get turbidityMaxBandY(): number | null {
    if (!this.hasValue(this.threshold?.maxTurbidity)) {
      return null;
    }
    return this.mapY(this.threshold!.maxTurbidity!);
  }

  get conductivityMinBandY(): number | null {
    if (!this.hasValue(this.threshold?.minConductivity)) {
      return null;
    }
    return this.mapY(this.threshold!.minConductivity!);
  }

  get conductivityMaxBandY(): number | null {
    if (!this.hasValue(this.threshold?.maxConductivity)) {
      return null;
    }
    return this.mapY(this.threshold!.maxConductivity!);
  }

  get phDots(): SeriesDot[] {
    return this.toDots('pH', this.sortedReadings.map((reading) => reading.ph));
  }

  get turbidityDots(): SeriesDot[] {
    return this.toDots('Turbidity', this.sortedReadings.map((reading) => reading.turbidity));
  }

  get conductivityDots(): SeriesDot[] {
    return this.toDots(
      'Conductivity',
      this.sortedReadings.map((reading) => reading.conductivity),
    );
  }

  formatNumber(value: number | undefined): string {
    if (!this.hasValue(value)) {
      return '-';
    }
    return value.toFixed(2);
  }

  private toPolyline(values: Array<number | undefined>): string {
    const points: ChartPoint[] = [];
    values.forEach((value, index) => {
      if (value === undefined) {
        return;
      }
      points.push({ x: this.mapX(index, values.length), y: this.mapY(value) });
    });

    return points.map((point) => `${point.x},${point.y}`).join(' ');
  }

  private toDots(metric: string, values: Array<number | undefined>): SeriesDot[] {
    const dots: SeriesDot[] = [];
    values.forEach((value, index) => {
      if (!this.hasValue(value)) {
        return;
      }

      const reading = this.sortedReadings[index];
      const time = reading?.ts ? new Date(reading.ts).toLocaleTimeString() : '-';

      dots.push({
        x: this.mapX(index, values.length),
        y: this.mapY(value),
        tooltip: `${metric}: ${this.formatNumber(value)} at ${time}`,
      });
    });

    return dots;
  }

  private mapX(index: number, total: number): number {
    const usableWidth = this.width - this.padding * 2;
    const step = total > 1 ? usableWidth / (total - 1) : 0;
    return this.padding + index * step;
  }

  private mapY(value: number): number {
    const range = this.valueRange();
    if (range.max === range.min) {
      return this.height / 2;
    }

    const normalized = (value - range.min) / (range.max - range.min);
    return this.height - this.padding - normalized * (this.height - this.padding * 2);
  }

  private valueRange(): { min: number; max: number } {
    const values = this.sortedReadings.flatMap((reading) => [
      reading.ph,
      reading.turbidity,
      reading.conductivity,
    ]);
    const filtered = values.filter(
      (value): value is number => value !== undefined && !Number.isNaN(value),
    );

    if (this.threshold) {
      [
        this.threshold.minPh,
        this.threshold.maxPh,
        this.threshold.minTurbidity,
        this.threshold.maxTurbidity,
        this.threshold.minConductivity,
        this.threshold.maxConductivity,
      ].forEach((value) => {
        if (this.hasValue(value)) {
          filtered.push(value);
        }
      });
    }

    if (filtered.length === 0) {
      return { min: 0, max: 1 };
    }

    return {
      min: Math.min(...filtered),
      max: Math.max(...filtered),
    };
  }

  private hasValue(value: number | undefined | null): value is number {
    return value !== undefined && value !== null && !Number.isNaN(value);
  }
}
