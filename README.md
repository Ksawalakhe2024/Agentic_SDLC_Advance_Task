# MCP Server + Local Chat UI (Spring Boot + H2)

Overview
--------
This project provides a minimal, local-only MCP-style server with a built-in chat UI. It lets you:
- Inspect the task schema
- Generate and insert bulk tasks (e.g., 1000) into an H2 file database
- View summary statistics

It runs entirely on your machine using Spring Boot and H2 (file mode). No external databases or services are required.

What’s working now (tested)
---------------------------
- Executable Spring Boot JAR (Spring Boot repackage configured)
- H2 in file-mode at ./data/mcpdb
- REST endpoints under /mcp/…
- Java client that POSTS 1000 tasks
- Local web chat UI at http://127.0.0.1:8080/
- Optional API key gate for external write endpoint

Project structure
-----------------
- src/main/java/com/example/mcp
  - Application.java
  - controller/
    - McpController.java           (REST endpoints)
    - ChatController.java          (Chat endpoint for UI)
  - service/
    - McpService.java              (schema, insert, summary)
    - ChatAgentService.java        (simple in-app agent for chat UI)
  - entity/
    - Task.java
  - repository/
    - TaskRepository.java
  - dto/
    - TaskDto.java
    - ChatRequest.java
    - ChatResponse.java
  - client/
    - GenerateTasksClient.java     (posts 1000 tasks)
- src/main/resources
  - application.yml
  - static/
    - index.html                   (chat UI)
- pom.xml
- README.md
- .gitignore

Prerequisites
-------------
- Java 17+
- Maven 3.6+

Build and run (server)
----------------------
- Build a bootable jar (repackage is already configured in pom.xml):
  - mvn clean package -DskipTests spring-boot:repackage
- Run the application:
  - java -jar target/mcp-h2-0.0.1-SNAPSHOT.jar
- Server is local-only by default: http://127.0.0.1:8080

H2 console
----------
- http://127.0.0.1:8080/h2-console
- JDBC URL: jdbc:h2:file:./data/mcpdb
- User: sa
- Password: (empty)

API key (optional, recommended)
-------------------------------
- External writes to /mcp/tasks require header X-MCP-KEY matching application.yml (mcp.apiKey).
- For convenience, you can set it via environment variable:
  - In application.yml: mcp.apiKey: ${MCP_API_KEY:replace_with_secure_local_only_key}
  - PowerShell: $env:MCP_API_KEY="your_key"
  - macOS/Linux: export MCP_API_KEY="your_key"
- The in-app chat UI does not use this header; it calls services directly and remains local-only.

Endpoints
---------
- GET /mcp/help — brief description
- GET /mcp/schema/tasks — simplified schema description
- POST /mcp/tasks — insert array of TaskDto (requires X-MCP-KEY)
- GET /mcp/tasks/summary — totals by status
- POST /chat — chat endpoint used by the web UI

Run the chat UI
---------------
- After starting the server, open http://127.0.0.1:8080/
- Try messages:
  - show schema
  - generate 1000 tasks
  - show summary
  - Please inspect the task schema at /mcp/schema/tasks. Then generate and insert 1000 diverse tasks with random statuses, titles, and due dates using the /mcp/tasks endpoint.

Java client to insert 1000 tasks
--------------------------------
- In a second terminal from the project root:
  - Ensure the server is running
  - Option A (configured main): mvn exec:java
  - Option B (explicit main): mvn exec:java -Dexec.mainClass=com.example.mcp.client.GenerateTasksClient
- If using the external REST endpoint directly, ensure the header X-MCP-KEY matches application.yml’s mcp.apiKey (or MCP_API_KEY env var).

Configuration (application.yml)
-------------------------------
```yaml
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
mcp:
  apiKey: ${MCP_API_KEY:replace_with_secure_local_only_key}
logging:
  level:
    root: INFO
    com.example.mcp: DEBUG
spring.h2.console.enabled: true
spring.h2.console.path: /h2-console
```

