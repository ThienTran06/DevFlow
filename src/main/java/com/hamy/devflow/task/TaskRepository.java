package com.hamy.devflow.task;

import com.hamy.devflow.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    Page<Task> findByGoalId(Long goalId, Pageable pageable);
    Page<Task> findByOwner(User owner, Pageable pageable);

    Page<Task> findByOwnerAndStatus(User owner, TaskStatus status, Pageable pageable);

    Page<Task> findByOwnerAndPriority(User owner, TaskPriority priority, Pageable pageable);

    Page<Task> findByOwnerAndStatusAndPriority(
            User owner,
            TaskStatus status,
            TaskPriority priority,
            Pageable pageable
    );
    long countByStatus(TaskStatus status);
    long countByPriority(TaskPriority priority);
    long countByStatusAndPriority(TaskStatus status, TaskPriority priority);
}
