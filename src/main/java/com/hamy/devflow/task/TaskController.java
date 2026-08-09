package com.hamy.devflow.task;

import com.hamy.devflow.task.dto.TaskRequest;
import com.hamy.devflow.task.dto.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping("/{id}")
    public TaskResponse findTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        return new ResponseEntity<>(taskService.createTask(taskRequest), HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{id}")
    public TaskResponse updateTaskById(@PathVariable Long id,@Valid @RequestBody TaskRequest taskRequest) {
        return taskService.updateTaskById(id, taskRequest);
    }
    @GetMapping
    public Page<TaskResponse> getTasks(@RequestParam(required=false)TaskStatus status, @RequestParam(required=false)TaskPriority priority,@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "10") int size,@RequestParam(defaultValue = "createdAt") String sortBy,
                                       @RequestParam(defaultValue = "desc") String direction ) {
        return taskService.getTasks(status,priority,page,size,sortBy,direction);
    }
    @GetMapping("/admin")
    public Page<TaskResponse> getAllTasks(@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "10") int size){
        return taskService.getAllTasks(page,size);
    }
}
