# Session 1 — How to run the mock APIs

This workspace contains a Java-based mock API server that you can run with Maven.

Java mock server
- Location: `session1/mock-api-server`
- Requirements: Java 11+ and Maven

Start the server:

```bash
cd "Automation-Engineer-Prep/session1/mock-api-server"
mvn compile exec:java
# server listens on http://localhost:4568
```

Run the integration tests (they start/stop the server automatically):

```bash
mvn test
```

Updated TestNG example
- File: `session1/examples/TestNGExample.java`
- These tests call `http://localhost:4568/api/cse` and `/api/it` and assert `facultyCount` and `availableToday`.
- To run the TestNG example, either run it from your IDE (ensure Java 11) or add TestNG to a Maven project and execute the `api` group.
