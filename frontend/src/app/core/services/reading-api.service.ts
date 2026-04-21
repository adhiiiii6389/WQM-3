import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { QualityReading } from '../models/wqm.models';

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
      this.getJson<QualityReading[]>(`${this.baseUrl}/line/${lineId}?${query.toString()}`),
    );
  }

  private async getJson<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }
    return (await response.json()) as T;
  }
}
