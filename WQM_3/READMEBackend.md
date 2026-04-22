# WQM_3 Backend Setup

This is the Spring Boot backend for the Water Quality Monitor project.

## Requirements
- Java 17+
- Maven
- MySQL 8+
- MySQL database: `quality_db`
- MySQL username/password in `src/main/resources/application.properties`

## Application Properties
Check these values in `src/main/resources/application.properties`:
- `spring.datasource.url=jdbc:mysql://localhost:3306/quality_db`
- `spring.datasource.username=root`
- `spring.datasource.password=root`
- `server.port=8081`

## How To Run In STS
1. Open STS.
2. Import the project as an existing Maven project.
3. Select the `WQM_3` folder.
4. Wait for Maven dependencies to download.
5. Run the main class:
   - `com.wqm.Wqm3Application`
6. Open:
   - `http://localhost:8081`

If STS asks for a run configuration, choose **Spring Boot App**.

## How To Run In IntelliJ IDEA
1. Open IntelliJ IDEA.
2. Select **Open** and choose the `WQM_3` folder.
3. Let IntelliJ import it as a Maven project.
4. Make sure the project SDK is set to Java 17 or higher.
5. Run the main class:
   - `src/main/java/com/wqm/Wqm3Application.java`
6. Open:
   - `http://localhost:8081`

## How To Run From Terminal
From inside the `WQM_3` folder, run:

```bash
sh mvnw spring-boot:run
```

If the wrapper is executable, this also works:

```bash
./mvnw spring-boot:run
```

## Simple Working Flow
1. Spring Boot starts on port `8081`.
2. The app connects to MySQL and creates tables automatically.
3. The scheduled simulator starts generating water quality readings.
4. Each reading is checked by `QualityService.Check()`.
5. If a value goes outside the threshold, `IncidentService` creates an incident.
6. You can view data through the REST endpoints.

## Main Endpoints
- `GET /api/lines`
- `GET /api/sensors`
- `GET /api/readings`
- `GET /api/thresholds`
- `GET /api/incidents`
- `GET /api/process/lines/out-of-spec`
- `GET /api/process/sensor-drift?lineId=1&startTs=...&endTs=...`

## Notes
- The simulator also creates demo data if the database starts empty.
- If MySQL is not running, the backend will not start.
- This README is only for the backend inside `WQM_3`.

## Connectivity Troubleshooting (Frontend <-> Backend)
1. Confirm MySQL is running and reachable on `localhost:3306`.
2. Start backend and verify API health quickly:
   - `curl http://localhost:8081/api/lines`
3. Start frontend with proxy enabled from `frontend/`:
   - `npm start`
4. Verify frontend proxy target in `frontend/proxy.conf.json` points to `http://localhost:8081`.
5. If you run frontend without Angular dev proxy, backend CORS now allows `http://localhost:4200` for `/api/**`.

Environment overrides are supported for local setup differences:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`
