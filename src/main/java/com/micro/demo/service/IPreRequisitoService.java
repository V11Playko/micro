package com.micro.demo.service;

import com.micro.demo.entities.PreRequisito;

import java.util.Map;

public interface IPreRequisitoService {

    Map<String, Object> getAllPreRequisito(Integer pagina, Integer elementosXpagina);
    void savePreRequisito(PreRequisito preRequisito);
    void deletePrerequisito(Long id);
}
