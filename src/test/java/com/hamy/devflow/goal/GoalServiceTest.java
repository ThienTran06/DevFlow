package com.hamy.devflow.goal;

import com.hamy.devflow.common.exception.ResourceNotFoundException;
import com.hamy.devflow.goal.dto.GoalRequest;
import com.hamy.devflow.goal.dto.GoalResponse;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;
    @Mock
    private GoalRepository goalRepository;
    @Captor
    private ArgumentCaptor<Goal> captor;
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    @Test
    void createGoal_whenStatusIsNull_shouldUseTodoAndSetOwner(){
        GoalRequest newGoalRequest = new GoalRequest();
        newGoalRequest.setStatus(null);
        newGoalRequest.setDescription("description");
        newGoalRequest.setTitle("Hoc test");
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);
       CustomUserDetails customUserDetails = new CustomUserDetails(user);
       Authentication authentication =  new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
       SecurityContextHolder.getContext().setAuthentication(authentication);
       when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArgument(0));
       goalService.createGoal(newGoalRequest);
       verify(goalRepository).save(captor.capture());
       Goal goal = captor.getValue();
       assertEquals(GoalStatus.TODO, goal.getStatus());
       assertEquals(user.getId(), goal.getOwner().getId());
    }
    @Test
    void createGoal_whenStatusIsProvided_shouldKeepStatus(){
        GoalRequest newGoalRequest = new GoalRequest();
        newGoalRequest.setStatus(GoalStatus.IN_PROGRESS);
        newGoalRequest.setDescription("description");
        newGoalRequest.setTitle("Hoc test");
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authentication =  new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArgument(0));
        goalService.createGoal(newGoalRequest);
        verify(goalRepository).save(captor.capture());
        Goal goal = captor.getValue();
        assertEquals(GoalStatus.IN_PROGRESS, goal.getStatus());
        assertEquals(user.getId(), goal.getOwner().getId());
    }
    @Test
    void getGoalById_whenGoalBelongsToCurrentUser_shouldReturnGoal(){
        User  currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication =  new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal  goal = new Goal();
        goal.setId(10L);
        goal.setOwner(currentUser);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setTitle("Hoc Java");
        when(goalRepository.findById(10L)).thenReturn(Optional.of(goal));
        GoalResponse goalResponse = goalService.getGoalById(10L);
        assertEquals(10L, goalResponse.getId());
        assertEquals(GoalStatus.IN_PROGRESS, goalResponse.getStatus());
        assertEquals("Hoc Java", goalResponse.getTitle());
    }
    @Test
    void getGoalById_whenGoalBelongsToAnotherUser_shouldThrowException(){
        User  currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication =   new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal  goal = new Goal();
        goal.setId(10L);
        User  anotherUser = new User();
        anotherUser.setId(2L);
        goal.setOwner(anotherUser);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setTitle("Hoc Java");
        when(goalRepository.findById(10L)).thenReturn(Optional.of(goal));
        assertThrows(AccessDeniedException.class,() -> goalService.getGoalById(10L));
    }
    @Test
    void getGoalById_whenGoalDoesNotExist_shouldThrowException(){
        when(goalRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,() -> goalService.getGoalById(10L));
    }
    @Test
    void updateGoalById_whenOwnerUpdates_shouldSaveChanges(){
        User  currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal  goal = new Goal();
        goal.setId(10L);
        goal.setOwner(currentUser);
        goal.setStatus(GoalStatus.TODO);
        goal.setTitle("Old goal");
        when(goalRepository.findById(10L)).thenReturn(Optional.of(goal));
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setTitle("New goal");
        goalRequest.setStatus(GoalStatus.IN_PROGRESS);
        when(goalRepository.save(goal)).thenReturn(goal);
        goalService.updateGoalById(10L, goalRequest);
        verify(goalRepository).save(captor.capture());
        Goal savedGoal = captor.getValue();
        assertSame(goal, savedGoal);
        assertEquals(10L, savedGoal.getId());
        assertSame(currentUser, savedGoal.getOwner());
        assertEquals(GoalStatus.IN_PROGRESS, savedGoal.getStatus());
        assertEquals("New goal", savedGoal.getTitle());
    }
    @Test
    void updateGoalById_whenStatusIsNull_shouldKeepOldStatus(){
        User  currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal  goal = new Goal();
        goal.setId(10L);
        goal.setOwner(currentUser);
        goal.setStatus(GoalStatus.TODO);
        goal.setTitle("Old goal");
        when(goalRepository.findById(10L)).thenReturn(Optional.of(goal));
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setTitle("New goal");
        goalRequest.setStatus(null);
        when(goalRepository.save(goal)).thenReturn(goal);
        goalService.updateGoalById(10L, goalRequest);
        verify(goalRepository).save(captor.capture());
        Goal savedGoal = captor.getValue();
        assertSame(goal, savedGoal);
        assertEquals(10L, savedGoal.getId());
        assertSame(currentUser, savedGoal.getOwner());
        assertEquals(GoalStatus.TODO, savedGoal.getStatus());
        assertEquals("New goal", savedGoal.getTitle());
    }
    @Test
    void updateGoalById_whenGoalBelongsToAnotherUser_shouldNotSave() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal goal = new Goal();
        goal.setId(10L);
        User anotherUser = new User();
        anotherUser.setId(2L);
        goal.setOwner(anotherUser);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setTitle("Old goal");
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setTitle("New goal");
        goalRequest.setStatus(null);
        goalRequest.setDescription("test");
        when(goalRepository.findById(10L)).thenReturn(Optional.of(goal));
        assertThrows(AccessDeniedException.class, () -> goalService.updateGoalById(10L, goalRequest));
        verify(goalRepository, never()).save(any(Goal.class));
    }
    @Test
    void updateGoalById_whenGoalDoesNotExist_shouldNotSave(){
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setTitle("New goal");
        goalRequest.setStatus(null);
        goalRequest.setDescription("test");
        when(goalRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,() -> goalService.updateGoalById(10L, goalRequest));
        verify(goalRepository, never()).save(any(Goal.class));
    }
    @Test
    void deleteGoalById_whenOwnerDeletes_shouldDeleteGoal(){
        User  currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal goal = new Goal();
        goal.setId(10L);
        goal.setOwner(currentUser);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setTitle("Old goal");
        when(goalRepository.findById(10L)).thenReturn(Optional.of(goal));
        goalService.deleteGoalById(10L);
        verify(goalRepository).delete(goal);
    }
    @Test
    void deleteGoalById_whenGoalBelongsToAnotherUser_shouldNotDelete(){
        User  currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(currentUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Goal goal = new Goal();
        goal.setId(10L);
        User anotherUser = new User();
        anotherUser.setId(2L);
        goal.setOwner(anotherUser);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setTitle("New goal");
        when(goalRepository.findById(10L)).thenReturn(Optional.of(goal));
        assertThrows(AccessDeniedException.class,() -> goalService.deleteGoalById(10L));
        verify(goalRepository, never()).delete(any(Goal.class));
    }
    @Test
    void deleteGoalById_whenGoalDoesNotExist_shouldNotDelete(){
        when(goalRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,() -> goalService.deleteGoalById(10L));
        verify(goalRepository, never()).delete(any(Goal.class));
    }
}
