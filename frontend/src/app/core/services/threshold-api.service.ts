import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { Threshold } from '../models/wqm.models';
import { fetchJson } from './api-http.util';

export interface ThresholdUpsert {
  line: { id: number };
  minPh?: number;
  maxPh?: number;
  minTurbidity?: number;
  maxTurbidity?: number;
  minConductivity?: number;
  maxConductivity?: number;
}

@Injectable({ providedIn: 'root' })
export class ThresholdApiService {
  private readonly baseUrl = '/api/thresholds';

  getLatestByLine(lineId: number): Observable<Threshold[]> {
    const query = new URLSearchParams({ lineId: String(lineId) });
    return from(fetchJson<Threshold[]>(`${this.baseUrl}?${query.toString()}`));
  }

  create(payload: ThresholdUpsert): Observable<Threshold> {
    return from(
      fetchJson<Threshold>(this.baseUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      }),
    );
  }

  update(id: number, payload: ThresholdUpsert): Observable<Threshold> {
    return from(
      fetchJson<Threshold>(`${this.baseUrl}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      }),
    );
  }
}
