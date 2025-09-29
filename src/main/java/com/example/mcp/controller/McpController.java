package com.example.mcp.controller;
import com.example.mcp.dto.TaskDto; import com.example.mcp.service.McpService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity; import org.springframework.validation.annotation.Validated; import org.springframework.web.bind.annotation.*;
import java.util.*; import jakarta.servlet.http.HttpServletRequest;
@RestController @RequestMapping("/")
public class McpController {
    private final McpService service; private final String apiKey; private final Logger log = LoggerFactory.getLogger(McpController.class);
    public McpController(McpService service, @Value("${mcp.apiKey}") String apiKey) { this.service = service; this.apiKey = apiKey; }
    @GetMapping("/mcp/help") public ResponseEntity<Map<String,Object>> help() {
        Map<String,Object> m = new LinkedHashMap<>(); List<Map<String,String>> endpoints = new ArrayList<>();
        endpoints.add(ep("GET","/mcp/schema/tasks","Return simplified JSON schema for tasks table"));
        endpoints.add(ep("POST","/mcp/tasks","Insert array of tasks into DB. Requires header X-MCP-KEY"));
        endpoints.add(ep("GET","/mcp/tasks/summary","Return summary stats")); m.put("endpoints", endpoints); return ResponseEntity.ok(m); }
    @GetMapping("/mcp/schema/tasks") public ResponseEntity<Map<String,Object>> schema() { return ResponseEntity.ok(service.getTaskSchema()); }
    @PostMapping("/mcp/tasks") public ResponseEntity<Object> insertTasks(
            @RequestHeader(value = "X-MCP-KEY", required = false) String key,
            @Validated @RequestBody List<TaskDto> tasks, HttpServletRequest request) {
        if (!Objects.equals(apiKey, key)) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid or missing X-MCP-KEY")); }
        log.info("AI-origin request from {}, payloadCount={}", request.getRemoteAddr(), tasks.size());
        Map<String,Object> res = service.insertTasks(tasks); res.put("source","mcp-server"); return ResponseEntity.ok(res);
    }
    @GetMapping("/mcp/tasks/summary") public ResponseEntity<Map<String,Object>> summary() { return ResponseEntity.ok(service.getSummary()); }
    private Map<String,String> ep(String method, String path, String desc) { Map<String,String> m = new LinkedHashMap<>(); m.put("method", method); m.put("path", path); m.put("desc", desc); return m; }
}
