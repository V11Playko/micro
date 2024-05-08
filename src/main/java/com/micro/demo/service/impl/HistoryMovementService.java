package com.micro.demo.service.impl;

import com.micro.demo.configuration.security.userDetails.CustomUserDetails;
import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.Email;
import com.micro.demo.entities.HistoryMovement;
import com.micro.demo.entities.Pensum;
import com.micro.demo.repository.IAsignaturaPensumRepository;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.IHistoryMovementRepository;
import com.micro.demo.repository.IPensumRepository;
import com.micro.demo.repository.IProgramaAcademicoRepository;
import com.micro.demo.service.IAsignaturaService;
import com.micro.demo.service.IEmailService;
import com.micro.demo.service.IHistoryMovementService;
import com.micro.demo.service.IPensumService;
import com.micro.demo.service.exceptions.AsignaturaAlreadyForAdd;
import com.micro.demo.service.exceptions.AsignaturaAlreadyInPensum;
import com.micro.demo.service.exceptions.AsignaturaAlreadyRemoved;
import com.micro.demo.service.exceptions.AsignaturaNotFound;
import com.micro.demo.service.exceptions.AtributosNotFound;
import com.micro.demo.service.exceptions.CambiosAceptadosNotFoundException;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.ModificationPeriodDisabled;
import com.micro.demo.service.exceptions.ModificationPeriodWorking;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.PensumNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private final IProgramaAcademicoRepository programaAcademicoRepository;
    private final IAsignaturaRepository asignaturaRepository;
    private final IPensumRepository pensumRepository;
    private final IAsignaturaService asignaturaService;
    private final IPensumService pensumService;
    private final IEmailService emailService;

    public HistoryMovementService(IHistoryMovementRepository historyMovementRepository, IAsignaturaPensumRepository asignaturaPensumRepository, IProgramaAcademicoRepository programaAcademicoRepository, IAsignaturaRepository asignaturaRepository, IPensumRepository pensumRepository, IAsignaturaService asignaturaService, IPensumService pensumService, IEmailService emailService) {
        this.historyMovementRepository = historyMovementRepository;
        this.asignaturaPensumRepository = asignaturaPensumRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.pensumRepository = pensumRepository;
        this.asignaturaService = asignaturaService;
        this.pensumService = pensumService;
        this.emailService = emailService;
    }


    @Override
    public List<HistoryMovement> getAllMovements(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<HistoryMovement> paginaMovements =
                historyMovementRepository.findAll(PageRequest.of(pagina -1, elementosXpagina, Sort.by("id").ascending()));

        if (paginaMovements.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaMovements.getContent();
    }

    @Override
    public void agregarAsignatura(HistoryMovement historyMovement) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        Asignatura asignatura = asignaturaRepository.findById(historyMovement.getAsignaturaAfectada().getId()).orElseThrow(AsignaturaNotFound::new);
        Pensum pensum = pensumRepository.findById(historyMovement.getPensum().getId()).orElseThrow(PensumNotFoundException::new);


        if (pensum.getProgramaAcademico().getFechaInicioModificacion() == null ||
                pensum.getProgramaAcademico().getDuracionModificacion() == null) {
            throw new ModificationPeriodDisabled();
        }

        if (asignatura == null) {
            throw new AsignaturaNotFound();
        }

        if (pensum == null) {
            throw new PensumNotFoundException();
        }

        // Verificar si la asignatura ya esta relacionada con el pensum
        if (asignaturaPensumRepository.existsByAsignaturaAndPensum(asignatura, pensum)) {
            throw new AsignaturaAlreadyInPensum();
        }

        // Verificar si la asignatura ya está agregada en el historial y si cambiosAceptados es true o null
        if (historyMovementRepository.existsByAsignaturaAfectadaAndAsignaturaAgregadaTrueAndCambiosAceptadosNull(asignatura)) {
            throw new AsignaturaAlreadyForAdd();
        }

        historyMovement.setCorreoDocente(correoUsuarioAutenticado);
        historyMovement.setAsignaturaAgregada(true);
        historyMovement.setAsignaturaRemovida(false);
        historyMovement.setAsignaturaActualizada(false);
        historyMovement.setCambiosAceptados(null);
        historyMovement.setProgramaAcademico(pensum.getProgramaAcademico());
        historyMovement.setFechaMovimiento(LocalDateTime.now());

        Email email = new Email();
        email.setDestinatario(historyMovement.getCorreoDocente());
        email.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje = String.format(MESSAGE_ADDED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email.setMensaje(mensaje);
        emailService.sendMail(email);

        Email email2 = new Email();
        email2.setDestinatario(pensum.getProgramaAcademico().getDirector().getCorreo());
        email2.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje2 = String.format(MESSAGE_ADDED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email2.setMensaje(mensaje2);
        emailService.sendMail(email2);

        historyMovementRepository.save(historyMovement);
    }

    @Override
    public void removerAsignatura(HistoryMovement historyMovement) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        Asignatura asignatura = asignaturaRepository.findById(historyMovement.getAsignaturaAfectada().getId()).orElseThrow(AsignaturaNotFound::new);
        Pensum pensum = pensumRepository.findById(historyMovement.getPensum().getId()).orElseThrow(PensumNotFoundException::new);


        if (pensum.getProgramaAcademico().getFechaInicioModificacion() == null ||
                pensum.getProgramaAcademico().getDuracionModificacion() == null) {
            throw new ModificationPeriodDisabled();
        }

        if (asignatura == null) {
            throw new AsignaturaNotFound();
        }

        if (pensum == null) {
            throw new PensumNotFoundException();
        }

        // Verificar si la asignatura ya está removida en el historial
        if (historyMovementRepository.existsByAsignaturaAfectadaAndAsignaturaRemovidaTrueAndCambiosAceptadosNull(asignatura)) {
            throw new AsignaturaAlreadyRemoved();
        }

        // Setear los valores correspondientes al remover la asignatura
        historyMovement.setCorreoDocente(correoUsuarioAutenticado);
        historyMovement.setAsignaturaAgregada(false);
        historyMovement.setAsignaturaRemovida(true);
        historyMovement.setAsignaturaActualizada(false);
        historyMovement.setCambiosAceptados(null);
        historyMovement.setProgramaAcademico(pensum.getProgramaAcademico());
        historyMovement.setFechaMovimiento(LocalDateTime.now());

        Email email = new Email();
        email.setDestinatario(historyMovement.getCorreoDocente());
        email.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje = String.format(MESSAGE_REMOVED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email.setMensaje(mensaje);
        emailService.sendMail(email);

        Email email2 = new Email();
        email2.setDestinatario(pensum.getProgramaAcademico().getDirector().getCorreo());
        email2.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje2 = String.format(MESSAGE_REMOVED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email2.setMensaje(mensaje2);
        emailService.sendMail(email2);

        historyMovementRepository.save(historyMovement);
    }

    @Override
    public void actualizarAsignatura(HistoryMovement historyMovement) {
        Map<String, String> atributosModificados = historyMovement.getAtributosModificados();
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        Asignatura asignaturaAfectada = asignaturaRepository.findById(historyMovement.getAsignaturaAfectada().getId()).orElseThrow(AsignaturaNotFound::new);
        Pensum pensum = pensumRepository.findById(historyMovement.getPensum().getId()).orElseThrow(PensumNotFoundException::new);


        if (pensum.getProgramaAcademico().getFechaInicioModificacion() == null ||
                pensum.getProgramaAcademico().getDuracionModificacion() == null) {
            throw new ModificationPeriodDisabled();
        }

        if (atributosModificados == null) {
            throw new AtributosNotFound();
        }

        Asignatura asignatura = new Asignatura();
        // Actualizar los atributos de la asignatura con los valores proporcionados en los atributos modificados
        if (atributosModificados.containsKey("nombre")) {
            asignatura.setNombre(atributosModificados.get("nombre"));
        }
        if (atributosModificados.containsKey("codigo")) {
            asignatura.setCodigo(Integer.parseInt(atributosModificados.get("codigo")));
        }
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
            asignatura.setObjetivo(atributosModificados.get("objetivo"));
        }
        if (atributosModificados.containsKey("semestre")) {
            asignatura.setSemestre(atributosModificados.get("semestre"));
        }
        if (atributosModificados.containsKey("tipoCredito")) {
            asignatura.setTipoCredito(atributosModificados.get("tipoCredito"));
        }
        if (atributosModificados.containsKey("tipoCurso")) {
            asignatura.setTipoCurso(atributosModificados.get("tipoCurso"));
        }
        if (atributosModificados.containsKey("asignaturaSucesora")) {
            asignatura.setAsignaturaSucesora(atributosModificados.get("asignaturaSucesora"));
        }
        if (atributosModificados.containsKey("asignaturaPredecesora")) {
            asignatura.setAsignaturaPredecesora(atributosModificados.get("asignaturaPredecesora"));
        }


        historyMovement.setAsignaturaAfectada(asignaturaAfectada);
        historyMovement.setCorreoDocente(correoUsuarioAutenticado);
        historyMovement.setAsignaturaAgregada(false);
        historyMovement.setAsignaturaRemovida(false);
        historyMovement.setAsignaturaActualizada(true);
        historyMovement.setCambiosAceptados(null);
        historyMovement.setProgramaAcademico(pensum.getProgramaAcademico());
        historyMovement.setFechaMovimiento(LocalDateTime.now());


        Email email = new Email();
        email.setDestinatario(historyMovement.getCorreoDocente());
        email.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje = String.format(MESSAGE_EDITED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email.setMensaje(mensaje);
        emailService.sendMail(email);

        Email email2 = new Email();
        email2.setDestinatario(pensum.getProgramaAcademico().getDirector().getCorreo());
        email2.setAsunto(SUBJECT_EDITED_ASIGNATURA);
        String mensaje2 = String.format(MESSAGE_EDITED_ASIGNATURA, historyMovement.getCorreoDocente(), asignatura.getNombre());
        email2.setMensaje(mensaje2);
        emailService.sendMail(email2);

        historyMovementRepository.save(historyMovement);
    }

    @Override
    public void aprobarRechazarCambiosDespuesPeriodoModificacion(boolean aceptarCambios, Integer codigo, String reasonMessage) {
        LocalDate fechaActual = LocalDate.now();

        List<HistoryMovement> cambiosPropuestos = historyMovementRepository.findByCambiosAceptadosIsNullAndCodigo(codigo);

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

    @Override
    public void aplicarCambiosPropuestos(Integer codigo) {
        List<HistoryMovement> cambiosPropuestos = historyMovementRepository.findByCambiosAceptadosTrueAndCodigo(codigo);

        if (cambiosPropuestos.isEmpty()) {
            throw new CambiosAceptadosNotFoundException();
        }

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

        for (HistoryMovement cambio : cambiosRemoverAsignaturas) {
            Pensum pensum = pensumRepository.findById(cambio.getPensum().getId()).orElseThrow(PensumNotFoundException::new);
            Asignatura asignatura = asignaturaRepository.findById(cambio.getAsignaturaAfectada().getId()).orElseThrow(AsignaturaNotFound::new);

            pensumService.duplicatePensum(pensum.getId());

            Pensum nuevoPensum = pensumRepository.findTopByOrderByIdDesc();

            pensumService.removeAsignaturaFromPensum(nuevoPensum.getId(), asignatura.getId());

        }

        for (HistoryMovement cambio : cambiosActualizarAsignaturas) {
            Asignatura asignatura = asignaturaRepository.findById(cambio.getAsignaturaAfectada().getId()).orElseThrow(AsignaturaNotFound::new);

            asignaturaService.updateAsignatura(asignatura.getId(), asignatura);
        }
    }



    private String getCorreoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}