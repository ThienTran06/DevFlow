package com.hamy.devflow.goal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    public Long countByStatus(GoalStatus status);
}
