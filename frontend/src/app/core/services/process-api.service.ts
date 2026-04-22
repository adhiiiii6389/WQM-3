import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { Line } from '../models/wqm.models';
import { fetchJson } from './api-http.util';

@Injectable({ providedIn: 'root' })
export class ProcessApiService {
  private readonly baseUrl = '/api/process';

  getOutOfSpecLines(): Observable<Line[]> {
    return from(fetchJson<Line[]>(`${this.baseUrl}/lines/out-of-spec`));
  }

  getSensorDrift(lineId: number, startTs: string, endTs: string): Observable<unknown[]> {
    const query = new URLSearchParams({
      lineId: String(lineId),
      startTs,
      endTs,
    });
    return from(fetchJson<unknown[]>(`${this.baseUrl}/sensor-drift?${query.toString()}`));
  }
}
