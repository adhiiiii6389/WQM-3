export interface Line {
  id: number;
  name: string;
  location?: string;
  active: boolean;
}

export interface WaterSensor {
  id: number;
  sensorCode: string;
  sensorType: string;
  line: Line;
  baselinePh?: number;
  baselineTurbidity?: number;
  baselineConductivity?: number;
}

export interface QualityReading {
  id: number;
  line: Line;
  sensor?: WaterSensor;
  ph?: number;
  turbidity?: number;
  conductivity?: number;
  ts: string;
}

export interface Threshold {
  id: number;
  line: Line;
  minPh?: number;
  maxPh?: number;
  minTurbidity?: number;
  maxTurbidity?: number;
  minConductivity?: number;
  maxConductivity?: number;
}

export interface Incident {
  id: number;
  line: Line;
  metric: string;
  readingValue?: number;
  thresholdValue?: number;
  status: string;
  message?: string;
  createdAt: string;
  resolvedAt?: string;
}
