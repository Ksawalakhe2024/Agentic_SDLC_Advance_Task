# Knowledge Transfer (KT) Document
Project: MCP Server + Local Chat UI (Spring Boot + H2)
Source: [Agentic_SDLC_Advance_Task](https://github.com/Ksawalakhe2024/Agentic_SDLC_Advance_Task)

1) Purpose and Audience
- Purpose: This document explains, in simple and detailed terms, what this project is, how it works, and how to install, run, and use it—so a non-technical person can understand and follow along.
- Audience: Non-technical stakeholders, new team members, QA, and anyone who needs to operate or demo the system on a local machine.

2) What This Project Is (Plain-English Overview)
- Think of this as a small, private app that runs on your laptop only. It has:
  - A “Server” (the brain) that handles requests and stores data.
  - A simple “Client” (a web page chat window) where you type messages and get results.
  - A built-in “Database” (H2) that saves information to a file on your computer, so your data persists between runs.
- What you can do:
  - Ask the system what kind of “task” information it stores (the “schema”).
  - Tell it to generate a lot of tasks (for example, 1000) and save them.
  - See a summary of tasks by their status (like TODO, DONE, etc.).
- Everything runs locally (on 127.0.0.1), with no internet services or external databases required.

3) Key Concepts (Client, Server, Database, API)
- Client (Your Web Page)
  - The “client” is what you see and use: a local web page (chat UI) in your browser at http://127.0.0.1:8080/.
  - You type simple commands like “show schema” or “generate 1000 tasks,” and it shows the answers.
- Server (The App’s Brain)
  - The “server” is a program that listens for requests and does the work (generating tasks, saving data, giving summaries).
  - It runs on your machine, at address 127.0.0.1 (your computer) and port 8080.
- Database (H2, File-Based)
  - A database stores information so it’s not lost when you close the app.
  - H2 is a lightweight database that stores data in a local file at ./data/mcpdb on your computer.
- API and Endpoints (Doors into the Server)
  - An API is a set of rules/addresses the client can “call” to ask for things.
  - Endpoints are specific URLs like /mcp/tasks or /mcp/tasks/summary where the server listens for requests.

4) How the System Works (End-to-End)
There are two main ways to interact:
- Option A: Use the Chat UI (simple for anyone)
  1. You open the web page at http://127.0.0.1:8080/.
  2. You type messages like “show schema” or “generate 1000 tasks”.
  3. The page sends your message to the server’s /chat endpoint.
  4. The server’s “Chat Agent” understands common phrases and calls the core service to do the job.
  5. The service talks to the database (H2) to save or read data.
  6. A reply is shown back in the chat window.
- Option B: Use the REST API (for automated tools or scripts)
  1. A program/script sends HTTP calls to the server (e.g., POST /mcp/tasks).
  2. The server validates the request. For some actions (like adding tasks), it checks an API key.
  3. The server saves or reads data from H2 and returns a response.

Simple analogy:
- Client is like a waiter taking your order.
- Server is like the kitchen that cooks your order.
- Database is like the pantry/fridge storing ingredients.

5) What You Can Do With It (Features)
- See a description of “Task” data fields (the schema).
- Generate and insert many tasks (like 1000) with random titles, statuses, and dates.
- View summary totals of tasks by status (e.g., how many are DONE, TODO).
- Use a built-in web chat interface for simple commands.
- Use a basic security gate (API key) for external writes, to avoid accidental misuse.

6) System Components (Plain-English)
- Web UI (static/index.html)
  - A simple web page with a chat box where you type instructions.
  - It sends your messages to the /chat endpoint on the server.
- ChatController and ChatAgentService
  - ChatController receives your message.
  - ChatAgentService is a simple “helper” that recognizes phrases like “schema,” “generate N,” and “summary,” and calls the core logic.
- McpController and McpService
  - McpController holds the official API endpoints (like /mcp/tasks).
  - McpService contains the core logic to get the schema, insert tasks, and compute summaries.
- TaskRepository and Task Entity
  - TaskRepository is how the app reads/writes tasks to the database.
  - Task is the data model describing what a “task” is (its fields like title, status, due date, etc.).
