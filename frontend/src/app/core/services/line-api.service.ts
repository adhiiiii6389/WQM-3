import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { Line } from '../models/wqm.models';

@Injectable({ providedIn: 'root' })
export class LineApiService {
  private readonly baseUrl = '/api/lines';

  getAll(): Observable<Line[]> {
    return from(this.getJson<Line[]>(this.baseUrl));
  }

  private async getJson<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Request failed: ${response.status}`);
    }
    return (await response.json()) as T;
  }
}
