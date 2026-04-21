import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { Threshold } from '../models/wqm.models';

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
    return from(this.getJson<Threshold[]>(`${this.baseUrl}?${query.toString()}`));
  }

  create(payload: ThresholdUpsert): Observable<Threshold> {
    return from(this.sendJson<Threshold>(this.baseUrl, 'POST', payload));
  }

  update(id: number, payload: ThresholdUpsert): Observable<Threshold> {
    return from(this.sendJson<Threshold>(`${this.baseUrl}/${id}`, 'PUT', payload));
  }

  private async getJson<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }
    return (await response.json()) as T;
  }

  private async sendJson<T>(url: string, method: 'POST' | 'PUT', body: unknown): Promise<T> {
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }
    return (await response.json()) as T;
  }
}
