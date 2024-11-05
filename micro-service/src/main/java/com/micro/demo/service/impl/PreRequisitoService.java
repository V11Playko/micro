package com.micro.demo.service.impl;

import com.micro.demo.controller.dto.PreRequisitoDto;
import com.micro.demo.entities.PreRequisito;
import com.micro.demo.mapper.PreRequisitoMapper;
import com.micro.demo.repository.IPreRequisitoRepository;
import com.micro.demo.service.IPreRequisitoService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.PreRequisitoNotFound;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PreRequisitoService implements IPreRequisitoService {
    private final IPreRequisitoRepository preRequisitoRepository;
    private final PreRequisitoMapper preRequisitoMapper;

    public PreRequisitoService(IPreRequisitoRepository preRequisitoRepository, PreRequisitoMapper preRequisitoMapper) {
        this.preRequisitoRepository = preRequisitoRepository;
        this.preRequisitoMapper = preRequisitoMapper;
    }

    /**
     * Obtiene los pre-requisitos mediante la paginacion
     *
     * @param pagina           numero de pagina
     * @param elementosXpagina elementos que habran en cada pagina
     * @return Lista de los pre-requisitos.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllPreRequisito(Integer pagina, Integer elementosXpagina) {
        Page<PreRequisito> paginaRequisitos;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            paginaRequisitos = new PageImpl<>(preRequisitoRepository.findAll(Sort.by("id").ascending()));
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaRequisitos = preRequisitoRepository.findAll(
                    PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending())
            );
        }

        if (paginaRequisitos.isEmpty()) {
            throw new NoDataFoundException();
        }

        // Crear el mapa de respuesta que incluye totalData y los datos de la página
        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaRequisitos.getTotalElements());
        response.put("data", paginaRequisitos.getContent());

        return response;
    }

    @Override
    public PreRequisito getPreRequisito(Long id) {
        return preRequisitoRepository.findById(id).orElseThrow(PreRequisitoNotFound::new);
    }

    /**
     * Guardar un pre-requisito.
     *
     * @param preRequisitoDto - Informacion de el pre-requisito.
     * */
    @Override
    public void savePreRequisito(PreRequisitoDto preRequisitoDto) {
        PreRequisito preRequisito = preRequisitoMapper.toEntity(preRequisitoDto);
        preRequisitoRepository.save(preRequisito);
    }

    /**
     * Elimina un pre-requisito por su identificador único.
     *
     * @param id - Identificador único del pre-requisito a eliminar
     * @throws PreRequisitoNotFound - Se lanza si no se encuentra el pre-requisito con el ID especificado
     */
    @Override
    public void deletePrerequisito(Long id) {
        preRequisitoRepository.findById(id)
                .orElseThrow(PreRequisitoNotFound::new);

        preRequisitoRepository.deleteById(id);
    }
}
