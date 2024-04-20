package com.micro.demo.service.impl;

import com.micro.demo.entities.Unidad;
import com.micro.demo.repository.IUnidadRepository;
import com.micro.demo.service.IUnidadService;
import com.micro.demo.service.exceptions.NoDataFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnidadService implements IUnidadService {
    private final IUnidadRepository unidadRepository;

    public UnidadService(IUnidadRepository unidadRepository) {
        this.unidadRepository = unidadRepository;
    }

    @Override
    public List<Unidad> getAllUnidad() {
        List<Unidad> unidads = unidadRepository.findAll();
        if (unidads.isEmpty()) throw new NoDataFoundException();
        return unidads ;
    }

    @Override
    public Unidad getUnidad(Long id) {
        return unidadRepository.findById(id).orElseThrow(NoDataFoundException::new);
    }

    @Override
    public void saveUnidad(Unidad unidad) {
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
        unidadRepository.deleteById(id);
    }
}
