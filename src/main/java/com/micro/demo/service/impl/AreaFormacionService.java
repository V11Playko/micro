package com.micro.demo.service.impl;

import com.micro.demo.entities.AreaFormacion;
import com.micro.demo.repository.IAreaFormacionRepository;
import com.micro.demo.service.IAreaFormacionService;
import com.micro.demo.service.exceptions.AreaFormacionNotFound;
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
public class AreaFormacionService implements IAreaFormacionService {
    private final IAreaFormacionRepository areaFormacionRepository;

    public AreaFormacionService(IAreaFormacionRepository areaFormacionRepository) {
        this.areaFormacionRepository = areaFormacionRepository;
    }

    @Override
    public List<AreaFormacion> getAllAreaFormacion(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<AreaFormacion> paginaAreas =
                areaFormacionRepository.findAll(PageRequest.of(pagina -1, elementosXpagina, Sort.by("id").ascending()));

        if (paginaAreas.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaAreas.getContent();
    }

    @Override
    public void saveAreaFormacion(AreaFormacion areaFormacion) {
        areaFormacionRepository.save(areaFormacion);
    }

    @Override
    public void deleteAreaFormacion(Long id) {
        areaFormacionRepository.findById(id)
                        .orElseThrow(AreaFormacionNotFound::new);

        areaFormacionRepository.deleteById(id);
    }
}
