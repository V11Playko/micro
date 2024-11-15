package com.micro.demo.mapper;

import com.micro.demo.controller.dto.AreaFormacionDto;
import com.micro.demo.entities.AreaFormacion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AreaFormacionMapper {

    AreaFormacion toEntity(AreaFormacionDto dto);

    AreaFormacionDto toDto(AreaFormacion areaFormacion);
}
