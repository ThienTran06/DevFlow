package com.hamy.devflow.dashboard;

import com.hamy.devflow.dashboard.dto.DashBoardResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
