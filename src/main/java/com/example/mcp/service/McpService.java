package com.example.mcp.service;
import com.example.mcp.dto.TaskDto;
import com.example.mcp.entity.Task;
import com.example.mcp.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class McpService {
    private final TaskRepository repository;
    public McpService(TaskRepository repository) { this.repository = repository; }
    public Map<String, Object> getTaskSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("table", "tasks");
        List<Map<String,Object>> cols = new ArrayList<>();
        cols.add(col("id","LONG", false, true));
        cols.add(col("title","STRING", false, false, 255));
        cols.add(col("description","STRING", true, false));
        cols.add(col("status","ENUM", true, false, Arrays.asList("TODO","IN_PROGRESS","DONE","BLOCKED")));
        cols.add(col("priority","ENUM", true, false, Arrays.asList("LOW","MEDIUM","HIGH")));
        cols.add(col("dueDate","DATE", true, false));
        cols.add(col("assignee","STRING", true, false));
        cols.add(col("createdAt","TIMESTAMP", false, false));
        schema.put("columns", cols);
        return schema;
    }
    private Map<String,Object> col(String name, String type, boolean nullable, boolean pk) { return col(name, type, nullable, pk, null); }
    private Map<String,Object> col(String name, String type, boolean nullable, boolean pk, Object meta) {
        Map<String,Object> m = new LinkedHashMap<>(); m.put("name", name); m.put("type", type);
        m.put("nullable", nullable); m.put("primaryKey", pk); if (meta != null) m.put("meta", meta); return m; }
    public Map<String, Object> insertTasks(List<TaskDto> dtos) {
        List<Task> toSave = dtos.stream().map(this::toEntity).collect(Collectors.toList());
        List<Task> saved = repository.saveAll(toSave);
        Map<String,Object> r = new HashMap<>(); r.put("inserted", saved.size()); r.put("skipped", 0); r.put("errors", Collections.emptyList());
        return r;
    }
    private Task toEntity(TaskDto dto) {
        Task.Status status = null; Task.Priority priority = null;
        try { if (dto.getStatus() != null) status = Task.Status.valueOf(dto.getStatus()); } catch (Exception ignored) {}
        try { if (dto.getPriority() != null) priority = Task.Priority.valueOf(dto.getPriority()); } catch (Exception ignored) {}
        LocalDate due = null; try { if (dto.getDueDate() != null && !dto.getDueDate().isBlank()) due = LocalDate.parse(dto.getDueDate()); } catch (Exception ignored) {}
        return new Task(dto.getTitle(), dto.getDescription(), status, priority, due, dto.getAssignee());
    }
    public Map<String,Object> getSummary() {
        var all = repository.findAll(); long total = all.size();
        Map<String, Long> perStatus = all.stream()
                .map(t -> t.getStatus() == null ? "UNKNOWN" : t.getStatus().name())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        Map<String,Object> result = new LinkedHashMap<>(); result.put("total", total); result.put("perStatus", perStatus); return result;
    }
}
