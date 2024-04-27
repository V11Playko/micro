package com.micro.demo.service;

import com.micro.demo.entities.PreRequisito;

import java.util.List;

public interface IPreRequisitoService {

    List<PreRequisito> getAllPreRequisito(int pagina, int elementosXpagina);
    void savePreRequisito(PreRequisito preRequisito);
    void deletePrerequisito(Long id);
}
