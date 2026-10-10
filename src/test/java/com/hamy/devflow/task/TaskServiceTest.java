package com.hamy.devflow.task;

import com.hamy.devflow.common.exception.ResourceNotFoundException;
import com.hamy.devflow.goal.Goal;
import com.hamy.devflow.goal.GoalRepository;
import com.hamy.devflow.task.dto.TaskRequest;
import com.hamy.devflow.task.dto.TaskResponse;
import com.hamy.devflow.user.CustomUserDetails;
import com.hamy.devflow.user.User;
import com.hamy.devflow.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private GoalRepository goalRepository;
    @InjectMocks
    private TaskService taskService;
    @Captor
    private ArgumentCaptor<Task> captor;

    @Test
    void createTask_whenStatusAndPriorityAreNull_shouldUseDefaultValues(){
        Goal goal = new  Goal();
        goal.setId(1L);
        goal.setTitle("Goal 1");
        User  user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);
        goal.setOwner(user);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails,null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TaskRequest taskRequest = new  TaskRequest();
        taskRequest.setStatus(null);
        taskRequest.setPriority(null);
        taskRequest.setGoalId(1L);
        taskRequest.setTitle("Learn Mockito");
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.createTask(taskRequest);
        verify(taskRepository).save(captor.capture());
        Task capturedTask = captor.getValue();
        assertEquals(TaskStatus.TODO, capturedTask.getStatus());
        assertEquals(TaskPriority.MEDIUM, capturedTask.getPriority());
        assertEquals(user.getId(), capturedTask.getOwner().getId());
        assertEquals(goal.getId(), capturedTask.getGoal().getId());
    }
    @Test
    void createTask_whenGoalDoesNotExist_throwException(){
        TaskRequest taskRequest = new  TaskRequest();
        taskRequest.setGoalId(1L);
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(taskRequest));
        verify(taskRepository,never()).save(any(Task.class));
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    @Test
    void getTaskById_whenTaskBelongsToAnotherUser_shouldThrowException(){
        Task task = new  Task();
        task.setId(1L);
        User owner = new User();
        owner.setId(1L);
        task.setOwner(owner);
        User currentUser = new User();
        currentUser.setRole(UserRole.USER);
        currentUser.setId(2L);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null,customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(AccessDeniedException.class, () -> taskService.getTaskById(1L));
    }
    @Test
    void getTaskById_whenTaskBelongsToCurrentUser_shouldReturnTask(){
        Task task = new  Task();
        task.setId(1L);
        User owner = new User();
        owner.setId(1L);
        task.setOwner(owner);
        Goal goal = new  Goal();
        task.setGoal(goal);
        User currentUser = new User();
        currentUser.setRole(UserRole.USER);
        currentUser.setId(1L);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null,customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        TaskResponse taskResponse = taskService.getTaskById(1L);
        assertNotNull(taskResponse);
        assertEquals(1L, taskResponse.getId());
    }
    @Test
    void createTask_WhenGoalBelongsToAnotherUser_shouldThrowException(){
        User currentUser = new User();
        currentUser.setRole(UserRole.USER);
        currentUser.setId(1L);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null,customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal goal = new  Goal();
        goal.setId(1L);
        User owner = new User();
        owner.setId(2L);
        goal.setOwner(owner);
        TaskRequest taskRequest = new  TaskRequest();
        taskRequest.setStatus(null);
        taskRequest.setPriority(null);
        taskRequest.setGoalId(1L);
        taskRequest.setTitle("Learn Mockito");
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        assertThrows(AccessDeniedException.class, () -> taskService.createTask(taskRequest));
        verify(taskRepository,never()).save(any(Task.class));
    }
    @Test
    void getTaskById_whenTaskDoesNotExist_shouldThrowResourceNotFoundException(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L));
    }
    @Test
    void updateTaskById_whenOwnerUpdates_shouldSaveChanges(){
        Task task = new  Task();
        task.setId(1L);
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.LOW);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        User currentUser = new User();
        currentUser.setRole(UserRole.USER);
        currentUser.setId(1L);
        task.setOwner(currentUser);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null,customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal goal = new  Goal();
        goal.setId(1L);
        goal.setOwner(currentUser);
        TaskRequest taskRequest = new  TaskRequest();
        taskRequest.setStatus(TaskStatus.DONE);
        taskRequest.setGoalId(1L);
        taskRequest.setTitle("Learn Mockito");
        taskRequest.setPriority(TaskPriority.HIGH);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        TaskResponse taskResponse = taskService.updateTaskById(1L,taskRequest);
        verify(taskRepository).save(captor.capture());
        Task savedTask = captor.getValue();
        assertEquals(1L, savedTask.getId());
        assertEquals(TaskStatus.DONE, savedTask.getStatus());
        assertEquals(TaskPriority.HIGH, savedTask.getPriority());
    }
    @Test
    void updateTaskById_whenNewGoalBelongsToAnotherUser_shouldNotSave()
    {
     Task task = new  Task();
     task.setId(1L);
     task.setStatus(TaskStatus.IN_PROGRESS);
     task.setPriority(TaskPriority.LOW);
     when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
     User currentUser = new User();
     currentUser.setRole(UserRole.USER);
     currentUser.setId(1L);
     task.setOwner(currentUser);
     CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
     Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null,customUserDetails.getAuthorities());
     SecurityContextHolder.getContext().setAuthentication(authentication);
     User newUser = new User();
     newUser.setId(2L);
     Goal  newGoal = new  Goal();
     newGoal.setId(2L);
     newGoal.setOwner(newUser);
     TaskRequest taskRequest = new  TaskRequest();
     taskRequest.setStatus(TaskStatus.DONE);
     taskRequest.setGoalId(2L);
     taskRequest.setPriority(TaskPriority.HIGH);
     when(goalRepository.findById(2L)).thenReturn(Optional.of(newGoal));
     assertThrows(AccessDeniedException.class,() -> taskService.updateTaskById(1L,taskRequest));
     verify(taskRepository,never()).save(any(Task.class));
     assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
     assertEquals(TaskPriority.LOW, task.getPriority());
    }
    @Test
    void findByGoalId_whenGoalBelongsToAnotherUser_shouldDenyAccess(){
        User currentUser = new User();
        currentUser.setRole(UserRole.USER);
        currentUser.setId(1L);
        CustomUserDetails principal = new CustomUserDetails(currentUser);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal goal = new  Goal();
        goal.setId(1L);
        User newUser = new User();
        newUser.setId(2L);
        goal.setOwner(newUser);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        assertThrows(AccessDeniedException.class,()->taskService.findByGoalId(1L,0,10));
        verify(taskRepository, never())
                .findByGoalId(anyLong(), any(Pageable.class));
    }
    @Test
    void deleteTaskById_whenTaskBelongsToAnotherUser_shouldNotDelete(){
        User currentUser = new User();
        currentUser.setRole(UserRole.USER);
        currentUser.setId(1L);
        User newUser = new User();
        newUser.setId(2L);
        CustomUserDetails principal = new CustomUserDetails(currentUser);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Task task = new  Task();
        task.setId(1L);
        task.setOwner(newUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(AccessDeniedException.class,()->taskService.deleteTaskById(1L));
        verify(taskRepository, never()).delete(any(Task.class));

    }
}
