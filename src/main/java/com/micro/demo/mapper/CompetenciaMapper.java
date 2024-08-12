package com.micro.demo.mapper;

import com.micro.demo.controller.dto.CompetenciaDto;
import com.micro.demo.entities.Competencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompetenciaMapper {

    CompetenciaMapper INSTANCE = Mappers.getMapper(CompetenciaMapper.class);

    @Mapping(source = "resultados", target = "resultados", ignore = true) // Manejo manual
    Competencia toEntity(CompetenciaDto dto);

}