package com.micro.demo.service.impl;

import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.Tema;
import com.micro.demo.entities.Unidad;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.ITemaRepository;
import com.micro.demo.repository.IUnidadRepository;
import com.micro.demo.service.IUnidadService;
import com.micro.demo.service.exceptions.AsignaturaNotFound;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.TemasNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UnidadService implements IUnidadService {
    private final IUnidadRepository unidadRepository;
    private final IAsignaturaRepository asignaturaRepository;
    private final ITemaRepository temaRepository;

    public UnidadService(IUnidadRepository unidadRepository, IAsignaturaRepository asignaturaRepository, ITemaRepository temaRepository) {
        this.unidadRepository = unidadRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.temaRepository = temaRepository;
    }

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

    @Override
    public Unidad getUnidad(Long id) {
        return unidadRepository.findById(id).orElseThrow(NoDataFoundException::new);
    }

    @Override
    public void saveUnidad(Unidad unidad) {
        if (unidad.getAsignatura() != null && unidad.getAsignatura().getCodigo() != null) {
            Asignatura asignatura = asignaturaRepository.findByCodigo(unidad.getAsignatura().getCodigo());
            if (asignatura == null) throw new AsignaturaNotFound();
            unidad.setAsignatura(asignatura);
        }

        if (unidad.getTemas() != null && !unidad.getTemas().isEmpty()) {
            List<Long> temaIds = unidad.getTemas().stream()
                    .map(Tema::getId)
                    .collect(Collectors.toList());
            List<Tema> temas = temaRepository.findAllById(temaIds);
            if (temas.isEmpty()) throw new TemasNotFoundException();
            unidad.setTemas(temas);
        }

        unidadRepository.save(unidad);
    }

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

    @Override
    public void deleteUnidad(Long id) {
        unidadRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        unidadRepository.deleteById(id);
    }
}
