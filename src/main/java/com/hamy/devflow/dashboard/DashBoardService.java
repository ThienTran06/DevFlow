package com.hamy.devflow.dashboard;

import com.hamy.devflow.dashboard.dto.DashBoardResponse;
import com.hamy.devflow.goal.GoalService;
import com.hamy.devflow.goal.GoalStatus;
import com.hamy.devflow.task.TaskPriority;
import com.hamy.devflow.task.TaskService;
import com.hamy.devflow.task.TaskStatus;
import org.springframework.stereotype.Service;

@Service
public class DashBoardService {
    private final GoalService goalService;
    private final TaskService taskService;
    public DashBoardService(GoalService goalService, TaskService taskService) {
        this.goalService = goalService;
        this.taskService = taskService;
    }
   private long getNumberOfGoals(GoalStatus goalStatus){
        return goalService.getNumberOfGoals(goalStatus);
   }
   private long getNumberOfTasks(TaskStatus taskStatus, TaskPriority taskPriority){
        return taskService.getNumberOfTasks(taskStatus, taskPriority);
   }
   public DashBoardResponse getDashBoardResponse(){
        DashBoardResponse dashBoardResponse = new DashBoardResponse();
        dashBoardResponse.setTotalGoals(getNumberOfGoals(null));
        dashBoardResponse.setDoneTasks(getNumberOfTasks(TaskStatus.DONE, null));
        dashBoardResponse.setTotalTasks(getNumberOfTasks(null,null));
        dashBoardResponse.setDoneGoals(getNumberOfGoals(GoalStatus.DONE));
        dashBoardResponse.setInProgressGoals(getNumberOfGoals(GoalStatus.IN_PROGRESS));
        dashBoardResponse.setInProgressTasks(getNumberOfTasks(TaskStatus.IN_PROGRESS, null));
        dashBoardResponse.setHighPriorityTasks(getNumberOfTasks(null, TaskPriority.HIGH));
        dashBoardResponse.setTodoTasks(getNumberOfTasks(TaskStatus.TODO,null));
        dashBoardResponse.setTodoGoals(getNumberOfGoals(GoalStatus.TODO));
        return dashBoardResponse;
   }
}
