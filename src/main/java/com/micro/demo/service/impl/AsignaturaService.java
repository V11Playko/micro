package com.micro.demo.service.impl;

import com.micro.demo.entities.AreaFormacion;
import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.AsignaturaDocente;
import com.micro.demo.entities.AsignaturaPensum;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IAreaFormacionRepository;
import com.micro.demo.repository.IAsignaturaDocenteRepository;
import com.micro.demo.repository.IAsignaturaPensumRepository;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.IPreRequisitoRepository;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IAsignaturaService;
import com.micro.demo.service.exceptions.AllDocentesAssignsException;
import com.micro.demo.service.exceptions.AreaFormacionNotFound;
import com.micro.demo.service.exceptions.AsignaturaNotFoundByIdException;
import com.micro.demo.service.exceptions.DocenteNotAssignException;
import com.micro.demo.service.exceptions.DocenteNotFound;
import com.micro.demo.service.exceptions.DocenteNotFoundCorreoException;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.PreRequisitoNotFound;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AsignaturaService implements IAsignaturaService {
    private final IAsignaturaRepository asignaturaRepository;
    private final IAsignaturaDocenteRepository asignaturaDocenteRepository;
    private final IAsignaturaPensumRepository asignaturaPensumRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IAreaFormacionRepository areaFormacionRepository;
    private final IPreRequisitoRepository preRequisitoRepository;

    public AsignaturaService(IAsignaturaRepository asignaturaRepository, IAsignaturaDocenteRepository asignaturaDocenteRepository, IAsignaturaPensumRepository asignaturaPensumRepository, IUsuarioRepository usuarioRepository, IAreaFormacionRepository areaFormacionRepository, IPreRequisitoRepository preRequisitoRepository) {
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaDocenteRepository = asignaturaDocenteRepository;
        this.asignaturaPensumRepository = asignaturaPensumRepository;
        this.usuarioRepository = usuarioRepository;
        this.areaFormacionRepository = areaFormacionRepository;
        this.preRequisitoRepository = preRequisitoRepository;
    }

    @Override
    public List<Asignatura> getAllAsignatura(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<Asignatura> paginaAsignaturas =
                asignaturaRepository.findAll(PageRequest.of(pagina -1, elementosXpagina, Sort.by("id").ascending()));

        if (paginaAsignaturas.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaAsignaturas.getContent();
    }

    @Override
    public void saveAsignatura(Asignatura asignatura) {
        areaFormacionRepository.findById
                (asignatura.getAreaFormacion().getId()).orElseThrow(AreaFormacionNotFound::new);
        preRequisitoRepository.findById
                (asignatura.getPreRequisito().getId()).orElseThrow(PreRequisitoNotFound::new);

        asignaturaRepository.save(asignatura);
    }

    @Override
    public void updateAsignatura(Long id, Asignatura asignatura) {
        Asignatura existingAsignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la asignatura con el ID: " + id));

        existingAsignatura.setNombre(asignatura.getNombre());
        existingAsignatura.setCodigo(asignatura.getCodigo());
        existingAsignatura.setAccFormacionInv(asignatura.getAccFormacionInv());
        existingAsignatura.setBibliografia(asignatura.getBibliografia());
        existingAsignatura.setCreditos(asignatura.getCreditos());
        existingAsignatura.setHad(asignatura.getHad());
        existingAsignatura.setHti(asignatura.getHti());
        existingAsignatura.setHadhti(asignatura.getHadhti());
        existingAsignatura.setJustificacion(asignatura.getJustificacion());
        existingAsignatura.setMetodologia(asignatura.getMetodologia());
        existingAsignatura.setObjetivo(asignatura.getObjetivo());
        existingAsignatura.setSemestre(asignatura.getSemestre());
        existingAsignatura.setTipoCredito(asignatura.getTipoCredito());
        existingAsignatura.setTipoCurso(asignatura.getTipoCurso());

        asignaturaRepository.save(existingAsignatura);
    }

    @Override
    public void assignDocentes(Long asignaturaId, List<String> correoDocentes) {
        Asignatura asignatura = asignaturaRepository.findById(asignaturaId)
                .orElseThrow(() -> new AsignaturaNotFoundByIdException(asignaturaId));

        // Obtener los usuarios por correo electrónico
        List<Usuario> docentes = usuarioRepository.findAllByCorreoIn(correoDocentes);

        if (docentes.size() != correoDocentes.size()) {
            throw new DocenteNotFound();
        }

        // Verificar si todos los usuarios son docentes
        for (Usuario docente : docentes) {
            if (!docente.getRole().getNombre().equals("ROLE_DOCENTE")) {
                throw new DocenteNotFoundCorreoException(docente.getCorreo());
            }
        }

        // Verificar si los docentes ya están asignados a la asignatura
        List<String> correosAsignados = asignatura.getAsignaturaDocentes().stream()
                .map(docente -> docente.getUsuario().getCorreo())
                .toList();
        List<String> correosNuevos = correoDocentes.stream()
                .filter(correo -> !correosAsignados.contains(correo))
                .toList();
        if (correosNuevos.isEmpty()) {
            throw new AllDocentesAssignsException();
        }

        // Filtrar los docentes encontrados que no están asignados a la asignatura
        List<Usuario> docentesNoAsignados = docentes.stream()
                .filter(docente -> correosNuevos.contains(docente.getCorreo()))
                .toList();

        // Asignar los docentes a la asignatura a través de la relación muchos a muchos
        List<AsignaturaDocente> asignaturasDocentes = docentesNoAsignados.stream()
                .map(docente -> {
                    AsignaturaDocente asignaturaDocente = new AsignaturaDocente();
                    asignaturaDocente.setAsignatura(asignatura);
                    asignaturaDocente.setUsuario(docente);
                    return asignaturaDocente;
                })
                .toList();
        asignaturaDocenteRepository.saveAll(asignaturasDocentes);
    }

    @Override
    public void removeDocente(Long asignaturaId, String correoDocente) {
        Asignatura asignatura = asignaturaRepository.findById(asignaturaId)
                .orElseThrow(() -> new AsignaturaNotFoundByIdException(asignaturaId));

        Usuario docente = usuarioRepository.findByCorreo(correoDocente);
        if (docente == null) throw new DocenteNotFoundCorreoException(docente.getCorreo());

        // Verificar si el docente está asignado a la asignatura
        AsignaturaDocente asignaturaDocente = asignatura.getAsignaturaDocentes().stream()
                .filter(ad -> ad.getUsuario().getCorreo().equals(correoDocente))
                .findFirst()
                .orElseThrow(DocenteNotAssignException::new);

        // Eliminar la relación entre la asignatura y el docente
        asignatura.getAsignaturaDocentes().remove(asignaturaDocente);
        asignaturaDocenteRepository.delete(asignaturaDocente);
    }

    @Override
    public void deleteAsignatura(Long id) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new AsignaturaNotFoundByIdException(id));

        // Eliminar las relaciones de asignatura-docente asociadas a esta asignatura
        List<AsignaturaDocente> asignaturaDocentes = asignatura.getAsignaturaDocentes();
        asignaturaDocenteRepository.deleteAll(asignaturaDocentes);

        List<AsignaturaPensum> asignaturaPensums = asignatura.getAsignaturaPensum();
        asignaturaPensumRepository.deleteAll(asignaturaPensums);

        // Eliminar la asignatura
        asignaturaRepository.delete(asignatura);
    }
}