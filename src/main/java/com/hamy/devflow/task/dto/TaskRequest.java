package com.hamy.devflow.task.dto;

import com.hamy.devflow.task.TaskPriority;
import com.hamy.devflow.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Title không được rỗng")
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime deadline;
    @NotNull(message = "Task phải thuộc 1 goal")
    private Long goalId;
}
