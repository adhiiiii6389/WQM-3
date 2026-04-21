import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { WaterSensor } from '../models/wqm.models';

@Injectable({ providedIn: 'root' })
export class SensorApiService {
  private readonly baseUrl = '/api/sensors';

  getAll(): Observable<WaterSensor[]> {
    return from(this.getJson<WaterSensor[]>(this.baseUrl));
  }

  private async getJson<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }
    return (await response.json()) as T;
  }
}
