# MyAngularApp

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.2.7.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.

# Prompt 7: Angular Skeleton and Layout

* Built missing Angular app source structure and bootstrap:
** main.ts
** index.html
** styles.css
** app.component.ts
app.config.ts
app.routes.ts
Implemented dashboard shell (top bar, side nav, status/content layout):
dashboard.component.ts
dashboard.component.html
dashboard.component.css
Simplified app to frontend-only path (removed SSR dependency path usage) in:
angular.json
Prompt 8: Required Components + API Integration

Added reusable API models/services:
wqm.models.ts
services
Added required components:
line-status
quality-chart
threshold-form
incident-modal
Wired dashboard to live backend data (lines, readings, thresholds, incidents, out-of-spec).
Prompt 9: Chart Quality + UX Fixes

Upgraded chart to full multi-series rendering:
pH, turbidity, conductivity lines.
Added threshold/spec bands for all metrics.
Fixed legend consistency (line and band colors mapped deterministically).
Applied consistent numeric precision display across chart + incident views.
Key files:
quality-chart.component.ts
quality-chart.component.html
quality-chart.component.css
incident-modal.component.ts
dashboard.component.ts
Prompt 10: End-to-End Verification + Outcome

Fixed frontend proxy target to backend port 8081:
proxy.conf.json
Confirmed backend + frontend + DB integrated path works:
simulator inserts readings,
violations create incidents,
incidents/readings visible via frontend proxy endpoints used by UI,
chart/status sources refresh with live data.
TS migration warning resolved by explicit rootDir:
tsconfig.app.json
Backend run helper adjustment:
mvnw (executable permission)
Key decisions

Used temporary MySQL container for reliable local verification without changing your local DB setup permanently.
Kept frontend logic separated: API services for data access, components for presentation, dashboard as orchestrator.
Matched runtime integration with backend port 8081 and frontend proxy to prevent silent data-path failures.
