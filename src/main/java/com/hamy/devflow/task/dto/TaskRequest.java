package com.hamy.devflow.task.dto;

import com.hamy.devflow.task.TaskPriority;
import com.hamy.devflow.task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    @NotBlank(message = "Title không được rỗng")
    @Size(max=255)
    private String title;
    @Size(max=255)
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime deadline;
    @NotNull(message = "Task phải thuộc 1 goal")
    private Long goalId;
}
