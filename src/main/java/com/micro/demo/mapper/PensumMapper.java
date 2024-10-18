package com.micro.demo.mapper;

import com.micro.demo.controller.dto.PensumDto;
import com.micro.demo.entities.AsignaturaPensum;
import com.micro.demo.entities.Pensum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PensumMapper {

    @Mapping(source = "programaAcademicoId", target = "programaAcademico.id")
    @Mapping(target = "asignaturaPensum", ignore = true)
    Pensum toEntity(PensumDto pensumDto);

    @Mapping(source = "programaAcademico.id", target = "programaAcademicoId")
    @Mapping(target = "asignaturaPensum", ignore = true)
    PensumDto toDto(Pensum pensum);
}

