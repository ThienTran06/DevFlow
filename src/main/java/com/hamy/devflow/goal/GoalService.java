package com.hamy.devflow.goal;

import com.hamy.devflow.common.exception.ResourceNotFoundException;
import com.hamy.devflow.goal.dto.GoalRequest;
import com.hamy.devflow.goal.dto.GoalResponse;
import com.hamy.devflow.user.CustomUserDetails;
import com.hamy.devflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }
    @PreAuthorize("hasAuthority('CREATE_GOAL')")
    public GoalResponse createGoal(GoalRequest newGoalRequest) {
        Goal newGoal = new Goal();
        if (newGoalRequest.getStatus() == null) {
            newGoal.setStatus(GoalStatus.TODO);
        } else {
            newGoal.setStatus(newGoalRequest.getStatus());
        }
        newGoal.setCreatedAt(LocalDateTime.now());
        newGoal.setUpdatedAt(LocalDateTime.now());
        newGoal.setDescription(newGoalRequest.getDescription());
        newGoal.setTitle(newGoalRequest.getTitle());
        Goal savedGoal = goalRepository.save(newGoal);
        return toGoalResponse(savedGoal);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public Page<GoalResponse> getAllGoals(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Goal> goalsPage = goalRepository.findAll(pageable);
        return goalsPage.map(this::toGoalResponse);
    }
    @PreAuthorize("hasAuthority('VIEW_GOAL')")
    public GoalResponse getGoalById(Long id) {
         Goal goal =  findGoalById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();
        if (!goal.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this goal");
        }
         return toGoalResponse(goal);
    }
    @PreAuthorize("hasAuthority('UPDATE_GOAL')")
    public GoalResponse updateGoalById(Long id, GoalRequest newGoalRequest){
        Goal goal = findGoalById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();
        if (!goal.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this goal");
        }
        goal.setDescription(newGoalRequest.getDescription());
        goal.setTitle(newGoalRequest.getTitle());
        goal.setUpdatedAt(LocalDateTime.now());
        if(newGoalRequest.getStatus() != null){
            goal.setStatus(newGoalRequest.getStatus());
        }
        Goal savedGoal = goalRepository.save(goal);
        return toGoalResponse(savedGoal);
    }
    @PreAuthorize("hasAuthority('DELETE_GOAL')")
    public void deleteGoalById(Long id){
        Goal goal = findGoalById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();
        User currentUser = customUserDetails.getUser();
        if (!goal.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this goal");
        }
        goalRepository.delete(goal);
    }
    private GoalResponse toGoalResponse(Goal goal){
        GoalResponse goalResponse = new GoalResponse();
        goalResponse.setId(goal.getId());
        goalResponse.setDescription(goal.getDescription());
        goalResponse.setTitle(goal.getTitle());
        goalResponse.setCreatedAt(goal.getCreatedAt());
        goalResponse.setUpdatedAt(goal.getUpdatedAt());
        goalResponse.setStatus(goal.getStatus());
        return goalResponse;
    }
    private Goal findGoalById(Long id){
       return goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy goal có id = " + id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public long getNumberOfGoals(GoalStatus goalStatus) {
        if(goalStatus == null){
            return goalRepository.count();
        }
        return goalRepository.countByStatus(goalStatus);
    }
}
