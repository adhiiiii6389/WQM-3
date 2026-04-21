import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { Line } from '../models/wqm.models';

@Injectable({ providedIn: 'root' })
export class ProcessApiService {
  private readonly baseUrl = '/api/process';

  getOutOfSpecLines(): Observable<Line[]> {
    return from(this.getJson<Line[]>(`${this.baseUrl}/lines/out-of-spec`));
  }

  getSensorDrift(lineId: number, startTs: string, endTs: string): Observable<unknown[]> {
    const query = new URLSearchParams({
      lineId: String(lineId),
      startTs,
      endTs,
    });
    return from(this.getJson<unknown[]>(`${this.baseUrl}/sensor-drift?${query.toString()}`));
  }

  private async getJson<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }
    return (await response.json()) as T;
  }
}
