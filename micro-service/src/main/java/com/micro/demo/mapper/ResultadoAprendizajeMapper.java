package com.micro.demo.mapper;

import com.micro.demo.controller.dto.ResultadoAprendizajeDto;
import com.micro.demo.entities.ResultadoAprendizaje;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResultadoAprendizajeMapper {

    @Mapping(target = "unidades", ignore = true)
    ResultadoAprendizaje toEntity(ResultadoAprendizajeDto dto);
    ResultadoAprendizajeDto toDto(ResultadoAprendizaje entity);
}

