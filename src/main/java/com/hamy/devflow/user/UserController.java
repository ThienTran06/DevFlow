package com.hamy.devflow.user;

import com.hamy.devflow.user.dto.UpdateProfileRequest;
import com.hamy.devflow.user.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponse>getMyProfile(){
        return new ResponseEntity<>(userService.getMyProfile(), HttpStatus.OK);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
        return new ResponseEntity<>(userService.updateMyProfile(updateProfileRequest), HttpStatus.OK );
    }

}
