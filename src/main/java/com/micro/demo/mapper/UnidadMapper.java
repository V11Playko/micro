package com.micro.demo.mapper;

import com.micro.demo.controller.dto.UnidadDto;
import com.micro.demo.entities.Unidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UnidadMapper {

    UnidadMapper INSTANCE = Mappers.getMapper(UnidadMapper.class);

    @Mapping(source = "temas", target = "temas", ignore = true) // Manejo manual
    @Mapping(source = "resultados", target = "resultadoAprendizaje", ignore = true) // Manejo manual
    @Mapping(source = "asignatura", target = "asignatura.id") // Asignatura ID
    Unidad toEntity(UnidadDto dto);
}