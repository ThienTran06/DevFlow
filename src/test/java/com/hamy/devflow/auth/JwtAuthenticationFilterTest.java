package com.hamy.devflow.auth;
import com.hamy.devflow.user.CustomUserDetails;
import com.hamy.devflow.user.CustomUserDetailsService;
import com.hamy.devflow.user.User;
import com.hamy.devflow.user.UserStatus;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
   @Mock
    Claims  claims;
   @Mock
    CustomUserDetailsService customUserDetailsService;
   @Mock
    JwtService jwtService;
   @Mock
   FilterChain filterChain;
   @InjectMocks
   JwtAuthenticationFilter jwtAuthenticationFilter;
   @Test
    void authenticateUser_whenUserIsNotActive_shouldReturnUnauthorized() throws Exception {
       MockHttpServletRequest  mockHttpServletRequest = new MockHttpServletRequest();
       MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
       mockHttpServletRequest.addHeader("Authorization","Bearer valid-token");
       when(jwtService.parseAccessClaims("valid-token")).thenReturn(claims);
       when(claims.getSubject()).thenReturn("1");
       User user = new User();
       user.setId(1L);
       user.setStatus(UserStatus.LOCKED);
       CustomUserDetails customUserDetails = new CustomUserDetails(user);
       when (customUserDetailsService.loadUserByUsername("1")).thenReturn(customUserDetails);
      jwtAuthenticationFilter.doFilter(mockHttpServletRequest,mockHttpServletResponse,filterChain);
      assertEquals(HttpServletResponse.SC_UNAUTHORIZED,mockHttpServletResponse.getStatus());
   }





}
