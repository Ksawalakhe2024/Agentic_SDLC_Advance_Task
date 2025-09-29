# Minimal MCP Server for AI-Powered Data Injection (H2 DB — Local Only)

Last updated: 2025-09-29

Overview
--------
This repository contains instructions and examples for building a Minimal Model Context Protocol (MCP) server using Spring Boot and an H2 embedded/file database. The MCP server provides controlled, auditable endpoints for an AI agent to inspect schema, insert data, and fetch summary statistics for a Task Management application's data. This README covers everything you need to run the service locally with minimal installs (Java 17+, Maven or Gradle) and no external database.

Important constraint (per request)
- Use H2 DB (file mode recommended) — no PostgreSQL or remote DB required.
- Keep everything local to the developer machine.
- Minimize installed apps to avoid potential office-laptop security issues.

Goals / Success Criteria
------------------------
1. MCP Server is running and accessible locally.
2. Schema inspection works via GET /mcp/schema/tasks.
3. AI agent inserts 1000 task records via POST /mcp/tasks.
4. Summary endpoint reflects the inserted data (e.g., shows 1000 tasks split across statuses).
5. All actions are documented (prompts, AI output, and results).

Architecture (logical)
----------------------
AI Agent (Claude / GPT-4o / etc.)
    ⇅
Custom MCP Server (Spring Boot - local)
    ⇅
H2 Database (file: ./data/mcpdb) — local only (not part of main app flow)

- The MCP server acts as a controlled access layer between the agent and the DB.
- H2 is used so you don't need to run/manage a Postgres instance.

ASCII diagram
-------------
[AI Agent]
    ⇵
[Local MCP Server (Spring Boot)]
    ⇵
[H2 DB (file: ./data/mcpdb)]

PlantUML diagram (optional)
---------------------------
You can paste the following into an online PlantUML renderer or local PlantUML to generate a visual:
@startuml
actor "AI Agent" as Agent
box "Local Machine" #LightBlue
  participant "MCP Server\n(Spring Boot)" as MCP
  database "H2 DB\n(./data/mcpdb)" as H2
end box
Agent -> MCP : Inspect schema (GET /mcp/schema/tasks)
Agent -> MCP : Generate tasks and upload (POST /mcp/tasks)
MCP -> H2 : Read/Write via JPA
MCP -> Agent : Provide summary (GET /mcp/tasks/summary)
@enduml

Prerequisites (local machine)
-----------------------------
- Java 17 JDK (OpenJDK 17+) — required
- Maven 3.6+ or Gradle (optional; examples here use Maven)
- Git (optional, only if you want to clone the repo)
- (Optional) Python 3 for small utility scripts (the README provides Java client too, so Python is not required)

Why H2 file-mode?
- File-mode H2 stores DB in a file in the repo folder (`./data/mcpdb`); this persists across runs but remains local and isolated.
- No network DB server to install or expose.

Project scope & suggested project structure
-------------------------------------------
Suggested package: com.example.mcp (you can change it)
Suggested Maven structure:
- src/main/java/com/example/mcp/
  - entity/Task.java
  - repository/TaskRepository.java
  - controller/McpController.java
  - service/McpService.java
  - dto/TaskDto.java
  - client/GenerateTasksClient.java
  - Application.java
- src/main/resources/
  - application.yml (or application.properties)
- scripts/
  - generate_tasks_client.java (or Python alternative)
  - post_1000_tasks.sh (curl + Python)

Spring Boot dependency notes (pom.xml)
-------------------------------------
Minimal dependencies:
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- com.h2database:h2
- spring-boot-starter-validation (optional)
- springdoc-openapi-ui (optional, for swagger)
- lombok (optional, for brevity)

Example minimal pom.xml snippet (for reference)
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.1.0</version>
    </dependency>
</dependencies>

H2 configuration (application.yml / application.properties)
----------------------------------------------------------
Use file-mode DB (persists to ./data/mcpdb) and bind server to localhost only.

application.yml (recommended)
server:
  port: 8080
  address: 127.0.0.1

spring:
  datasource:
    url: jdbc:h2:file:./data/mcpdb;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# Enable H2 console for local debugging (only accessible locally)
spring.h2.console.enabled: true
spring.h2.console.path: /h2-console

Quick application startup (local)
--------------------------------
1. Build:
   - Using Maven:
     - mvn clean package -DskipTests
   - Or use your IDE to run Application.java.

2. Start:
   - java -jar target/mcp-server-0.0.1-SNAPSHOT.jar
   - Or mvn spring-boot:run

3. The server listens only on 127.0.0.1:8080 by default.

H2 console
----------
- URL: http://127.0.0.1:8080/h2-console
- JDBC URL: jdbc:h2:file:./data/mcpdb
- User: sa
- Password: (empty)
- Use this only locally for verification.

Data model (Task schema)
------------------------
A simple Task entity suitable to a Task Management app:

- id (Long) — primary key
- title (String, required)
- description (String, optional)
- status (ENUM — TODO, IN_PROGRESS, DONE, BLOCKED)
- priority (ENUM — LOW, MEDIUM, HIGH) or integer
- dueDate (LocalDate)
- createdAt (Instant)
- updatedAt (Instant)
- assignee (String)
- tags (String or JSON text) — optional

Example Java entity (Task.java)
[See src/main/java/com/example/mcp/entity/Task.java]

Repository (TaskRepository.java)
[See src/main/java/com/example/mcp/repository/TaskRepository.java]

MCP endpoints (controller / behavior)
------------------------------------
- GET /mcp/help
- GET /mcp/schema/tasks
- POST /mcp/tasks
- GET /mcp/tasks/summary

Validation & Safety (local-only)
-------------------------------
- server.address=127.0.0.1 -> Prevent public exposure.
- (Optional) Add a simple token header check: require X-MCP-KEY header to match a strong value stored in application.yml.
- Log AI-originated operations.

Generating and inserting 1000 tasks (Java client)
-------------------------------------------------
See src/main/java/com/example/mcp/client/GenerateTasksClient.java and run:
  mvn exec:java -Dexec.mainClass="com.example.mcp.client.GenerateTasksClient"

Verifying results
-----------------
- GET /mcp/tasks/summary should show total 1000.
- H2 console: SELECT COUNT(*) FROM tasks; SELECT status, COUNT(*) FROM tasks GROUP BY status;

Sample prompts for AI agent
---------------------------
"Please inspect the task schema at http://127.0.0.1:8080/mcp/schema/tasks. Then generate and insert 1000 diverse tasks with random statuses, titles, and due dates using the POST http://127.0.0.1:8080/mcp/tasks endpoint. Use the header X-MCP-KEY with value 'replace_with_secure_local_only_key'. After inserting, verify success using GET http://127.0.0.1:8080/mcp/tasks/summary and return a count of tasks per status and total."

OpenAPI / Swagger (optional)
----------------------------
- Add springdoc-openapi to enable /swagger-ui.html

Troubleshooting
---------------
- Ensure ./data exists and is writable.
- 401 on POST: header X-MCP-KEY mismatch.
- Validation errors: title is required.