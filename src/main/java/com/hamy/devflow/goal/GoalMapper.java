package com.hamy.devflow.goal;

import com.hamy.devflow.goal.dto.GoalResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    GoalResponse toResponse(Goal goal);
}
