package com.hamy.devflow.goal;

import com.hamy.devflow.MySqlTestConfig;
import com.hamy.devflow.auth.JwtService;
import com.hamy.devflow.user.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureMockMvc
@Import({MySqlTestConfig.class})
@Transactional
public class GoalIntergtationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    GoalRepository goalRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JwtService jwtService;

    @Test
    void getGoal_whenCurrentUserIsOwner_shouldReturn200() throws Exception {
        User user = new User();
        user.setFullName("Owner");
        user.setRole(UserRole.USER);
        user.setEmail("example@gmail.com");
        user.setPassword("dangtranthien2006");
        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        Goal goal = new Goal();
        goal.setOwner(savedUser);
        goal.setTitle("Learn Java");
        goal.setStatus(GoalStatus.IN_PROGRESS);
        Goal savedGoal = goalRepository.save(goal);
        mockMvc.perform(
                        get("/api/goals/{id}", savedGoal.getId())
                                .with(user(new CustomUserDetails(savedUser)))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedGoal.getId().intValue()))
                .andExpect(jsonPath("$.title").value("Learn Java"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void getGoal_whenCurrentUserIsNotOwner_shouldReturn403() throws Exception {
        User user = new User();
        user.setFullName("CurrentUser");
        user.setRole(UserRole.USER);
        user.setEmail("example@gmail.com");
        user.setPassword("dangtranthien2006");
        user.setStatus(UserStatus.ACTIVE);
        User currentUser = userRepository.save(user);
        User user1 = new User();
        user1.setFullName("Owner");
        user1.setRole(UserRole.USER);
        user1.setEmail("example2@gmail.com");
        user1.setPassword("dangtranthien2005");
        user1.setStatus(UserStatus.ACTIVE);
        User owner = userRepository.save(user1);
        Goal goal = new Goal();
        goal.setOwner(owner);
        goal.setTitle("Learn Java");
        goal.setStatus(GoalStatus.IN_PROGRESS);
        Goal savedGoal = goalRepository.save(goal);
        mockMvc.perform(
                get("/api/goals/{id}", savedGoal.getId())
                        .with(user(new CustomUserDetails(currentUser)))
        ).andExpect(status().isForbidden());
    }

    @Test
    void createGoal_whenTitleIsBlank_shouldReturn400() throws Exception {
        User user1 = new User();
        user1.setFullName("Owner");
        user1.setRole(UserRole.USER);
        user1.setEmail("example2@gmail.com");
        user1.setPassword("dangtranthien2005");
        user1.setStatus(UserStatus.ACTIVE);
        User owner = userRepository.save(user1);
        long goalCount = goalRepository.count();
        mockMvc.perform(
                post("/api/goals")
                        .with(user(new CustomUserDetails(owner)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "title": "   ",
                          "description": "Test validation"
                        }
                        """)
                ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Title is required"));
        assertEquals(goalCount, goalRepository.count());
    }
    @Test
    void updateGoal_whenCurrentUserIsOwner_shouldReturn200AndPersistChanges() throws Exception{
        User owner = new User();
        owner.setFullName("Owner");
        owner.setRole(UserRole.USER);
        owner.setEmail("example2@gmail.com");
        owner.setPassword("dangtranthien2005");
        owner.setStatus(UserStatus.ACTIVE);
        User Owner = userRepository.save(owner);
        Goal goal = new Goal();
        goal.setOwner(owner);
        goal.setTitle("Learn Java");
        goal.setStatus(GoalStatus.IN_PROGRESS);
        Goal savedGoal = goalRepository.save(goal);
        mockMvc.perform(
                put("/api/goals/{id}", savedGoal.getId())
                        .with(user(new CustomUserDetails(Owner)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                      "title": "Learn Java 2",
                      "status": "DONE"
                      }
                """)
        ).andExpect(status().isOk());
        entityManager.flush();
        entityManager.clear();
        Goal updatedGoal = goalRepository.findById(savedGoal.getId()).get();
        assertEquals("Learn Java 2", updatedGoal.getTitle());
        assertEquals(GoalStatus.DONE, updatedGoal.getStatus());
    }
    @Test
    void updateGoal_whenCurrentUserIsNotOwner_shouldReturn403() throws Exception {
        User user  = new User();
        user.setFullName("User");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmail("emailTest@gmail.com");
        user.setPassword("dangtranthien2006");
        User user1 = userRepository.save(user);
        User owner = new User();
        owner.setFullName("Owner");
        owner.setRole(UserRole.USER);
        owner.setEmail("emailTest2@gmail.com");
        owner.setPassword("dangtranthien2005");
        owner.setStatus(UserStatus.ACTIVE);
        User Owner = userRepository.save(owner);
        Goal goal = new Goal();
        goal.setOwner(Owner);
        goal.setTitle("Learn Java");
        goal.setStatus(GoalStatus.IN_PROGRESS);
        Goal savedGoal = goalRepository.save(goal);
        mockMvc.perform(
                put("/api/goals/{id}", savedGoal.getId())
                .with(user(new CustomUserDetails(user1)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                      "title": "Learn Java 2",
                      "status": "DONE"
                      }
                """)
        ).andExpect(status().isForbidden());
        entityManager.flush();
        entityManager.clear();
        Goal goal1 = goalRepository.findById(savedGoal.getId()).get();
        assertEquals("Learn Java", goal1.getTitle());
        assertEquals(GoalStatus.IN_PROGRESS, goal1.getStatus());
    }
    @Test
    void getGoal_whenAccessTokenIsValid_shouldReturn200() throws Exception {
        User owner = new User();
        owner.setFullName("Owner");
        owner.setRole(UserRole.USER);
        owner.setEmail("testEmail@gmail.com");
        owner.setPassword("dangtranthien2005");
        owner.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(owner);
        Goal goal = new Goal();
        goal.setOwner(savedUser);
        goal.setTitle("Learn Java");
        goal.setStatus(GoalStatus.IN_PROGRESS);
        Goal savedGoal = goalRepository.save(goal);
        String accessToken = jwtService.generateAccessToken(savedUser);
        mockMvc.perform(
                get("/api/goals/{id}",savedGoal.getId())
                        .header("Authorization","Bearer "+ accessToken)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedGoal.getId()));
    }
    @Test
    void getGoal_whenAccessTokenIsMissing_shouldReturn401() throws Exception {

        User owner = new User();
        owner.setFullName("Owner");
        owner.setRole(UserRole.USER);
        owner.setEmail("testEmail@gmail.com");
        owner.setPassword("dangtranthien2005");
        owner.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(owner);
        Goal goal = new Goal();
        goal.setOwner(savedUser);
        goal.setTitle("Learn Java");
        goal.setStatus(GoalStatus.IN_PROGRESS);
        Goal savedGoal = goalRepository.save(goal);
        mockMvc.perform(
                        get("/api/goals/{id}",savedGoal.getId())
                ).andExpect(status().isUnauthorized());
    }
    @Test
    void getGoal_whenAccessTokenIsExpired_shouldReturn401()  throws Exception {
        String accessTokenSecret = "my-super-secret-key-my-super-secret-key-123456";
        SecretKey signingKey = Keys.hmacShaKeyFor(
                accessTokenSecret.getBytes(StandardCharsets.UTF_8)
        );
        User owner = new User();
        owner.setFullName("Owner");
        owner.setRole(UserRole.USER);
        owner.setEmail("testEmail@gmail.com");
        owner.setPassword("dangtranthien2005");
        owner.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(owner);
        Goal goal = new Goal();
        goal.setOwner(savedUser);
        goal.setTitle("Learn Java");
        goal.setStatus(GoalStatus.IN_PROGRESS);
        Goal savedGoal = goalRepository.save(goal);
        long now = System.currentTimeMillis();
        String accessToken = Jwts.builder()
                .subject(owner.getId().toString())
                .issuedAt(new Date(now-120000))
                .expiration(new Date(now-60000))
                .signWith(signingKey)
                .compact();
        assertThrows(ExpiredJwtException.class,()->jwtService.parseAccessClaims(accessToken));
        mockMvc.perform(
                get("/api/goals/{id}",savedGoal.getId())
                .header("Authorization","Bearer "+ accessToken)
        ).andExpect(status().isUnauthorized());
    }
}
