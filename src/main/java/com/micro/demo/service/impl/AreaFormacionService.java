package com.micro.demo.service.impl;

import com.micro.demo.entities.AreaFormacion;
import com.micro.demo.repository.IAreaFormacionRepository;
import com.micro.demo.service.IAreaFormacionService;
import com.micro.demo.service.exceptions.AreaFormacionNotFound;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
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
public class AreaFormacionService implements IAreaFormacionService {
    private final IAreaFormacionRepository areaFormacionRepository;

    public AreaFormacionService(IAreaFormacionRepository areaFormacionRepository) {
        this.areaFormacionRepository = areaFormacionRepository;
    }

    /**
     * Obtiene las Areas de formacion mediante la paginacion
     *
     * @param pagina           - numero de pagina
     * @param elementosXpagina - elementos que habran en cada pagina
     * @return Lista de areas de formacion
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllAreaFormacion(Integer pagina, Integer elementosXpagina) {
        Page<AreaFormacion> paginaAreas;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            List<AreaFormacion> areas = areaFormacionRepository.findAll(Sort.by("id").ascending());
            paginaAreas = new PageImpl<>(areas);
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaAreas = areaFormacionRepository.findAll(PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending()));
        }

        if (paginaAreas.isEmpty()) {
            throw new NoDataFoundException();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaAreas.getTotalElements());
        response.put("data", paginaAreas.getContent());

        return response;
    }


    /**
     * Guardar un area de formacion
     *
     * @param areaFormacion - Informacion del area de formacion
     * */
    @Override
    public void saveAreaFormacion(AreaFormacion areaFormacion) {
        areaFormacionRepository.save(areaFormacion);
    }


    /**
     * Elimina un area de formacion por su identificador único.
     *
     * @param id - Identificador único del area de formacion a eliminar
     * @throws AreaFormacionNotFound - Se lanza si no se encuentra el area de formacion con el ID especificado
     */
    @Override
    public void deleteAreaFormacion(Long id) {
        areaFormacionRepository.findById(id)
                        .orElseThrow(AreaFormacionNotFound::new);

        areaFormacionRepository.deleteById(id);
    }
}
