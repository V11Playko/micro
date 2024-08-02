package com.micro.demo.service.impl;

import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.Tema;
import com.micro.demo.entities.Unidad;
import com.micro.demo.entities.UnidadResultado;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.IUnidadRepository;
import com.micro.demo.service.IUnidadService;
import com.micro.demo.service.exceptions.AsignaturaNotFound;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnidadService implements IUnidadService {
    private final IUnidadRepository unidadRepository;
    private final IAsignaturaRepository asignaturaRepository;

    public UnidadService(IUnidadRepository unidadRepository, IAsignaturaRepository asignaturaRepository) {
        this.unidadRepository = unidadRepository;
        this.asignaturaRepository = asignaturaRepository;
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
    public List<Unidad> getAllUnidad(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<Unidad> paginaUnidades =
                unidadRepository.findAll(PageRequest.of(pagina -1, elementosXpagina, Sort.by("id").ascending()));

        if (paginaUnidades.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaUnidades.getContent();
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
     * @param unidad - Informacion de la unidad
     * @throws AsignaturaNotFound - Se lanza si no existe una asignatura con el codigo previamente dicho.
     * */
    @Override
    public void saveUnidad(Unidad unidad) {
        if (unidad.getAsignatura() != null && unidad.getAsignatura().getCodigo() != null) {
            Asignatura asignatura = asignaturaRepository.findByCodigo(unidad.getAsignatura().getCodigo());
            if (asignatura == null) throw new AsignaturaNotFound();
            unidad.setAsignatura(asignatura);
        }

        if (unidad.getTemas() != null) {
            for (Tema tema : unidad.getTemas()) {
                tema.setUnidad(unidad);
            }
        }

        if (unidad.getResultados() != null) {
            for (UnidadResultado resultado : unidad.getResultados()) {
                resultado.setUnidad(unidad);
            }
        }

        unidadRepository.save(unidad);
    }


    /**
     * Actualizar una unidad
     *
     * @param id - Identificador unico de la unidad
     * @param unidad - Informacion de la unidad
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     **/
    @Override
    public void updateUnidad(Long id, Unidad unidad) {
        Optional<Unidad> optionalUnidad = unidadRepository.findById(id);

        if (optionalUnidad.isPresent()) {
            Unidad unidadExistente = optionalUnidad.get();

            unidadExistente.setHad(unidad.getHad());
            unidadExistente.setHti(unidad.getHti());
            unidadExistente.setNombre(unidad.getNombre());

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
