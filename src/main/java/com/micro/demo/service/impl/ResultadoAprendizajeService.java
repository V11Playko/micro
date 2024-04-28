package com.micro.demo.service.impl;

import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.entities.Tema;
import com.micro.demo.entities.UnidadResultado;
import com.micro.demo.repository.IResultadoAprendizajeRepository;
import com.micro.demo.service.IResultadoAprendizajeService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ResultadoAprendizajeService implements IResultadoAprendizajeService {
    private final IResultadoAprendizajeRepository resultadoAprendizajeRepository;

    public ResultadoAprendizajeService(IResultadoAprendizajeRepository resultadoAprendizajeRepository) {
        this.resultadoAprendizajeRepository = resultadoAprendizajeRepository;
    }

    @Override
    public List<ResultadoAprendizaje> getAllResultado(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<ResultadoAprendizaje> paginaResultados =
                resultadoAprendizajeRepository.findAll(PageRequest.of(pagina -1, elementosXpagina,
                        Sort.by("id").ascending()));

        if (paginaResultados.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaResultados.getContent();
    }

    @Override
    public void saveResultado(ResultadoAprendizaje resultadoAprendizaje) {
        resultadoAprendizajeRepository.save(resultadoAprendizaje);
    }

    @Override
    public void updateResultado(Long id,ResultadoAprendizaje resultadoAprendizaje) {
        ResultadoAprendizaje existingResultado = resultadoAprendizajeRepository
                .findById(id).orElseThrow(NoDataFoundException::new);

        existingResultado.setNombre(resultadoAprendizaje.getNombre());
        existingResultado.setDescripcion(resultadoAprendizaje.getDescripcion());
        existingResultado.setEstatus(resultadoAprendizaje.isEstatus());

        resultadoAprendizajeRepository.save(existingResultado);
    }

    @Override
    public void deleteResultado(Long id) {
        resultadoAprendizajeRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        resultadoAprendizajeRepository.deleteById(id);
    }
}
