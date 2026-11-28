package com.hamy.devflow.common.utils;

import com.hamy.devflow.user.CustomUserDetails;
import com.hamy.devflow.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class GetCurrentUserUtil {
    private  GetCurrentUserUtil() {}
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUser();
    }
}

