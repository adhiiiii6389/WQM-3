import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { Incident } from '../models/wqm.models';
import { fetchJson } from './api-http.util';

@Injectable({ providedIn: 'root' })
export class IncidentApiService {
  private readonly baseUrl = '/api/incidents';

  getByLine(lineId: number): Observable<Incident[]> {
    const query = new URLSearchParams({ lineId: String(lineId) });
    return from(fetchJson<Incident[]>(`${this.baseUrl}?${query.toString()}`));
  }

  getAll(): Observable<Incident[]> {
    return from(fetchJson<Incident[]>(this.baseUrl));
  }

  updateStatus(id: number, status: 'ACTIVE' | 'RESOLVED'): Observable<Incident> {
    return from(
      fetchJson<Incident>(`${this.baseUrl}/${id}/status`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status }),
      }),
    );
  }
}
