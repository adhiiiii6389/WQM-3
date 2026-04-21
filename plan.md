## Coding Agent Prompt Plan

Use this file as prompt-by-prompt instructions for a coding agent.

Important rules for the coding agent:
- Build backend first. Do not start frontend until backend is completed and runnable.
- Keep code simple and easy to understand for a fresher.
- Use MySQL.
- Add only new code/files needed. Do not rewrite unrelated existing files.
- Keep functionality exactly aligned with Water Quality Monitor (Lite) process scope.
- Do not implement tests (manual checks only).

## Project Objective
Validate pH, turbidity, and conductivity against thresholds and create simple incidents.

Week 1: Spring Boot + MySQL backend
- Tables: Lines, WaterSensors, QualityReadings(lineId, pH, turbidity, conductivity, ts), Thresholds, Incidents.
- APIs: CRUD for lines/sensors/readings/thresholds/incidents.
- Queries: lines out of spec, sensor drift vs baseline (average compare).
- Console: create incidents on threshold violations.
- OOP requirement: QualityService.Check().
- Extra requirement: Java sensor simulator using scheduled generation.

Week 2: Angular frontend
- Components: LineStatus, QualityChart, ThresholdForm, IncidentModal.
- Chart: multi-series with specification bands.
- Fixes: color legend consistency and numeric precision.
- UI direction: match provided dashboard image style (top bar, side nav, cards, incident panel).

## Prompt 1 - Backend Foundation Setup
Goal: Prepare Spring app for MySQL and clear package structure.

Instruction to coding agent:
1. Configure MySQL and JPA in application.properties (datasource, dialect, ddl-auto, show-sql).
2. Enable scheduling in main Spring application class.
3. Create package folders: entity, repository, service, controller(maintain the simplicity).
4. Keep naming clear and beginner-friendly.
5. Print a short summary of files created/updated.



Done criteria:
- App starts with MySQL config in place.
- No frontend changes yet.

## Prompt 2 - Entity and Repository Layer
Goal: Build all required database models and repositories.

Instruction to coding agent:
1. Create entities:
	- Line
	- WaterSensor
	- QualityReading (lineId, pH, turbidity, conductivity, ts)
	- Threshold
	- Incident
2. Keep relations simple and explicit.
3. Create JpaRepository interfaces for each entity.
4. Add query methods for:
	- out-of-spec lines
	- readings by line/time window
	- baseline average compare for drift
5. Keep fields and annotations minimal and understandable.

Done criteria:
- Tables can be auto-created by JPA.
- Repository methods exist for required process queries.

## Prompt 3 - OOP Quality Check and Incident Logic
Goal: Implement business logic using a clear OOP service design.

Instruction to coding agent:
1. Create QualityService with Check() as central rule validation method.
2. Check() should compare latest reading against threshold values and return violation details.
3. Create IncidentService to:
	- create incidents on violations
	- prevent duplicate active incidents for same line + metric
	- allow status update (active/resolved)
4. Keep methods short and readable.

Done criteria:
- Rule evaluation is isolated in QualityService.Check().
- Incident creation flow is reusable from API and simulator.

## Prompt 4 - CRUD and Process APIs
Goal: Expose required endpoints.


Instruction to coding agent:
1. Create REST controllers with CRUD for:
	- lines
	- sensors
	- readings
	- thresholds
	- incidents
2. Add process endpoints:
	- lines out of spec
	- sensor drift vs baseline average
3. Keep request/response models simple and consistent.
4. Use numeric precision handling where needed for output clarity.

Done criteria:
- All required CRUD endpoints available.
- Both process endpoints available and readable.

## Prompt 5 - Java Sensor Simulation + Console Flow
Goal: Add auto-reading simulation and violation-driven incident creation.

Instruction to coding agent:
1. Add a scheduled Java simulator that generates realistic pH/turbidity/conductivity values every fixed interval.
2. Save generated readings into QualityReadings.
3. Call QualityService.Check() for generated readings.
4. Create incidents automatically when violations occur.
5. Add simple console logs for each simulation cycle and new incident.

Done criteria:
- Scheduled simulator runs automatically.
- Incident creation happens from simulated violations.

## Prompt 6 - Backend Completion Checkpoint
Goal: Confirm backend is complete before any frontend work.

Instruction to coding agent:
1. Run backend and verify:
	- MySQL connectivity
	- table creation for all required entities
	- CRUD endpoints respond
	- out-of-spec and drift endpoints respond
	- simulator inserts readings and triggers incidents
2. Provide a backend completion report.
3. Only after success, proceed to frontend prompts.

Done criteria:
- Backend is runnable and functionally complete.
- Backend-first rule respected.

## Prompt 7 - Angular Skeleton and Layout
Goal: Start frontend only after backend completion.

Instruction to coding agent:
1. Build missing Angular app structure in existing angular project.
2. Configure routing and base layout.
3. Implement dashboard shell similar to reference UI:
	- top header bar
	- left side navigation
	- status/content panels
	
4. Keep styling clean and simple (no over-engineering).

Done criteria:
- Angular app runs.
- Dashboard shell matches requested visual structure.

## Prompt 8 - Required Angular Components
Goal: Implement requested components and API integration.

Instruction to coding agent:
1. Create components:
	- LineStatus
	- QualityChart
	- ThresholdForm
	- IncidentModal
2. Create Angular services for backend API calls.
3. Bind live backend data to status cards, chart, thresholds, and incident modal/list.
4. Keep component logic simple and separated.

Done criteria:
- All four components implemented and integrated.

## Prompt 9 - Chart Quality and UX Fixes
Goal: Finish frontend behavior details.

Instruction to coding agent:
1. Build multi-series chart for pH, turbidity, conductivity.
2. Add spec/threshold bands to chart.
3. Fix color legend so labels and lines always match correctly.
4. Apply consistent numeric precision in chart/tooltips/cards/tables.
5. Keep UI behavior straightforward and stable.

Done criteria:
- Chart supports all three metrics and spec bands.
- Legend and precision issues are fixed.

## Prompt 10 - Final Integration Summary
Goal: Complete end-to-end flow and report outcome.

Instruction to coding agent:
1. Run backend and frontend together.
2. Verify end-to-end flow:
	- sensor simulation generates data
	- threshold violation creates incident
	- incident appears in UI
	- status/chart refresh correctly
3. Provide final implementation summary with changed files and key decisions.

Done criteria:
- Full Lite process works from simulated sensor data to incident display in Angular UI.

## Explicit Constraints to Preserve
- Backend must be completed before frontend begins.
- Framework for frontend is Angular (not React).
- MySQL is mandatory.
- Java scheduled sensor simulation is mandatory.
- Keep everything simple and fresher-friendly.
- Do not add testing implementation in this scope.