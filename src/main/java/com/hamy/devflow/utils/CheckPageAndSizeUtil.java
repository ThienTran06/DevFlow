package com.hamy.devflow.utils;

import com.hamy.devflow.common.exception.BadRequestException;


public final class CheckPageAndSizeUtil {
    private CheckPageAndSizeUtil() {}
    public static void checkPageAndSize(int page, int size) {
        if(page<0) throw new BadRequestException("Page must be greater than or equal to 0");
        if(size<1||size>100) throw new BadRequestException("Size must be between 1 and 100");
    }
}
