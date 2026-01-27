# Java Mock API Server

This is a minimal Java-based mock API server using Spark Java. It exposes two endpoints:

- `GET /api/cse` — returns CSE department info
- `GET /api/it` — returns IT department info

Both responses include `facultyCount` and `availableToday` fields.

Run locally (requires Java 11+ and Maven):

```bash
cd "Automation-Engineer-Prep/session 1/mock-api-server"
mvn compile exec:java
```

Run tests:

```bash
mvn test
```
