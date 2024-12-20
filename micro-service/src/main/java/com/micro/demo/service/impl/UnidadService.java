package com.micro.demo.service.impl;

import com.micro.demo.controller.dto.UnidadDto;
import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.EvaluacionResultadoAprendizaje;
import com.micro.demo.entities.Tema;
import com.micro.demo.entities.Unidad;
import com.micro.demo.mapper.UnidadMapper;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.IResultadoAprendizajeRepository;
import com.micro.demo.repository.ITemaRepository;
import com.micro.demo.repository.IUnidadRepository;
import com.micro.demo.service.IUnidadService;
import com.micro.demo.service.exceptions.AsignaturaNotFound;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.TemasNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UnidadService implements IUnidadService {
    private final IUnidadRepository unidadRepository;
    private final IAsignaturaRepository asignaturaRepository;
    private final UnidadMapper unidadMapper;
    private final ITemaRepository temaRepository;
    private final IResultadoAprendizajeRepository resultadoAprendizajeRepository;

    public UnidadService(IUnidadRepository unidadRepository, IAsignaturaRepository asignaturaRepository, UnidadMapper unidadMapper, ITemaRepository temaRepository, IResultadoAprendizajeRepository resultadoAprendizajeRepository) {
        this.unidadRepository = unidadRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.unidadMapper = unidadMapper;
        this.temaRepository = temaRepository;
        this.resultadoAprendizajeRepository = resultadoAprendizajeRepository;
    }

    /**
     * Obtiene las unidades mediante la paginacion
     *
     * @param pagina numero de pagina
     * @param elementosXpagina elementos que habran en cada pagina
     * @return Lista de unidades
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllUnidad(Integer pagina, Integer elementosXpagina) {
        Page<Unidad> paginaUnidades;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            paginaUnidades = new PageImpl<>(unidadRepository.findAll(Sort.by("id").ascending()));
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaUnidades = unidadRepository.findAll(
                    PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending())
            );
        }

        if (paginaUnidades.isEmpty()) {
            throw new NoDataFoundException();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaUnidades.getTotalElements());
        response.put("unidades", paginaUnidades.getContent());

        return response;
    }


    /**
     * Obtiene una unidad por su identificador unico
     *
     * @param id - Identificador unico de la unidad
     * @throws NoDataFoundException - Si no se encuentra datos.
     */
    @Override
    public Unidad getUnidad(Long id) {
        return unidadRepository.findById(id).orElseThrow(NoDataFoundException::new);
    }

    /**
     * Guardar una unidad
     *
     * @param unidadDto - Informacion de la unidad
     * @throws AsignaturaNotFound - Se lanza si no existe una asignatura con el codigo previamente dicho.
     * */
    @Override
    public void saveUnidad(UnidadDto unidadDto) {
        Unidad unidad = unidadMapper.toEntity(unidadDto);

        if (unidadDto.getAsignatura() != null) {
            Asignatura asignatura = asignaturaRepository.findByCodigo(Math.toIntExact(unidadDto.getAsignatura()));
            if (asignatura == null) {
                throw new AsignaturaNotFound();
            }
            unidad.setAsignatura(asignatura);
        }

        // Manejo de temas
        if (unidadDto.getTemas() != null && !unidadDto.getTemas().isEmpty()) {
            List<Tema> temas = unidadDto.getTemas().stream()
                    .map(id -> temaRepository.findById(id)
                            .orElseThrow(TemasNotFoundException::new))
                    .collect(Collectors.toList());
            unidad.setTemas(temas);
        }

        // Manejo de resultados con ResultadoAprendizaje
        if (unidadDto.getResultados() != null && !unidadDto.getResultados().isEmpty()) {
            List<EvaluacionResultadoAprendizaje> resultadoAprendizaje = unidadDto.getResultados().stream()
                    .map(id -> {
                        EvaluacionResultadoAprendizaje intermedia = new EvaluacionResultadoAprendizaje();
                        intermedia.setUnidad(unidad);
                        intermedia.setResultadoAprendizaje(resultadoAprendizajeRepository.findById(id)
                                .orElseThrow(NoDataFoundException::new));
                        return intermedia;
                    })
                    .collect(Collectors.toList());
            unidad.setResultadoAprendizaje(resultadoAprendizaje);
        }

        unidadRepository.save(unidad);
    }




    /**
     * Actualizar una unidad
     *
     * @param id - Identificador unico de la unidad
     * @param unidadDto - Informacion de la unidad
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     **/
    @Override
    public void updateUnidad(Long id, UnidadDto unidadDto) {
        Optional<Unidad> optionalUnidad = unidadRepository.findById(id);

        if (optionalUnidad.isPresent()) {
            Unidad unidadExistente = optionalUnidad.get();

            unidadExistente.setHad(unidadDto.getHad());
            unidadExistente.setHti(unidadDto.getHti());
            unidadExistente.setNombre(unidadDto.getNombre());

            unidadRepository.save(unidadExistente);
        } else throw new NoDataFoundException();
    }

    /**
     * Elimina una unidad por su identificador único.
     *
     * @param id - Identificador único de la unidad a eliminar
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     */
    @Override
    public void deleteUnidad(Long id) {
        unidadRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        unidadRepository.deleteById(id);
    }
}
