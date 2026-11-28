package com.hamy.devflow.goal.dto;

import com.hamy.devflow.goal.GoalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class GoalRequest {
    @NotBlank(message = "Title is required")
    @Size(max=255)
    private String title;
    @Size(max=255)
    private String description;
    private GoalStatus status;
    private LocalDateTime deadline;
}
