package com.micro.demo.service.impl;

import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.AsignaturaDocente;
import com.micro.demo.entities.AsignaturaPensum;
import com.micro.demo.entities.AsignaturaPreRequisito;
import com.micro.demo.entities.Competencia;
import com.micro.demo.entities.PreRequisito;
import com.micro.demo.entities.Usuario;
import com.micro.demo.entities.enums.AsignaturaObligatoria;
import com.micro.demo.entities.enums.ElectivaProfesional;
import com.micro.demo.entities.enums.ElectivaSociohumanistica;
import com.micro.demo.repository.IAreaFormacionRepository;
import com.micro.demo.repository.IAsignaturaDocenteRepository;
import com.micro.demo.repository.IAsignaturaPensumRepository;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.ICompetenciaRepository;
import com.micro.demo.repository.IPreRequisitoRepository;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IAsignaturaService;
import com.micro.demo.service.exceptions.AllDocentesAssignsException;
import com.micro.demo.service.exceptions.AreaFormacionNotFound;
import com.micro.demo.service.exceptions.AsignaturaNotFoundByIdException;
import com.micro.demo.service.exceptions.CompetenciaNotFoundException;
import com.micro.demo.service.exceptions.DocenteNotAssignException;
import com.micro.demo.service.exceptions.DocenteNotFound;
import com.micro.demo.service.exceptions.DocenteNotFoundCorreoException;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.PreRequisitoNotFound;
import com.micro.demo.service.exceptions.TipoCursoIncorrectoException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final ICompetenciaRepository competenciaRepository;

    public AsignaturaService(IAsignaturaRepository asignaturaRepository, IAsignaturaDocenteRepository asignaturaDocenteRepository, IAsignaturaPensumRepository asignaturaPensumRepository, IUsuarioRepository usuarioRepository, IAreaFormacionRepository areaFormacionRepository, IPreRequisitoRepository preRequisitoRepository, ICompetenciaRepository competenciaRepository) {
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaDocenteRepository = asignaturaDocenteRepository;
        this.asignaturaPensumRepository = asignaturaPensumRepository;
        this.usuarioRepository = usuarioRepository;
        this.areaFormacionRepository = areaFormacionRepository;
        this.preRequisitoRepository = preRequisitoRepository;
        this.competenciaRepository = competenciaRepository;
    }

    /**
     * Obtiene las asignaturas mediante la paginacion
     *
     * @param pagina numero de pagina
     * @param elementosXpagina elementos que habran en cada pagina
     * @return Lista de asignaturas
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException - Si no se encuentra datos.
     */
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

    /**
     * Guardar una asignatura
     *
     * @param asignatura - Informacion del area de formacion
     * @throws AreaFormacionNotFound - Se lanza si no se encuentra el area de formacion.
     * @throws PreRequisitoNotFound - Se lanza si no se encuentra el pre-requisito.
     * @throws TipoCursoIncorrectoException - Se lanza si el tipo de curso es incorrecto con respecto al nombre de la asignatura.
     * */
    @Override
    public void saveAsignatura(Asignatura asignatura) {
        // Validaciones previas
        areaFormacionRepository.findById(asignatura.getAreaFormacion().getId()).orElseThrow(AreaFormacionNotFound::new);
        Competencia competencia = competenciaRepository.findById(asignatura.getCompetencia().getId()).orElseThrow(CompetenciaNotFoundException::new);
        asignatura.setCompetencia(competencia);

        // Validación del nombre y tipo de curso
        String nombreAsignatura = asignatura.getNombre();
        String tipoCurso = asignatura.getTipoCurso();

        if (nombreAsignatura != null && tipoCurso != null) {
            switch (tipoCurso) {
                case "OBLIGATORIA":
                    if (!esAsignaturaObligatoria(nombreAsignatura)) {
                        throw new TipoCursoIncorrectoException();
                    }
                    break;
                case "ELECTIVA PROFESIONAL":
                    if (!esElectivaProfesional(nombreAsignatura)) {
                        throw new TipoCursoIncorrectoException();
                    }
                    break;
                case "ELECTIVA SOCIOHUMANISTICA":
                    if (!esElectivaSociohumanistica(nombreAsignatura)) {
                        throw new TipoCursoIncorrectoException();
                    }
                    break;
                default:
                    throw new TipoCursoIncorrectoException();
            }
        } else {
            throw new TipoCursoIncorrectoException();
        }

        // Verificar y manejar preRequisitos
        List<AsignaturaPreRequisito> asignaturaPreRequisitos = new ArrayList<>();
        if (asignatura.getPreRequisitos() != null) {
            for (AsignaturaPreRequisito preRequisito : asignatura.getPreRequisitos()) {
                PreRequisito existente = preRequisitoRepository.findById(preRequisito.getId()).orElseThrow(PreRequisitoNotFound::new);
                AsignaturaPreRequisito asignaturaPreRequisito = new AsignaturaPreRequisito(asignatura, existente);
                asignaturaPreRequisitos.add(asignaturaPreRequisito);
            }
        }
        asignatura.setPreRequisitos(asignaturaPreRequisitos);

        asignaturaRepository.save(asignatura);
    }

    /**
     * Actualiza la información de una asignatura.
     *
     * @param id - Identificador unico de la asignatura a actualizar.
     * @param asignatura - Información actualizada de la asignatura.
     * @throws AsignaturaNotFoundByIdException - Se lanza si no se encuentra la asignatura con el ID especificado.
     */
    @Override
    public void updateAsignatura(Long id, Asignatura asignatura) {
        Asignatura existingAsignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new AsignaturaNotFoundByIdException(id));

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
        existingAsignatura.setObjetivos(asignatura.getObjetivos());
        existingAsignatura.setSemestre(asignatura.getSemestre());
        existingAsignatura.setTipoCredito(asignatura.getTipoCredito());
        existingAsignatura.setTipoCurso(asignatura.getTipoCurso());
        existingAsignatura.setAsignaturaPredecesora(asignatura.getAsignaturaPredecesora());
        existingAsignatura.setAsignaturaSucesora(asignatura.getAsignaturaSucesora());

        asignaturaRepository.save(existingAsignatura);
    }

    /**
     * Asignar un docente a una asignatura.
     *
     * @param asignaturaId - Identificador unico de la asignatura.
     * @param correoDocentes - Correo de los docentes que se asignaran a la asignatura.
     * @throws AsignaturaNotFoundByIdException - Se lanza si no se encuentra la asignatura con el ID especificado.
     * @throws DocenteNotFound - Se lanza si el docente no se encuentra.
     * @throws DocenteNotFoundCorreoException - Se lanza si los usuarios no son docentes.
     * @throws AllDocentesAssignsException - Se lanza si los docentes ya han sido asignados a esa asignatura.
     */
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

    /**
     * Remover un docente de una asignatura.
     *
     * @param asignaturaId - Identificador unico de la asignatura.
     * @param correoDocente - Correo de el docente que se removera.
     * @throws AsignaturaNotFoundByIdException - Se lanza si no se encuentra la asignatura con el ID especificado.
     * @throws DocenteNotFoundCorreoException - Se lanza si el docente no se encuentra mediante el correo indicado.
     * @throws DocenteNotAssignException - Se lanza si el docente no esta asignado a la asignatura de la cual se le quiere remover.
     */
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

    /**
     * Elimina una asignatura por su identificador único.
     *
     * @param id - Identificador único de la asignatura a eliminar
     * @throws AsignaturaNotFoundByIdException - Se lanza si no se encuentra la asignatura con el ID especificado.
     */
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


    /**
     *
     * Método genérico para verificar si el nombre de la asignatura pertenece a un enum dado
     * */
    private <T extends Enum<T>> boolean esAsignatura(String nombreAsignatura, Class<T> enumType) {
        for (T asignatura : enumType.getEnumConstants()) {
            if (asignatura instanceof AsignaturaObligatoria) {
                AsignaturaObligatoria obligatoria = (AsignaturaObligatoria) asignatura;
                if (obligatoria.getNombre().equalsIgnoreCase(nombreAsignatura)) {
                    return true;
                }
            } else if (asignatura instanceof ElectivaProfesional) {
                ElectivaProfesional electiva = (ElectivaProfesional) asignatura;
                if (electiva.getNombre().equalsIgnoreCase(nombreAsignatura)) {
                    return true;
                }
            } else if (asignatura instanceof ElectivaSociohumanistica) {
                ElectivaSociohumanistica sociohumanistica = (ElectivaSociohumanistica) asignatura;
                if (sociohumanistica.getNombre().equalsIgnoreCase(nombreAsignatura)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean esAsignaturaObligatoria(String nombreAsignatura) {
        return esAsignatura(nombreAsignatura, AsignaturaObligatoria.class);
    }

    private boolean esElectivaProfesional(String nombreAsignatura) {
        return esAsignatura(nombreAsignatura, ElectivaProfesional.class);
    }

    private boolean esElectivaSociohumanistica(String nombreAsignatura) {
        return esAsignatura(nombreAsignatura, ElectivaSociohumanistica.class);
    }
}
