package com.hamy.devflow.goal;

import com.hamy.devflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    public long countByStatus(GoalStatus status);
    public long countByOwnerAndStatus(User user, GoalStatus status);
    public long countByOwner(User user);
    public Page<Goal> findByOwner(User owner, Pageable pageable);
    public Page<Goal> findByOwnerAndStatus(User owner, GoalStatus status, Pageable pageable);
    public Page<Goal> findByStatus(GoalStatus status, Pageable pageable);
}
