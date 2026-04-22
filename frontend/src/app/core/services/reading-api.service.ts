import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { QualityReading } from '../models/wqm.models';
import { fetchJson } from './api-http.util';

@Injectable({ providedIn: 'root' })
export class ReadingApiService {
  private readonly baseUrl = '/api/readings';

  getByLineAndWindow(
    lineId: number,
    startTs: string,
    endTs: string,
  ): Observable<QualityReading[]> {
    const query = new URLSearchParams({ startTs, endTs });
    return from(
      fetchJson<QualityReading[]>(`${this.baseUrl}/line/${lineId}?${query.toString()}`),
    );
  }
}
