package com.hamy.devflow.task;

import com.hamy.devflow.common.exception.BadRequestException;
import com.hamy.devflow.common.exception.ResourceNotFoundException;
import com.hamy.devflow.goal.Goal;
import com.hamy.devflow.goal.GoalRepository;
import com.hamy.devflow.goal.GoalStatus;
import com.hamy.devflow.task.dto.TaskRequest;
import com.hamy.devflow.task.dto.TaskResponse;
import com.hamy.devflow.user.CustomUserDetails;
import com.hamy.devflow.user.User;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    public TaskService(TaskRepository taskRepository,GoalRepository goalRepository) {
        this.taskRepository = taskRepository;
        this.goalRepository = goalRepository;
    }
    @PreAuthorize("hasAuthority('CREATE_TASK')")
    public TaskResponse createTask(TaskRequest taskRequest) {
        Goal goal = findGoalById(taskRequest.getGoalId());
        Task newTask = new Task();
        newTask.setTitle(taskRequest.getTitle());
        newTask.setDescription(taskRequest.getDescription());
       if(taskRequest.getStatus() == null) {
           newTask.setStatus(TaskStatus.TODO);
       }
       else{
           newTask.setStatus(taskRequest.getStatus());
       }
       if(taskRequest.getPriority() == null) {
           newTask.setPriority(TaskPriority.MEDIUM);
       }
       else{
           newTask.setPriority(taskRequest.getPriority());
       }
       newTask.setGoal(goal);
       newTask.setCreatedAt(LocalDateTime.now());
       newTask.setUpdatedAt(LocalDateTime.now());
       newTask.setDeadline(taskRequest.getDeadline());
       return toTaskResponse(taskRepository.save(newTask));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TaskResponse> getAllTasks(int Page, int size) {
       Pageable pageable = PageRequest.of(Page, size);
       Page<Task> tasksPage = taskRepository.findAll(pageable);
       return tasksPage.map(this::toTaskResponse);
    }

    private Goal findGoalById(Long goalId) {
        return goalRepository.findById(goalId).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy goal có id = " + goalId));
    }
    private TaskResponse toTaskResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setStatus(task.getStatus());
        taskResponse.setDeadline(task.getDeadline());
        taskResponse.setPriority(task.getPriority());
        taskResponse.setCreatedAt(task.getCreatedAt());
        taskResponse.setUpdatedAt(task.getUpdatedAt());
        taskResponse.setGoalId(task.getGoal().getId());
        taskResponse.setGoalTitle(task.getGoal().getTitle());
        return taskResponse;
    }
    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy task có id ="+taskId));
    }
    @PreAuthorize("hasAuthority('VIEW_TASK')")
    public TaskResponse getTaskById(Long taskId) {
        Task task = findTaskById(taskId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();
        if (!task.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this task");
        }
        return toTaskResponse(task);
    }
    @PreAuthorize("hasAuthority('UPDATE TASK')")
    public TaskResponse updateTaskById(Long taskId, TaskRequest taskRequest) {
        Task task = findTaskById(taskId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();
        if (!task.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this task");
        }
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        if(taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
        }
        if(taskRequest.getPriority() != null) {
            task.setPriority(taskRequest.getPriority());
        }
        task.setDeadline(taskRequest.getDeadline());
        task.setUpdatedAt(LocalDateTime.now());
        Goal goal = findGoalById(taskRequest.getGoalId());
        task.setGoal(goal);
        return toTaskResponse(taskRepository.save(task));
    }
    @PreAuthorize("hasAuthority('DELETE_TASK')")
    public void deleteTaskById(Long taskId) {
        Task task = findTaskById(taskId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();
        if (!task.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this task");
        }
        taskRepository.delete(task);
    }
    public Page<TaskResponse> findByGoalId(Long goalId,int page,int size) {
        findGoalById(goalId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasksPage = taskRepository.findByGoalId(goalId,pageable);
        return tasksPage.map(this::toTaskResponse);
    }

    @PreAuthorize("hasAuthority('VIEW_TASK')")
    public Page<TaskResponse> getTasks(TaskStatus status, TaskPriority priority, int page, int size,String sortBy, String sortOrder) {
        List<String> acceptedSortByFields = List.of("id","priority","status","createdAt","updatedAt","deadline","title");
        if(!acceptedSortByFields.contains(sortBy)) {throw new BadRequestException("Invalid sort field: "+sortBy);}
        Sort sort = sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<Task> taskPage;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        if(status != null&&priority!=null) {
            taskPage = taskRepository.findByOwnerAndStatusAndPriority(currentUser,status,priority,pageable);
        }
        else if(status==null&&priority==null) {
            taskPage = taskRepository.findByOwner(currentUser,pageable);
        }
        else if(status==null) {
            taskPage = taskRepository.findByOwnerAndPriority(currentUser,priority,pageable);
        }
        else {
            taskPage = taskRepository.findByOwnerAndStatus(currentUser,status,pageable);
        }
        return taskPage.map(this::toTaskResponse);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public long getNumberOfTasks( TaskStatus taskStatus, TaskPriority taskPriority) {
        if(taskStatus == null&&taskPriority==null){
            return taskRepository.count();
        }
        else if(taskStatus != null&&taskPriority!=null){
            return taskRepository.countByStatusAndPriority(taskStatus,taskPriority);
        }
       else if(taskStatus==null){
           return taskRepository.countByPriority(taskPriority);
        }
       else{
           return taskRepository.countByStatus(taskStatus);
        }
    }
}
