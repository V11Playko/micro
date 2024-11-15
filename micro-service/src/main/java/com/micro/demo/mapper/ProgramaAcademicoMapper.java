package com.micro.demo.mapper;

import com.micro.demo.controller.dto.ProgramaAcademicoDto;
import com.micro.demo.entities.ProgramaAcademico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgramaAcademicoMapper {

    @Mapping(target = "director.correo", source = "correoDirector")
    @Mapping(target = "pensums", ignore = true)
    ProgramaAcademico toEntity(ProgramaAcademicoDto programaAcademicoDto);

    @Mapping(target = "correoDirector", source = "director.correo")
    ProgramaAcademicoDto toDto(ProgramaAcademico programaAcademico);
}