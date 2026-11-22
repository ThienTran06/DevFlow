package com.hamy.devflow.utils;

import com.hamy.devflow.common.exception.BadRequestException;

public class CheckPageAndSizeUtil {
     public void checkPageAndSize(int page,int size){
        if(page<0) throw new BadRequestException("Page must larger than 0");
        if(size<1||size>100) throw new BadRequestException("Size must be between 1 and 100");
    }
}
