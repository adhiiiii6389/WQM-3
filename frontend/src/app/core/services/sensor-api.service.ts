import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { WaterSensor } from '../models/wqm.models';
import { fetchJson } from './api-http.util';

@Injectable({ providedIn: 'root' })
export class SensorApiService {
  private readonly baseUrl = '/api/sensors';

  getAll(): Observable<WaterSensor[]> {
    return from(fetchJson<WaterSensor[]>(this.baseUrl));
  }
}
