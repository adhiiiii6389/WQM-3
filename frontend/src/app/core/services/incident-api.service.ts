import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { Incident } from '../models/wqm.models';

@Injectable({ providedIn: 'root' })
export class IncidentApiService {
  private readonly baseUrl = '/api/incidents';

  getByLine(lineId: number): Observable<Incident[]> {
    const query = new URLSearchParams({ lineId: String(lineId) });
    return from(this.getJson<Incident[]>(`${this.baseUrl}?${query.toString()}`));
  }

  getAll(): Observable<Incident[]> {
    return from(this.getJson<Incident[]>(this.baseUrl));
  }

  updateStatus(id: number, status: 'ACTIVE' | 'RESOLVED'): Observable<Incident> {
    return from(
      this.sendJson<Incident>(`${this.baseUrl}/${id}/status`, 'PATCH', {
        status,
      }),
    );
  }

  private async getJson<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }
    return (await response.json()) as T;
  }

  private async sendJson<T>(url: string, method: 'PATCH', body: unknown): Promise<T> {
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
