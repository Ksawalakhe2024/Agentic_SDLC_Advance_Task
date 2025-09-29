package com.example.mcp.controller;

import com.example.mcp.dto.ChatRequest;
import com.example.mcp.dto.ChatResponse;
import com.example.mcp.service.ChatAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

    private final ChatAgentService agent;

    public ChatController(ChatAgentService agent) {
        this.agent = agent;
    }

    // Chat endpoint consumed by the web UI (same origin)
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest req) {
        var result = agent.handle(req.getMessage());
        return ResponseEntity.ok(new ChatResponse(result.reply, result.data));
    }
}