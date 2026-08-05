package com.hamy.devflow.dashboard.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardResponse {
     long totalGoals;
     long todoGoals;
     long inProgressGoals;
     long doneGoals;
     long totalTasks;
     long todoTasks;
     long inProgressTasks;
     long doneTasks;
     long highPriorityTasks;
}
