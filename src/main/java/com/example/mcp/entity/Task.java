package com.example.mcp.entity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
@Entity
@Table(name = "tasks")
public class Task {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(length = 4000)
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    private LocalDate dueDate;
    private String assignee;
    private Instant createdAt;
    private Instant updatedAt;
    public Task() {}
    public Task(String title, String description, Status status, Priority priority,
                LocalDate dueDate, String assignee) {
        this.title = title; this.description = description; this.status = status;
        this.priority = priority; this.dueDate = dueDate; this.assignee = assignee;
    }
    @PrePersist public void prePersist() {
        this.createdAt = Instant.now(); this.updatedAt = this.createdAt;
        if (this.status == null) this.status = Status.TODO;
        if (this.priority == null) this.priority = Priority.MEDIUM;
    }
    @PreUpdate public void preUpdate() { this.updatedAt = Instant.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public Status getStatus() { return status; } public void setStatus(Status status) { this.status = status; }
    public Priority getPriority() { return priority; } public void setPriority(Priority priority) { this.priority = priority; }
    public LocalDate getDueDate() { return dueDate; } public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getAssignee() { return assignee; } public void setAssignee(String assignee) { this.assignee = assignee; }
    public Instant getCreatedAt() { return createdAt; } public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; } public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public enum Status { TODO, IN_PROGRESS, DONE, BLOCKED }
    public enum Priority { LOW, MEDIUM, HIGH }
}
