package com.micro.demo.service;

import com.micro.demo.entities.PreRequisito;

import java.util.Map;

public interface IPreRequisitoService {

    Map<String, Object> getAllPreRequisito(Integer pagina, Integer elementosXpagina);
    PreRequisito getPreRequisito(Long id);
    void savePreRequisito(PreRequisito preRequisito);
    void deletePrerequisito(Long id);
}
