package com.micro.demo.service.impl;

import com.micro.demo.entities.Tema;
import com.micro.demo.entities.Unidad;
import com.micro.demo.repository.ITemaRepository;
import com.micro.demo.repository.IUnidadRepository;
import com.micro.demo.service.ITemaService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.TemaNoAssignException;
import com.micro.demo.service.exceptions.UnidadNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TemaService implements ITemaService {
    private final ITemaRepository temaRepository;
    private final IUnidadRepository unidadRepository;

    public TemaService(ITemaRepository temaRepository, IUnidadRepository unidadRepository) {
        this.temaRepository = temaRepository;
        this.unidadRepository = unidadRepository;
    }

    /**
     * Obtiene los temas mediante la paginacion
     *
     * @param pagina           numero de pagina
     * @param elementosXpagina elementos que habran en cada pagina
     * @return Lista de temas.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllTemas(Integer pagina, Integer elementosXpagina) {
        Page<Tema> paginaTemas;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            paginaTemas = new PageImpl<>(temaRepository.findAll(Sort.by("id").ascending()));
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaTemas = temaRepository.findAll(
                    PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending())
            );
        }

        if (paginaTemas.isEmpty()) {
            throw new NoDataFoundException();
        }

        // Crear el mapa de respuesta que incluye totalData y los datos de la página
        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaTemas.getTotalElements());
        response.put("data", paginaTemas.getContent());

        return response;
    }

    @Override
    public Tema getTema(Long id) {
        return temaRepository.findById(id).orElseThrow(NoDataFoundException::new);
    }

    /**
     * Guardar un tema
     *
     * @param tema - Informacion del tema.
     * */
    @Override
    public void saveTema(Tema tema) {
        temaRepository.save(tema);
    }

    /**
     * Actualizar un tema
     *
     * @param id - Identificador unico del tema a actualizar
     * @param tema - Informacion del tema.
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     * */
    @Override
    public void updateTema(Long id, Tema tema) {
        Tema existingTema = temaRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        existingTema.setNombre(tema.getNombre());
        existingTema.setDescripcion(tema.getDescripcion());
        existingTema.setEstatus(tema.isEstatus());

        temaRepository.save(existingTema);
    }

    /**
     * Asignar temas a una unidad
     *
     * @param unidadId - Identificador unico de una unidad
     * @param temaIds - Lista de identificadores unicos de temas
     * @throws UnidadNotFoundException - Se lanza si una unidad no se encuentra.
     * @throws TemaNoAssignException - Se lanza si el tema no pudo asignarse porque su estatus es falso.
     **/
    @Override
    public void assignTemasToUnidad(Long unidadId, List<Long> temaIds) {
        Unidad unidad = unidadRepository.findById(unidadId)
                .orElseThrow(UnidadNotFoundException::new);

        // Obtener los temas por sus IDs y asignarles la unidad
        List<Tema> temas = temaRepository.findAllByIdIn(temaIds);
        for (Tema tema : temas) {
            if (!tema.isEstatus()) {
                throw new TemaNoAssignException(tema.getId());
            }
            tema.setUnidad(unidad);
            temaRepository.save(tema);
        }
    }

    /**
     * Elimina un tema por su identificador único.
     *
     * @param id - Identificador único del tema a eliminar
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     */
    @Override
    public void deleteTema(Long id) {
        temaRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        temaRepository.deleteById(id);
    }
}
