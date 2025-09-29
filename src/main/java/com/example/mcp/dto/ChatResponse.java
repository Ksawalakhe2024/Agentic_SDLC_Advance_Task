package com.example.mcp.dto;

import java.util.Map;

public class ChatResponse {
    private String reply;
    private Map<String, Object> data;

    public ChatResponse() { }
    public ChatResponse(String reply, Map<String, Object> data) {
        this.reply = reply;
        this.data = data;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}