package com.micro.demo.mapper;

import com.micro.demo.controller.dto.ResultadoAprendizajeDto;
import com.micro.demo.controller.dto.UnidadDto;
import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.entities.Unidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ResultadoAprendizajeMapper {

    @Mapping(target = "unidades", ignore = true)
    ResultadoAprendizaje toEntity(ResultadoAprendizajeDto dto);
    ResultadoAprendizajeDto toDto(ResultadoAprendizaje entity);
}

