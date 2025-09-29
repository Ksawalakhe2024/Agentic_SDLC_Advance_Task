# Minimal MCP Server for AI-Powered Data Injection (H2 - Local Only)

Last updated: 2025-09-29

Overview
--------
This project implements a Minimal Model Context Protocol (MCP) server built with Spring Boot and H2 (file mode). The service provides a controlled, local-only API for an AI agent to inspect the Task schema, insert tasks, and retrieve summary statistics.

Key constraints and rationale
- Uses H2 in file mode (local only) — no external DB or network setup.
- Server binds to 127.0.0.1 by default (local-only).
- Lightweight API key check (X-MCP-KEY) to prevent accidental remote use.
- Minimal runtime requirements: Java 17 and Maven.

Goals / Success Criteria
1. MCP Server is running and accessible locally.
2. Schema inspection works via GET /mcp/schema/tasks.
3. AI agent (or client) inserts 1000 task records via POST /mcp/tasks.
4. Summary endpoint reflects the inserted data.
5. Audit/logging and README document all actions, sample prompts, and results.

Project structure
-----------------
- src/main/java/com/example/mcp
  - Application.java
  - controller/McpController.java
  - service/McpService.java
  - entity/Task.java
  - repository/TaskRepository.java
  - dto/TaskDto.java
  - client/GenerateTasksClient.java
- src/main/resources
  - application.yml
- pom.xml
- README.md
- .gitignore

Prerequisites (local dev)
-------------------------
- Java 17 JDK (OpenJDK 17+)
- Maven 3.6+
- Git (optional, for pushing)
- No database installs required

Build & run (quick)
-------------------
1. Build
   mvn clean package -DskipTests

2. Run
   java -jar target/mcp-h2-0.0.1-SNAPSHOT.jar

3. Server will be reachable at http://127.0.0.1:8080

H2 Console
----------
- Enabled and local-only: http://127.0.0.1:8080/h2-console
- JDBC URL: jdbc:h2:file:./data/mcpdb
- User: sa
- Password: (empty)

Configuration
-------------
Edit `src/main/resources/application.yml` to change API key (mcp.apiKey) if desired. Default binds server to 127.0.0.1.

API Endpoints
-------------
- GET /mcp/help
  - Short, machine/agent-readable description of endpoints.

- GET /mcp/schema/tasks
  - Returns a simplified JSON description of the `tasks` table (fields, types, enums).

- POST /mcp/tasks
  - Accepts JSON array of TaskDto objects and inserts them. Requires header `X-MCP-KEY: <apiKey>`.
  - Example payload (array of objects):
    {
      "title":"Task 1",
      "description":"Auto-generated",
      "status":"TODO",
      "priority":"MEDIUM",
      "dueDate":"2025-10-10",
      "assignee":"Alice"
    }

- GET /mcp/tasks/summary
  - Returns total count and counts per status.

Security & local-only controls
------------------------------
- server.address = 127.0.0.1 by default.
- X-MCP-KEY header required for POST /mcp/tasks (set in application.yml).
- Keep the DB file local (./data/mcpdb).

Sample prompt for an AI agent
-----------------------------
"Please inspect the task schema at http://127.0.0.1:8080/mcp/schema/tasks. Then generate and insert 1000 diverse tasks with random statuses, titles, and due dates using POST http://127.0.0.1:8080/mcp/tasks. Include header `X-MCP-KEY: <value>` with the configured key. After insertion, validate success via GET http://127.0.0.1:8080/mcp/tasks/summary and respond with counts per status."

Generate & insert 1000 tasks (Java client)
-----------------------------------------
Use the provided `GenerateTasksClient` (src/main/java/com/example/mcp/client/GenerateTasksClient.java). It uses java.net.http and Jackson to generate 1000 tasks and POST them. Run after server is started:

  mvn exec:java -Dexec.mainClass="com.example.mcp.client.GenerateTasksClient"

Verification
------------
1. GET /mcp/tasks/summary -> expect total 1000.
2. H2 console -> SELECT COUNT(*) FROM tasks; SELECT status, COUNT(*) FROM tasks GROUP BY status;
3. Server logs will show audit entries for inserted count.

What to include in deliverables
-------------------------------
- Source code (Spring Boot application)
- README (this file)
- Proof (logs or screenshot) showing 1000 tasks in DB (capture after performing the test locally)

Troubleshooting
---------------
- If H2 fails to open ./data/mcpdb, ensure the directory exists and is writable.
- If POST fails with 401, ensure header X-MCP-KEY matches application.yml.

License & notes
---------------
This project is intended for local development and testing only. Do not expose the server to public networks without additional security (TLS, OAuth, IP filters).