- H2 Database (file mode)
  - Saves task data to a local file on your machine: ./data/mcpdb.

7) Project Structure (What’s Where)
- src/main/java/com/example/mcp
  - Application.java — Starts the server.
  - controller/
    - McpController.java — API endpoints (help, schema, add tasks, summary).
    - ChatController.java — Chat endpoint for the UI.
  - service/
    - McpService.java — Core logic (schema, insert, summary).
    - ChatAgentService.java — Understands chat phrases and calls McpService.
  - entity/
    - Task.java — The task data model saved in the database.
  - repository/
    - TaskRepository.java — Database access for Task.
  - dto/
    - TaskDto.java — Input shape for adding tasks via API.
    - ChatRequest.java, ChatResponse.java — Data for chat messages.
  - client/
    - GenerateTasksClient.java — A simple Java program to send 1000 tasks via API.
- src/main/resources
  - application.yml — Configuration (port, database settings, API key).
  - static/index.html — The chat UI web page.
- pom.xml — Build configuration for Maven.
- README.md — Original project readme.
- .gitignore — Files to ignore in version control.

8) Requirements to Run (Prerequisites)
- Java 17 or newer
- Maven 3.6 or newer
- An internet browser (Chrome, Edge, Safari, Firefox)

9) Install and Run: Step-by-Step
A. Get the Code
- Download or clone the repository: [Agentic_SDLC_Advance_Task](https://github.com/Ksawalakhe2024/Agentic_SDLC_Advance_Task)
  - If you know Git:
    - git clone https://github.com/Ksawalakhe2024/Agentic_SDLC_Advance_Task.git
    - cd Agentic_SDLC_Advance_Task

B. Build the Server (Terminal 1)
- Open a terminal/command prompt in the project folder.
- Run:
```
mvn clean package -DskipTests spring-boot:repackage
```
- This compiles the code and creates a runnable file at target/mcp-h2-0.0.1-SNAPSHOT.jar

Optional: Verify the JAR is bootable
- Windows:
```
jar tf target/mcp-h2-0.0.1-SNAPSHOT.jar | findstr BOOT-INF
```
- macOS/Linux:
```
jar tf target/mcp-h2-0.0.1-SNAPSHOT.jar | grep BOOT-INF
```

C. Start the Server (Terminal 1)
- Run:
```
java -jar target/mcp-h2-0.0.1-SNAPSHOT.jar
```
- What you should see:
  - Logs showing Spring Boot starting
  - A message indicating it’s listening on 127.0.0.1:8080
  - No errors

D. Use the Chat UI (Easiest Way)
- Open your browser and go to: http://127.0.0.1:8080/
- In the chat box, try typing:
  - show schema
  - generate 1000 tasks
  - show summary
- The page will show responses accordingly.

E. Use the Java Client (Terminal 2)
- Open a second terminal in the same project folder.
- Set an API key in your environment (choose any string—local only):
  - Windows PowerShell:
```
$env:MCP_API_KEY="your_key"
```
  - Windows cmd.exe:
```
set MCP_API_KEY=your_key
```
  - macOS/Linux:
```
export MCP_API_KEY="your_key"
```
- Run the client:
```
mvn exec:java
```
- Optional (explicit main class, if needed):
```
mvn exec:java -Dexec.mainClass=com.example.mcp.client.GenerateTasksClient
```
- The client will post 1000 tasks to the server using the API.

F. Explore the Database via H2 Console (Optional)
- Visit: http://127.0.0.1:8080/h2-console
- Fill in:
  - JDBC URL: jdbc:h2:file:./data/mcpdb
  - User: sa
  - Password: leave blank (empty)
- Click “Connect” to view tables and data.

10) Configuration Explained (application.yml)
This file controls how the app runs. Key parts:
- Server
  - port: 8080 (where it listens)
  - address: 127.0.0.1 (local only—no outside access)
- Database (H2)
  - url: jdbc:h2:file:./data/mcpdb (file-based database)
  - username: sa, password: empty
  - JPA ddl-auto: update (auto-creates/updates tables)
