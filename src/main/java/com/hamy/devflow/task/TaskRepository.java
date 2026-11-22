package com.hamy.devflow.task;

import com.hamy.devflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    @Query("""
    SELECT t FROM Task t
    WHERE t.owner = :owner
      AND (:status IS NULL OR t.status = :status)
      AND (:priority IS NULL OR t.priority = :priority)
      AND (
          :overdueOnly = false
          OR (
              t.deadline IS NOT NULL
              AND t.deadline < :now
              AND t.status <> :doneStatus
          )
      )
    """)
    Page<Task> findTasks(
            @Param("owner") User owner,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("overdueOnly") boolean overdueOnly,
            @Param("now") LocalDateTime now,
            @Param("doneStatus") TaskStatus doneStatus,
            Pageable pageable
    );
    Page<Task> findByGoalId(Long goalId, Pageable pageable);
    Page<Task> findByOwner(User owner, Pageable pageable);
    Page<Task> findByStatusAndPriority(TaskStatus status,TaskPriority taskPriority, Pageable pageable);
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    Page<Task> findByPriority(TaskPriority taskPriority, Pageable pageable);
    long countByStatus(TaskStatus status);
    long countByPriority(TaskPriority priority);
    long countByStatusAndPriority(TaskStatus status, TaskPriority priority);
    long countByOwnerAndStatusAndPriority(User owner, TaskStatus status, TaskPriority priority);
    long countByOwnerAndPriority(User owner, TaskPriority priority);
    long countByOwnerAndStatus(User owner, TaskStatus status);
    long countByOwner(User owner);
}
