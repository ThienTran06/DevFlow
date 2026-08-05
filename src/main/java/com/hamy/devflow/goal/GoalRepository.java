package com.hamy.devflow.goal;

import com.hamy.devflow.task.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    public Long countByStatus(GoalStatus status);
}
