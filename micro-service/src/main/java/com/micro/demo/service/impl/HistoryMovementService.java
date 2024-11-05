package com.micro.demo.service.impl;

import com.micro.demo.controller.dto.HistoryMovementDto;
import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.Email;
import com.micro.demo.entities.HistoryMovement;
import com.micro.demo.entities.Pensum;
import com.micro.demo.entities.enums.Semesters;
import com.micro.demo.mapper.HistoryMovementMapper;
import com.micro.demo.repository.IAsignaturaPensumRepository;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.IHistoryMovementRepository;
import com.micro.demo.repository.IPensumRepository;
import com.micro.demo.service.IEmailService;
import com.micro.demo.service.IHistoryMovementService;
import com.micro.demo.service.IPensumService;
import com.micro.demo.service.exceptions.AsignaturaAlreadyForAdd;
import com.micro.demo.service.exceptions.AsignaturaAlreadyInPensum;
import com.micro.demo.service.exceptions.AsignaturaAlreadyRemoved;
import com.micro.demo.service.exceptions.AsignaturaNotFound;
import com.micro.demo.service.exceptions.AtributosNotFound;
import com.micro.demo.service.exceptions.CambiosAceptadosNotFoundException;
import com.micro.demo.service.exceptions.CodigoNotFound;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.ModificationPeriodDisabled;
import com.micro.demo.service.exceptions.ModificationPeriodWorking;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.PensumNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.micro.demo.configuration.Constants.MESSAGE_ADDED_ASIGNATURA;
import static com.micro.demo.configuration.Constants.MESSAGE_EDITED_ASIGNATURA;
import static com.micro.demo.configuration.Constants.MESSAGE_REMOVED_ASIGNATURA;
import static com.micro.demo.configuration.Constants.SUBJECT_APPROVE_OR_REJECT_CAMBIOS;
import static com.micro.demo.configuration.Constants.SUBJECT_EDITED_ASIGNATURA;

@Service
@Transactional
public class HistoryMovementService implements IHistoryMovementService {
    private final IHistoryMovementRepository historyMovementRepository;
    private final IAsignaturaPensumRepository asignaturaPensumRepository;
    private final IAsignaturaRepository asignaturaRepository;
    private final IPensumRepository pensumRepository;
    private final IPensumService pensumService;
    private final IEmailService emailService;
    private final HistoryMovementMapper historyMovementMapper;

    public HistoryMovementService(IHistoryMovementRepository historyMovementRepository, IAsignaturaPensumRepository asignaturaPensumRepository, IAsignaturaRepository asignaturaRepository, IPensumRepository pensumRepository, IPensumService pensumService, IEmailService emailService, HistoryMovementMapper historyMovementMapper) {
        this.historyMovementRepository = historyMovementRepository;
        this.asignaturaPensumRepository = asignaturaPensumRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.pensumRepository = pensumRepository;
        this.pensumService = pensumService;
        this.emailService = emailService;
        this.historyMovementMapper = historyMovementMapper;
    }


    /**
     * Obtiene las historias de movimiento mediante la paginacion
     *
     * @param pagina           numero de pagina.
     * @param elementosXpagina elementos que habran en cada pagina.
     * @return Lista de las historias de movimiento.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1.
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllMovements(Integer pagina, Integer elementosXpagina) {
        Page<HistoryMovement> paginaMovements;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            paginaMovements = new PageImpl<>(historyMovementRepository.findAll(Sort.by("id").ascending()));
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaMovements = historyMovementRepository.findAll(PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending()));
        }

        if (paginaMovements.isEmpty()) {
            throw new NoDataFoundException();
        }

        // Crear el mapa de respuesta que incluye totalData y los datos de la página o lista completa
        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaMovements.getTotalElements());
        response.put("data", paginaMovements.getContent());

        return response;
    }

    @Override
    public HistoryMovement getHistoryMovement(Long id) {
        return historyMovementRepository.findById(id).orElseThrow(NoDataFoundException::new);
    }


    /**
     * Agregar una asignatura al historial de movimiento.
     *
     * @param historyMovementDto - Informacion del historial de movimiento.
     * @throws AsignaturaNotFound - Se lanza si la asignatura indicada no se encuentra.
     * @throws PensumNotFoundException - Se lanza si el pensum indicado no se encuentra.
     * @throws ModificationPeriodDisabled - Se lanza si el periodo de modificacion esta deshabilitado.
     * @throws AsignaturaAlreadyInPensum - Se lanza si la asignatura ya esta relacionada con el pensum.
     * @throws AsignaturaAlreadyForAdd - Se lanza si la asignatura ya esta agregada en el historial y sus cambios aceptados son true o null.
     *
     * */
    @Override
    public void agregarAsignatura(HistoryMovementDto historyMovementDto) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();

