package com.hamy.devflow.dashboard;

import com.hamy.devflow.dashboard.dto.DashBoardResponse;
import com.hamy.devflow.goal.GoalService;
import com.hamy.devflow.goal.GoalStatus;
import com.hamy.devflow.task.TaskPriority;
import com.hamy.devflow.task.TaskService;
import com.hamy.devflow.task.TaskStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return taskService.getNumberOfMyTasks(taskStatus, taskPriority);
   }
   public DashBoardResponse getDashBoardResponse(){
        DashBoardResponse dashBoardResponse = new DashBoardResponse();
        dashBoardResponse.setTotalGoals(getMyNumberOfGoals(null));
        dashBoardResponse.setDoneTasks(getMyNumberOfTasks(TaskStatus.DONE, null));
        dashBoardResponse.setTotalTasks(getMyNumberOfTasks(null,null));
        dashBoardResponse.setDoneGoals(getMyNumberOfGoals(GoalStatus.DONE));
        dashBoardResponse.setInProgressGoals(getMyNumberOfGoals(GoalStatus.IN_PROGRESS));
        dashBoardResponse.setInProgressTasks(getMyNumberOfTasks(TaskStatus.IN_PROGRESS, null));
        dashBoardResponse.setHighPriorityTasks(getMyNumberOfTasks(null, TaskPriority.HIGH));
        dashBoardResponse.setTodoTasks(getMyNumberOfTasks(TaskStatus.TODO,null));
        dashBoardResponse.setTodoGoals(getMyNumberOfGoals(GoalStatus.TODO));
        return dashBoardResponse;
   }
   @PreAuthorize("hasRole('ADMIN')")
    public DashBoardResponse getAdminDashBoardResponse(){
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
   private long getMyNumberOfGoals(GoalStatus goalStatus){
        return goalService.getMyNumberOfGoals(goalStatus);
   }
   private long getMyNumberOfTasks(TaskStatus taskStatus, TaskPriority taskPriority){
        return taskService.getNumberOfMyTasks(taskStatus, taskPriority);
   }
}
