package com.example.mcp.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class TaskDto {
  @NotBlank @Size(max = 255) private String title;
  private String description; private String status; private String priority; private String dueDate; private String assignee;
  public TaskDto() {}
  public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
  public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
  public String getPriority() { return priority; } public void setPriority(String priority) { this.priority = priority; }
  public String getDueDate() { return dueDate; } public void setDueDate(String dueDate) { this.dueDate = dueDate; }
  public String getAssignee() { return assignee; } public void setAssignee(String assignee) { this.assignee = assignee; }
}
