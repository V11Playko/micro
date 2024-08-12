package com.micro.demo.mapper;

import com.micro.demo.controller.dto.AsignaturaDto;
import com.micro.demo.entities.Asignatura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface AsignaturaMapper {
    @Mapping(source = "areaFormacionId", target = "areaFormacion.id")
    @Mapping(source = "competenciaId", target = "competencia.id")
    @Mapping(target = "preRequisitos", ignore = true) // Manejo manual en el servicio
    @Mapping(target = "asignaturaPensum", ignore = true)
    @Mapping(target = "asignaturaDocentes", ignore = true)
    Asignatura toEntity(AsignaturaDto dto);
}