        HistoryMovement historyMovement = historyMovementMapper.toEntity(historyMovementDto);

        Asignatura asignatura = asignaturaRepository.findById(historyMovementDto.getAsignaturaAfectadaId())
                .orElseThrow(AsignaturaNotFound::new);
        Pensum pensum = pensumRepository.findById(historyMovementDto.getPensumId())
                .orElseThrow(PensumNotFoundException::new);

        if (pensum.getProgramaAcademico().getFechaInicioModificacion() == null ||
                pensum.getProgramaAcademico().getDuracionModificacion() == null) {
            throw new ModificationPeriodDisabled();
        }

        if (asignaturaPensumRepository.existsByAsignaturaAndPensum(asignatura, pensum)) {
            throw new AsignaturaAlreadyInPensum();
        }

        if (historyMovementRepository.existsByAsignaturaAfectadaAndAsignaturaAgregadaTrueAndCambiosAceptadosNull(asignatura)) {
            throw new AsignaturaAlreadyForAdd();
        }

        historyMovement.setCorreoDocente(correoUsuarioAutenticado);
        historyMovement.setAsignaturaAfectada(asignatura);
        historyMovement.setPensum(pensum);
        historyMovement.setAsignaturaAgregada(true);
        historyMovement.setAsignaturaRemovida(false);
        historyMovement.setAsignaturaActualizada(false);
        historyMovement.setCambiosAceptados(null);
        historyMovement.setProgramaAcademico(pensum.getProgramaAcademico());
        historyMovement.setFechaMovimiento(LocalDateTime.now());

        enviarCorreosAsignaturaAgregada(historyMovement, asignatura);

