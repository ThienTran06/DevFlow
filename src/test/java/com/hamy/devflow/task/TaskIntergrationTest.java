package com.hamy.devflow.task;

import com.hamy.devflow.MySqlTestConfig;
import com.hamy.devflow.goal.Goal;
import com.hamy.devflow.goal.GoalRepository;
import com.hamy.devflow.goal.GoalStatus;
import com.hamy.devflow.user.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureMockMvc
@Import({MySqlTestConfig.class})
@Transactional
public class TaskIntergrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    GoalRepository goalRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TaskRepository taskRepository;
    @Test
    void UpdateTask_whenCurrentUserIsOwner_shouldReturn200AndPersistChanges() throws Exception {
        User owner = new User();
        owner.setEmail("testEmail@gmail.com");
        owner.setPassword("testPassword");
        owner.setStatus(UserStatus.ACTIVE);
        owner.setFullName("testFullName");
        owner.setRole(UserRole.USER);
        User Owner = userRepository.save(owner);
        Goal goal = new Goal();
        goal.setOwner(Owner);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setTitle("testGoal");
        Goal savedGoal = goalRepository.save(goal);
        Task task = new Task();
        task.setGoal(savedGoal);
        task.setOwner(Owner);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setTitle("Old title");
        Task savedTask = taskRepository.save(task);
        mockMvc.perform(
                put("/api/tasks/{id}", savedTask.getId())
                        .with(user(new CustomUserDetails(Owner)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                      "title": "updatedTitle",
                      "status": "DONE",
                      "goalId" : %d
                      }
                """.formatted(savedGoal.getId()))
        ).andExpect(status().isOk());
        entityManager.flush();
        entityManager.clear();
        Task updatedTask = taskRepository.findById(savedTask.getId()).get();
        assertEquals("updatedTitle", updatedTask.getTitle());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }
    @Test
    void updateTask_whenTargetGoalBelongsToAnotherUser_shouldReturn403AndKeepData() throws Exception{
        User user1 = new User();
        user1.setEmail("testEmail1");
        user1.setPassword("testPassword1");
        user1.setStatus(UserStatus.ACTIVE);
        user1.setFullName("testFullName1");
        user1.setRole(UserRole.USER);
        User savedUser1 = userRepository.save(user1);
        User user2 = new User();
        user2.setEmail("testEmail2");
        user2.setPassword("testPassword2");
        user2.setStatus(UserStatus.ACTIVE);
        user2.setFullName("testFullName2");
        user2.setRole(UserRole.USER);
        User savedUser2 = userRepository.save(user2);
        Goal goal1 = new Goal();
        goal1.setOwner(user1);
        goal1.setStatus(GoalStatus.IN_PROGRESS);
        goal1.setTitle("testGoal1");
        Goal goal2 = new Goal();
        goal2.setOwner(user2);
        goal2.setStatus(GoalStatus.IN_PROGRESS);
        goal2.setTitle("testGoal2");
        Goal savedGoal1 = goalRepository.save(goal1);
        Goal savedGoal2 = goalRepository.save(goal2);
        Task task1 = new Task();
        task1.setGoal(savedGoal1);
        task1.setOwner(savedUser1);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setPriority(TaskPriority.MEDIUM);
        task1.setTitle("testTask1");
        Task savedTask1 = taskRepository.save(task1);
        mockMvc.perform(
                put("/api/tasks/{id}", savedTask1.getId())
                        .with(user(new CustomUserDetails(user1)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "title": "updatedTitle",
                  "status": "DONE",
                  "goalId" : %d
                  }
                """.formatted(savedGoal2.getId()))
        ).andExpect(status().isForbidden());
        entityManager.flush();
        entityManager.clear();
        Task updatedTask1 = taskRepository.findById(savedTask1.getId()).get();
        assertEquals("testTask1", updatedTask1.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask1.getStatus());
        assertEquals(updatedTask1.getGoal().getId(), savedGoal1.getId());
    }

}
