import { Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';

import { Line } from '../models/wqm.models';
import { fetchJson } from './api-http.util';

@Injectable({ providedIn: 'root' })
export class LineApiService {
  private readonly baseUrl = '/api/lines';

  getAll(): Observable<Line[]> {
    return from(fetchJson<Line[]>(this.baseUrl));
  }
}
