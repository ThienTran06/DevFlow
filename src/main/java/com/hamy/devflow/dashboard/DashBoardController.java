package com.hamy.devflow.dashboard;

import com.hamy.devflow.dashboard.dto.DashBoardResponse;
import com.hamy.devflow.goal.GoalService;
import com.hamy.devflow.goal.GoalStatus;
import com.hamy.devflow.task.TaskPriority;
import com.hamy.devflow.task.TaskService;
import com.hamy.devflow.task.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashBoardController {
    private final DashBoardService dashBoardService;
    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }
    @GetMapping
    public DashBoardResponse getDashBoard() {
        return dashBoardService.getDashBoardResponse();
    }

}
