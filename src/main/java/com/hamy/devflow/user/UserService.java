package com.hamy.devflow.user;

import com.hamy.devflow.common.exceptions.BadRequestException;
import com.hamy.devflow.user.dto.UpdateProfileRequest;
import com.hamy.devflow.user.dto.UserResponse;
import com.hamy.devflow.common.utils.GetCurrentUserUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    public UserResponse updateMyProfile(UpdateProfileRequest request){
        User currentUser = GetCurrentUserUtil.getCurrentUser();
        String currentName = currentUser.getFullName();
        if(request.getFullName().equals(currentName)){
            throw new BadRequestException("Tên mới không được trùng với tên cũ");
        }
        currentUser.setFullName(request.getFullName());
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
        return userMapper.toResponse(currentUser);
    }
    public UserResponse getMyProfile(){
        User currentUser = GetCurrentUserUtil.getCurrentUser();
        return userMapper.toResponse(currentUser);
    }

}
