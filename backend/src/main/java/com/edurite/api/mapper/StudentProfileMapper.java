package com.edurite.api.mapper;

import com.edurite.api.dto.ProfileDto;
import com.edurite.domain.model.StudentProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudentProfileMapper {
    ProfileDto toDto(StudentProfile model);
    StudentProfile toModel(ProfileDto dto);
}