High-Level Design (HLD)
-----------------------
Components:
- Web UI (static index.html)
  - Renders a simple chat interface, posts messages to /chat.
- ChatController
  - Receives chat requests, delegates to ChatAgentService.
- ChatAgentService
  - Lightweight, rule-based agent; understands “schema”, “generate N”, “summary”. Uses McpService.
- McpController
  - Public REST endpoints for schema, insert, and summary (agent-accessible via HTTP if desired).
- McpService
  - Core domain logic: returns schema, performs inserts, computes summary.
- TaskRepository (JPA)
  - CRUD over Task entity.
- H2 Database (file)
  - Local persistence at ./data/mcpdb.

Data flow (typical paths):
1) Chat UI -> /chat -> ChatAgentService -> McpService -> H2 (insert/read) -> Chat UI reply
2) External client -> /mcp/tasks (X-MCP-KEY) -> McpService -> H2 -> 200 with inserted count
3) External client -> /mcp/schema/tasks or /mcp/tasks/summary -> McpService -> H2 -> JSON result

Non-functional notes:
- Local-only binding (127.0.0.1) by default
- API key gate for write endpoint to reduce accidental misuse
- Minimal footprint; Java 17 + Maven only

Low-Level Design (LLD)
----------------------
Key classes and responsibilities:
- Task (entity)
  - Fields: id, title, description, status (TODO/IN_PROGRESS/DONE/BLOCKED), priority (LOW/MEDIUM/HIGH), dueDate, assignee, createdAt, updatedAt
  - Lifecycle hooks: @PrePersist assigns defaults and timestamps; @PreUpdate maintains updatedAt
- TaskRepository
  - JpaRepository<Task, Long>
- TaskDto
  - Input DTO for POST /mcp/tasks (validated: title required)
- McpService
  - getTaskSchema(): Map<String,Object>
  - insertTasks(List<TaskDto>): Map<String,Object> with inserted count
  - getSummary(): total and counts per status
- McpController
  - /mcp/help, /mcp/schema/tasks, /mcp/tasks (POST; requires X-MCP-KEY), /mcp/tasks/summary
- ChatAgentService
  - handle(String): AgentResult { reply, data }
  - generateTasks(int): List<TaskDto> with randomized contents
- ChatController
  - /chat POST => ChatResponse { reply, data }
- ChatRequest/ChatResponse
  - Simple request/response DTOs for chat endpoint

Sequence (Generate 1000 tasks via Chat UI):
1) Browser -> POST /chat { message: "generate 1000 tasks" }
2) ChatController -> ChatAgentService.handle
3) ChatAgentService.generateTasks(1000) -> McpService.insertTasks -> H2
4) ChatAgentService -> McpService.getSummary -> H2
5) ChatController -> Browser { reply, summary }

Sequence (External client using REST):
1) Client -> GET /mcp/schema/tasks
2) Client -> POST /mcp/tasks (X-MCP-KEY) with 1000 TaskDto
3) Client -> GET /mcp/tasks/summary

Troubleshooting
--------------
- Error: "no main manifest attribute"
  - Fix: Build with repackage (already configured). Use mvn clean package -DskipTests and run the jar.
- Error: 401 Unauthorized on POST /mcp/tasks
  - Ensure the X-MCP-KEY header matches mcp.apiKey (or MCP_API_KEY env var).
- Exec plugin error (Unknown lifecycle phase …mainClass…)
  - Ensure dashes are ASCII hyphens. Or run mvn exec:java (the main class is configured in pom.xml).

Sample curl calls
-----------------
- Help: curl http://127.0.0.1:8080/mcp/help
- Schema: curl http://127.0.0.1:8080/mcp/schema/tasks
- Insert (example empty array):
  - curl -X POST http://127.0.0.1:8080/mcp/tasks -H "X-MCP-KEY: $MCP_API_KEY" -H "Content-Type: application/json" -d "[]"
- Summary: curl http://127.0.0.1:8080/mcp/tasks/summary

License & notes
---------------
Local development and testing only. Do not expose the server publicly without adding transport security (TLS) and stronger auth.
