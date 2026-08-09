package com.hamy.devflow.task.dto;

import com.hamy.devflow.goal.Goal;
import com.hamy.devflow.task.TaskPriority;
import com.hamy.devflow.task.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long goalId;
    private String goalTitle;
}
