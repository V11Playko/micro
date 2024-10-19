package com.micro.demo.service;

import com.micro.demo.controller.dto.PreRequisitoDto;
import com.micro.demo.entities.PreRequisito;

import java.util.Map;

public interface IPreRequisitoService {

    Map<String, Object> getAllPreRequisito(Integer pagina, Integer elementosXpagina);
    PreRequisito getPreRequisito(Long id);
    void savePreRequisito(PreRequisitoDto preRequisitoDto);
    void deletePrerequisito(Long id);
}
