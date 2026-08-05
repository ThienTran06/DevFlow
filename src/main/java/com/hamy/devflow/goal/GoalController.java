package com.hamy.devflow.goal;

import com.hamy.devflow.goal.dto.GoalRequest;
import com.hamy.devflow.goal.dto.GoalResponse;
import com.hamy.devflow.task.TaskService;
import com.hamy.devflow.task.dto.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService goalService;
    private final TaskService taskService;
    public GoalController(GoalService goalService, TaskService taskService) {
        this.goalService = goalService;
        this.taskService = taskService;
    }
    @GetMapping
    public Page<GoalResponse> getGoals(int page, int size) {
        return goalService.getAllGoals(page, size);
    }
    @GetMapping("/{id}")
    public GoalResponse getGoalById(@PathVariable Long id){
        return goalService.getGoalById(id);
    }
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest goal) {
        return new ResponseEntity<>(goalService.createGoal(goal), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public GoalResponse updateGoal(@Valid @RequestBody GoalRequest goal, @PathVariable Long id) {
        return goalService.updateGoalById(id, goal);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id)
    {
        goalService.deleteGoalById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{goalId}/tasks")
    public Page<TaskResponse> getTasksByGoalId(@PathVariable Long goalId,@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "10")int size) {
        return taskService.findByGoalId(goalId,page,size);
    }
}
