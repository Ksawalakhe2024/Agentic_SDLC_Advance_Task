package com.example.mcp.client;
import com.example.mcp.dto.TaskDto; import com.fasterxml.jackson.databind.ObjectMapper; import java.net.URI; import java.net.http.HttpClient; import java.net.http.HttpRequest; import java.net.http.HttpResponse; import java.time.LocalDate; import java.util.*; import java.util.stream.Collectors; import java.util.stream.IntStream;
public class GenerateTasksClient {
    private static final ObjectMapper OM = new ObjectMapper();
    public static void main(String[] args) throws Exception {
        String url = "http://127.0.0.1:8080/mcp/tasks"; String apiKey = "replace_with_secure_local_only_key"; HttpClient client = HttpClient.newHttpClient();
        List<TaskDto> tasks = IntStream.range(0, 1000).mapToObj(i -> { TaskDto t = new TaskDto(); t.setTitle("Auto Task " + i); t.setDescription("Auto-generated task number " + i); String[] statuses = {"TODO","IN_PROGRESS","DONE","BLOCKED"}; String[] priorities = {"LOW","MEDIUM","HIGH"}; t.setStatus(statuses[i % statuses.length]); t.setPriority(priorities[i % priorities.length]); t.setDueDate(LocalDate.now().plusDays(i % 30).toString()); t.setAssignee("user" + (i % 10)); return t; }).collect(Collectors.toList());
        String payload = OM.writeValueAsString(tasks);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type","application/json").header("X-MCP-KEY", apiKey).POST(HttpRequest.BodyPublishers.ofString(payload)).build();
        System.out.println("Posting 1000 tasks to " + url); HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString()); System.out.println("Status: " + resp.statusCode()); System.out.println("Body: " + resp.body());
        HttpRequest sreq = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:8080/mcp/tasks/summary")).GET().build(); HttpResponse<String> sresp = client.send(sreq, HttpResponse.BodyHandlers.ofString()); System.out.println("Summary: " + sresp.body());
    }
}