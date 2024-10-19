package com.micro.demo.mapper;

import com.micro.demo.controller.dto.PreRequisitoDto;
import com.micro.demo.entities.PreRequisito;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PreRequisitoMapper {
    PreRequisito toEntity(PreRequisitoDto preRequisitoDto);
    PreRequisitoDto toDto(PreRequisito preRequisito);
}