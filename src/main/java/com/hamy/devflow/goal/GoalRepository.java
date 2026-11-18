package com.hamy.devflow.goal;

import com.hamy.devflow.task.Task;
import com.hamy.devflow.task.TaskPriority;
import com.hamy.devflow.task.TaskStatus;
import com.hamy.devflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    public long countByStatus(GoalStatus status);
    public long countByOwnerAndStatus(User user, GoalStatus status);
    public long countByOwner(User user);
    public Page<Goal> findByOwner(User owner, Pageable pageable);
    public Page<Goal> findByOwnerAndStatus(User owner, GoalStatus status, Pageable pageable);
    public Page<Goal> findByStatus(GoalStatus status, Pageable pageable);
    @Query("""
    SELECT t FROM Goal t
    WHERE (:ownerId IS NULL OR t.owner.id = :ownerId)
      AND (:status IS NULL OR t.status = :status)
      AND (
          :overdueOnly = false
          OR (
              t.deadline IS NOT NULL
              AND t.deadline < :now
              AND t.status <> :doneStatus
          )
      )
    """)
    Page<Goal> findGoals(
            @Param("ownerId") Long ownerId,
            @Param("status") GoalStatus status,
            @Param("overdueOnly") boolean overdueOnly,
            @Param("now") LocalDateTime now,
            @Param("doneStatus") GoalStatus doneStatus,
            Pageable pageable
    );
}