- Logging
  - Shows INFO generally, DEBUG for this app’s code
- API Key
  - mcp.apiKey: ${MCP_API_KEY:replace_with_secure_local_only_key}
  - Meaning: If MCP_API_KEY environment variable is set, it uses that; otherwise uses a placeholder.
  - Used to protect external write endpoint (/mcp/tasks).

11) Available Endpoints (What They Do)
- GET /mcp/help
  - Simple description of the service.
- GET /mcp/schema/tasks
  - Returns the task “schema” (what fields a task has).
- POST /mcp/tasks
  - Adds tasks to the database.
  - Requires header: X-MCP-KEY set to the same value as mcp.apiKey.
  - Body: An array of TaskDto objects (task data).
- GET /mcp/tasks/summary
  - Returns totals of tasks grouped by status (like TODO, DONE).
- POST /chat
  - Used by the web chat UI.
  - You send a message like “show summary” and it responds accordingly.
  - The chat agent calls services internally (no API key required).

12) The Data Model (Task, in Simple Terms)
Each task includes fields like:
- id — A unique number assigned automatically.
- title — Short name of the task (required).
- description — Details about the task (optional).
- status — One of TODO, IN_PROGRESS, DONE, BLOCKED.
- priority — One of LOW, MEDIUM, HIGH.
- dueDate — When the task is due.
- assignee — Who it’s assigned to.
- createdAt — When the task was created.
- updatedAt — When it was last updated.

13) How We Are Working (Typical Workflow)
- For a non-technical demo:
  1. Start the server (Terminal 1).
  2. Open the chat UI in your browser.
  3. Ask the system to “show schema” to understand the task fields.
  4. Ask it to “generate 1000 tasks” to populate sample data.
  5. Ask for “show summary” to see counts by status.
  6. Optionally, open the H2 console and browse data.
- For programmatic use:
  1. Start the server (Terminal 1).
  2. Set MCP_API_KEY in your environment.
  3. Use the Java client or your own script to call POST /mcp/tasks with header X-MCP-KEY.
  4. Check results with GET /mcp/tasks/summary.

14) Data Flows (Step-by-Step)
A. Chat UI Flow
1) Browser sends your text to POST /chat.
2) ChatController passes it to ChatAgentService.
3) ChatAgentService interprets commands:
   - “show schema” → McpService.getTaskSchema() → returns description.
   - “generate 1000 tasks” → creates random tasks → McpService.insertTasks() → saves to H2.
   - “show summary” → McpService.getSummary() → returns totals.
4) Response shown in the chat window.

B. External Client Flow
1) Client calls GET /mcp/schema/tasks to learn the format.
2) Client sends POST /mcp/tasks with X-MCP-KEY and task data to save.
3) Client calls GET /mcp/tasks/summary to see totals.

15) Security Notes (Local-Only by Default)
- The app listens on 127.0.0.1 only, which means only your computer can access it.
- The write endpoint (/mcp/tasks) requires X-MCP-KEY for external clients to avoid accidental misuse.
- Do not expose this app publicly without:
  - Adding TLS (HTTPS).
  - Stronger authentication/authorization.

16) Running Examples (Copy-Paste Friendly)
A. Chat UI
- Build and start server (Terminal 1):
```
mvn clean package -DskipTests spring-boot:repackage
java -jar target/mcp-h2-0.0.1-SNAPSHOT.jar
```
- Open browser: http://127.0.0.1:8080/
- Try messages: “show schema”, “generate 1000 tasks”, “show summary”

B. cURL (Command-Line)
- Help:
```
curl http://127.0.0.1:8080/mcp/help
```
- Schema:
```
curl http://127.0.0.1:8080/mcp/schema/tasks
```
- Insert (example with empty array just to test access; replace with actual tasks):
```
curl -X POST http://127.0.0.1:8080/mcp/tasks \
  -H "X-MCP-KEY: $MCP_API_KEY" \
  -H "Content-Type: application/json" \
  -d "[]"
```
- Summary:
```
curl http://127.0.0.1:8080/mcp/tasks/summary
```

C. Java Client (Terminal 2)
- Set environment variable (choose one of these based on your OS/shell):
  - Windows PowerShell:
