package com.micro.demo.mapper;

import com.micro.demo.controller.dto.HistoryMovementDto;
import com.micro.demo.entities.HistoryMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoryMovementMapper {

    @Mapping(source = "asignaturaAfectadaId", target = "asignaturaAfectada.id")
    @Mapping(source = "pensumId", target = "pensum.id")
    @Mapping(source = "programaAcademicoId", target = "programaAcademico.id")
    HistoryMovement toEntity(HistoryMovementDto historyMovementDto);

    @Mapping(source = "asignaturaAfectada.id", target = "asignaturaAfectadaId")
    @Mapping(source = "pensum.id", target = "pensumId")
    @Mapping(source = "programaAcademico.id", target = "programaAcademicoId")
    HistoryMovementDto toDto(HistoryMovement historyMovement);
}
