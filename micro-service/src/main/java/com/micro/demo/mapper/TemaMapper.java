package com.micro.demo.mapper;

import com.micro.demo.controller.dto.TemaDto;
import com.micro.demo.entities.Tema;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TemaMapper {

    @Mapping(source = "unidadId", target = "unidad.id")
    Tema toEntity(TemaDto temaDto);
}