```
$env:MCP_API_KEY="your_key"
```
  - Windows cmd.exe:
```
set MCP_API_KEY=your_key
```
  - macOS/Linux:
```
export MCP_API_KEY="your_key"
```
- Run:
```
mvn exec:java
```
- Optional explicit main:
```
mvn exec:java -Dexec.mainClass=com.example.mcp.client.GenerateTasksClient
```

17) Troubleshooting (Common Issues and Fixes)
- Error: “no main manifest attribute”
  - Cause: Running a non-repackaged jar.
  - Fix: Build with Spring Boot repackage. Run:
```
mvn clean package -DskipTests spring-boot:repackage
java -jar target/mcp-h2-0.0.1-SNAPSHOT.jar
```
- Error: 401 Unauthorized when calling POST /mcp/tasks
  - Cause: Missing or incorrect X-MCP-KEY.
  - Fix: Ensure header X-MCP-KEY matches application.yml’s mcp.apiKey (or your MCP_API_KEY environment variable).
- Maven exec plugin error like “Unknown lifecycle phase …mainClass…”
  - Cause: Wrong hyphen or command format.
  - Fix: Use ASCII hyphens. Prefer:
```
mvn exec:java
```
  - Or:
```
mvn exec:java -Dexec.mainClass=com.example.mcp.client.GenerateTasksClient
```
- Port 8080 already in use
  - Cause: Another app is using port 8080.
  - Fix: Stop the other app or change the port in application.yml and restart.
- H2 Console can’t connect
  - Check the values:
    - JDBC URL: jdbc:h2:file:./data/mcpdb
    - User: sa
    - Password: empty
  - Ensure the server is running and you are opening http://127.0.0.1:8080/h2-console
- Java version errors
  - Ensure Java 17+ is installed and active on your PATH.

18) Extending or Modifying the System (High-Level Tips)
- Add new fields to Task:
  - Update Task.java and TaskDto.java.
  - The database schema will adjust automatically (ddl-auto: update).
- Add new chat commands:
  - Extend ChatAgentService to recognize new phrases and call McpService methods.
- Add new endpoints:
  - Add methods in McpController and supporting logic in McpService.

19) Frequently Asked Questions (FAQ)
- Q: Do I need internet?
  - A: No. Everything runs locally.
- Q: Where is my data stored?
  - A: In a file-based H2 database at ./data/mcpdb on your machine.
- Q: Do I need an API key for the chat UI?
  - A: No. The chat UI uses internal calls. API key is needed only for external write endpoint (/mcp/tasks).
- Q: Can I run it on a different port?
  - A: Yes. Change server.port in application.yml (e.g., 9090), then restart the server.
- Q: Is this production-ready?
  - A: No. It’s intended for local development and testing. Don’t expose publicly without proper security (TLS, strong auth).

20) Glossary (Simple Definitions)
- Client: What the user interacts with (the browser page).
- Server: The program that processes requests and talks to the database.
- Database (H2): A place where data is stored on disk.
- API/Endpoint: A specific URL on the server where actions happen.
- Schema: A description of what data fields exist and how they’re structured.
- Status: The state of a task (e.g., TODO, DONE).
- Summary: A report showing totals (e.g., number of tasks in each status).

21) Quick Reference (All Key Values)
- Server URL: http://127.0.0.1:8080/
- H2 Console: http://127.0.0.1:8080/h2-console
- H2 JDBC URL: jdbc:h2:file:./data/mcpdb
- H2 User: sa, Password: empty
- Protected Header: X-MCP-KEY
- Environment Variable (optional): MCP_API_KEY
- Main JAR: target/mcp-h2-0.0.1-SNAPSHOT.jar

22) Appendix: Example Task JSON (for POST /mcp/tasks)
Send an array of tasks (TaskDto format). Title is required; other fields optional.
```json
[
  {
    "title": "Prepare project plan",
    "description": "Outline milestones and deliverables",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2025-10-15",
    "assignee": "Alice"
  },
  {
    "title": "Set up CI",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM",
    "assignee": "Bob"
  }
]
```