        historyMovementRepository.save(historyMovement);
    }

    /**
     * Remover una asignatura al historial de movimiento.
     *
     * @param historyMovementDto - Informacion del historial de movimiento.
     * @throws AsignaturaNotFound - Se lanza si la asignatura indicada no se encuentra.
     * @throws PensumNotFoundException - Se lanza si el pensum indicado no se encuentra.
     * @throws ModificationPeriodDisabled - Se lanza si el periodo de modificacion esta deshabilitado.
     * @throws AsignaturaAlreadyRemoved - Se lanza si la asignatura ya esta lista para ser removida en el historial.
     * */
    @Override
    public void removerAsignatura(HistoryMovementDto historyMovementDto) {
        HistoryMovement historyMovement = historyMovementMapper.toEntity(historyMovementDto);

        Asignatura asignatura = asignaturaRepository.findById(historyMovementDto.getAsignaturaAfectadaId())
                .orElseThrow(AsignaturaNotFound::new);
        Pensum pensum = pensumRepository.findById(historyMovementDto.getPensumId())
                .orElseThrow(PensumNotFoundException::new);

        if (pensum.getProgramaAcademico().getFechaInicioModificacion() == null ||
                pensum.getProgramaAcademico().getDuracionModificacion() == null) {
            throw new ModificationPeriodDisabled();
        }

        if (historyMovementRepository.existsByAsignaturaAfectadaAndAsignaturaRemovidaTrueAndCambiosAceptadosNull(asignatura)) {
            throw new AsignaturaAlreadyRemoved();
        }

        historyMovement.setCorreoDocente(getCorreoUsuarioAutenticado());
        historyMovement.setAsignaturaAgregada(false);
        historyMovement.setAsignaturaRemovida(true);
        historyMovement.setAsignaturaActualizada(false);
        historyMovement.setCambiosAceptados(null);
        historyMovement.setProgramaAcademico(pensum.getProgramaAcademico());
        historyMovement.setFechaMovimiento(LocalDateTime.now());

        enviarCorreosAsignaturaRemovida(historyMovement, asignatura);

        // Guardar el movimiento en la base de datos
        historyMovementRepository.save(historyMovement);
    }


    /**
     * Agregar una asignatura al historial de movimiento.
     *
     * @param historyMovementDto - Informacion del historial de movimiento.
     * @throws AsignaturaNotFound - Se lanza si la asignatura indicada no se encuentra.
     * @throws PensumNotFoundException - Se lanza si el pensum indicado no se encuentra.
     * @throws ModificationPeriodDisabled - Se lanza si el periodo de modificacion esta deshabilitado.
     * @throws AtributosNotFound - Se lanza si los atributos a modificar son nulos.
     * */
    @Override
    public void actualizarAsignatura(HistoryMovementDto historyMovementDto) {
        HistoryMovement historyMovement = historyMovementMapper.toEntity(historyMovementDto);

        Asignatura asignaturaAfectada = asignaturaRepository.findById(historyMovementDto.getAsignaturaAfectadaId())
                .orElseThrow(AsignaturaNotFound::new);
        Pensum pensum = pensumRepository.findById(historyMovementDto.getPensumId())
                .orElseThrow(PensumNotFoundException::new);

        if (pensum.getProgramaAcademico().getFechaInicioModificacion() == null ||
                pensum.getProgramaAcademico().getDuracionModificacion() == null) {
            throw new ModificationPeriodDisabled();
        }

        Map<String, String> atributosModificados = historyMovementDto.getAtributosModificados();
        if (atributosModificados == null || atributosModificados.isEmpty()) {
            throw new AtributosNotFound();
        }

        // Crear una nueva instancia de Asignatura y actualizar los atributos modificados
        Asignatura asignaturaActualizada = new Asignatura();
        actualizarAtributosAsignatura(asignaturaActualizada, atributosModificados);

        historyMovement.setAsignaturaAfectada(asignaturaAfectada);
        historyMovement.setCorreoDocente(getCorreoUsuarioAutenticado());
        historyMovement.setAsignaturaAgregada(false);
        historyMovement.setAsignaturaRemovida(false);
        historyMovement.setAsignaturaActualizada(true);
        historyMovement.setCambiosAceptados(null);
        historyMovement.setProgramaAcademico(pensum.getProgramaAcademico());
        historyMovement.setFechaMovimiento(LocalDateTime.now());

        enviarCorreosAsignaturaActualizada(historyMovement, asignaturaActualizada);

        historyMovementRepository.save(historyMovement);
    }


    /**
     * Aprobar o rechazar cambios propuestos despues del periodo de modificacion.
     *
     * @param aceptarCambios - Se aprueban los cambios o no.
     * @param codigo - Codigo de los registros a los cuales se les aceptaran los cambios o no.
     * @param reasonMessage - Mensaje del director sobre el por que sobre su decision de aprobar o rechazar los cambios.
     * @throws PensumNotFoundException - Se lanza si el pensum indicado no se encuentra.
     * @throws ModificationPeriodWorking - Se lanza si el periodo de modificacion todavia esta vigente,
     * */
    @Override
    public void aprobarRechazarCambiosDespuesPeriodoModificacion(boolean aceptarCambios, Integer codigo, String reasonMessage) {
        LocalDate fechaActual = LocalDate.now();

        List<HistoryMovement> cambiosPropuestos = historyMovementRepository.findByCambiosAceptadosIsNullAndCodigo(codigo);

        if (cambiosPropuestos.isEmpty()) {
            throw new CodigoNotFound();
        }

        for (HistoryMovement cambio : cambiosPropuestos) {
            Pensum pensum = pensumRepository.findById(cambio.getPensum().getId()).orElseThrow(PensumNotFoundException::new);
            LocalDate fechaInicioModificacion = pensum.getProgramaAcademico().getFechaInicioModificacion();
            Integer duracionModificacion = pensum.getProgramaAcademico().getDuracionModificacion();

            if ((fechaInicioModificacion == null || duracionModificacion == null) || (fechaActual.isAfter(fechaInicioModificacion.plusDays(duracionModificacion)))) {
                cambio.setCambiosAceptados(aceptarCambios);
                historyMovementRepository.save(cambio);

                Email email = new Email();
                email.setDestinatario(cambio.getCorreoDocente());
                email.setAsunto(SUBJECT_APPROVE_OR_REJECT_CAMBIOS);
                email.setMensaje(reasonMessage);
                emailService.sendMail(email);
            } else {
                throw new ModificationPeriodWorking();
            }
        }
    }

    /**
     * Aplicar los cambios propuestos
     *
     * @param codigo - Codigo de los registros a los cuales se les aplicaran los cambios.
     * @throws CambiosAceptadosNotFoundException - Se lanza si no hay registros con el codigo previamente elegido.
     * @throws AsignaturaNotFound - Se lanza si la asignatura indicada no se encuentra.
     * */
    @Override
    @Transactional
    public void aplicarCambiosPropuestos(Integer codigo) {
        List<HistoryMovement> cambiosPropuestos = historyMovementRepository.findByCambiosAceptadosTrueAndCodigo(codigo);

        if (cambiosPropuestos.isEmpty()) {
            throw new CambiosAceptadosNotFoundException();
        }

        // Agrupar cambios por tipo de operación
        List<HistoryMovement> cambiosAgregarAsignaturas = cambiosPropuestos.stream()
                .filter(HistoryMovement::isAsignaturaAgregada)
                .toList();

        List<HistoryMovement> cambiosRemoverAsignaturas = cambiosPropuestos.stream()
                .filter(HistoryMovement::isAsignaturaRemovida)
                .toList();

        List<HistoryMovement> cambiosActualizarAsignaturas = cambiosPropuestos.stream()
                .filter(HistoryMovement::isAsignaturaActualizada)
                .toList();

        for (HistoryMovement cambio : cambiosAgregarAsignaturas) {
            Pensum pensum = pensumRepository.findById(cambio.getPensum().getId()).orElseThrow(PensumNotFoundException::new);
            Asignatura asignatura = asignaturaRepository.findById(cambio.getAsignaturaAfectada().getId()).orElseThrow(AsignaturaNotFound::new);

            pensumService.assignAsignaturas(pensum.getId(), Arrays.asList(asignatura.getId()));
        }

        Map<Pensum, List<Long>> asignaturasPorPensum = new HashMap<>();
        for (HistoryMovement cambio : cambiosRemoverAsignaturas) {
            Pensum pensum = pensumRepository.findById(cambio.getPensum().getId()).orElseThrow(PensumNotFoundException::new);
            Asignatura asignatura = asignaturaRepository.findById(cambio.getAsignaturaAfectada().getId()).orElseThrow(AsignaturaNotFound::new);

            // Agrupar las asignaturas a remover por pensum
            asignaturasPorPensum.computeIfAbsent(pensum, k -> new ArrayList<>()).add(asignatura.getId());
        }

        for (Map.Entry<Pensum, List<Long>> entry : asignaturasPorPensum.entrySet()) {
            Pensum pensum = entry.getKey();
            pensumService.duplicatePensum(pensum.getId());

            Pensum nuevoPensum = pensumRepository.findTopByOrderByIdDesc();
            for (Long asignaturaId : entry.getValue()) {
                pensumService.removeAsignaturaFromPensum(nuevoPensum.getId(), asignaturaId);
            }
        }

        for (HistoryMovement cambio : cambiosActualizarAsignaturas) {
            Asignatura asignaturaAfectada = asignaturaRepository.findById(cambio.getAsignaturaAfectada().getId())
                    .orElseThrow(AsignaturaNotFound::new);

            actualizarAtributosAsignatura(asignaturaAfectada, cambio.getAtributosModificados());

            asignaturaRepository.save(asignaturaAfectada);
        }
    }



    private String getCorreoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();  // El correo está en el principal
        }
        throw new RuntimeException("Error obteniendo el correo del token.");
    }


    private void enviarCorreosAsignaturaAgregada(HistoryMovement historyMovement, Asignatura asignatura) {
        // Enviar correo al docente
        Email email = new Email();
        email.setDestinatario(historyMovement.getCorreoDocente());
        email.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje = String.format(MESSAGE_ADDED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email.setMensaje(mensaje);
        emailService.sendMail(email);

        // Enviar correo al director del programa académico
        Email email2 = new Email();
        email2.setDestinatario(historyMovement.getProgramaAcademico().getDirector().getCorreo());
        email2.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje2 = String.format(MESSAGE_ADDED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email2.setMensaje(mensaje2);
        emailService.sendMail(email2);
    }

    private void enviarCorreosAsignaturaActualizada(HistoryMovement historyMovement, Asignatura asignatura) {
        // Enviar correo al docente
        Email email = new Email();
        email.setDestinatario(historyMovement.getCorreoDocente());
        email.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje = String.format(MESSAGE_EDITED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email.setMensaje(mensaje);
        emailService.sendMail(email);

        // Enviar correo al director del programa académico
        Email email2 = new Email();
        email2.setDestinatario(historyMovement.getProgramaAcademico().getDirector().getCorreo());
        email2.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje2 = String.format(MESSAGE_EDITED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email2.setMensaje(mensaje2);
        emailService.sendMail(email2);
    }

    private void enviarCorreosAsignaturaRemovida(HistoryMovement historyMovement, Asignatura asignatura) {
        // Enviar correo al docente
        Email email = new Email();
        email.setDestinatario(historyMovement.getCorreoDocente());
        email.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje = String.format(MESSAGE_REMOVED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email.setMensaje(mensaje);
        emailService.sendMail(email);

        // Enviar correo al director del programa académico
        Email email2 = new Email();
        email2.setDestinatario(historyMovement.getProgramaAcademico().getDirector().getCorreo());
        email2.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje2 = String.format(MESSAGE_REMOVED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email2.setMensaje(mensaje2);
        emailService.sendMail(email2);
    }

    private void actualizarAtributosAsignatura(Asignatura asignatura, Map<String, String> atributosModificados) {
        if (atributosModificados.containsKey("nombre")) {
            asignatura.setNombre(atributosModificados.get("nombre"));
        }
        // El código ha sido eliminado de la actualización
        // if (atributosModificados.containsKey("codigo")) {
        //     asignatura.setCodigo(Integer.parseInt(atributosModificados.get("codigo")));
        // }
        if (atributosModificados.containsKey("accFormacionInv")) {
            asignatura.setAccFormacionInv(atributosModificados.get("accFormacionInv"));
        }
        if (atributosModificados.containsKey("bibliografia")) {
            asignatura.setBibliografia(atributosModificados.get("bibliografia"));
        }
        if (atributosModificados.containsKey("creditos")) {
            asignatura.setCreditos(Integer.parseInt(atributosModificados.get("creditos")));
        }
        if (atributosModificados.containsKey("had")) {
            asignatura.setHad(atributosModificados.get("had"));
        }
        if (atributosModificados.containsKey("hti")) {
            asignatura.setHti(atributosModificados.get("hti"));
        }
        if (atributosModificados.containsKey("hadhti")) {
            asignatura.setHadhti(atributosModificados.get("hadhti"));
        }
        if (atributosModificados.containsKey("justificacion")) {
            asignatura.setJustificacion(atributosModificados.get("justificacion"));
        }
        if (atributosModificados.containsKey("metodologia")) {
            asignatura.setMetodologia(atributosModificados.get("metodologia"));
        }
        if (atributosModificados.containsKey("objetivo")) {
            List<String> objetivos = new ArrayList<>();
            objetivos.add(atributosModificados.get("objetivo"));
            asignatura.setObjetivos(objetivos);
        }
        if (atributosModificados.containsKey("semestre")) {
            asignatura.setSemestre(Semesters.valueOf(atributosModificados.get("semestre")));
        }
        if (atributosModificados.containsKey("tipoCredito")) {
            asignatura.setTipoCredito(atributosModificados.get("tipoCredito"));
        }
        if (atributosModificados.containsKey("tipoCurso")) {
            asignatura.setTipoCurso(atributosModificados.get("tipoCurso"));
        }
    }

}