package com.hamy.devflow.goal.dto;

import com.hamy.devflow.goal.GoalStatus;
import jakarta.validation.constraints.NotBlank;
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
    private String title;
    private String description;
    private GoalStatus status;
    private LocalDateTime deadline;
}
