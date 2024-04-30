package com.micro.demo.service.impl;

import com.micro.demo.entities.AsignaturaPensum;
import com.micro.demo.entities.Pensum;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.repository.IAsignaturaPensumRepository;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.IPensumRepository;
import com.micro.demo.repository.IProgramaAcademicoRepository;
import com.micro.demo.service.IPdfService;
import com.micro.demo.service.exceptions.PdfDownloadNotAllowedException;
import com.micro.demo.service.exceptions.PensumNotFoundByIdException;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import jakarta.transaction.Transactional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class PdfService implements IPdfService {
    private final IPensumRepository pensumRepository;
    private final IAsignaturaRepository asignaturaRepository;
    private final IAsignaturaPensumRepository asignaturaPensumRepository;
    private final IProgramaAcademicoRepository programaAcademicoRepository;

    public PdfService(IPensumRepository pensumRepository, IAsignaturaRepository asignaturaRepository, IAsignaturaPensumRepository asignaturaPensumRepository, IProgramaAcademicoRepository programaAcademicoRepository) {
        this.pensumRepository = pensumRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaPensumRepository = asignaturaPensumRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
    }

    @Override
    public void generatePdf(Long pensumId) throws IOException {
        Pensum pensum = pensumRepository.findById(pensumId)
                .orElseThrow(() -> new PensumNotFoundByIdException(pensumId));

        ProgramaAcademico programaAcademico = pensum.getProgramaAcademico();

        if (!programaAcademico.getPuedeDescargarPdf()) {
            throw new PdfDownloadNotAllowedException();
        }

        String fileName = generateFileName(pensum);

        // Define la ubicación de la carpeta de descargas
        String downloadFolder = System.getProperty("user.home") + File.separator + "Downloads";

        // Verifica si el archivo ya existe y ajusta el nombre si es necesario
        File file = new File(downloadFolder, fileName);
        int count = 1;
        while (file.exists()) {
            fileName = generateFileNameWithIndex(pensum, count++);
            file = new File(downloadFolder, fileName);
        }


        try (PDDocument document = new PDDocument()) {
            PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
            int titleFontSize = 35;
            PDType1Font font = PDType1Font.HELVETICA;
            int fontSize = 12;

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(titleFont, titleFontSize);
                contentStream.beginText();

                float pageWidth = page.getMediaBox().getWidth();
                float textWidth = titleFont.getStringWidth(programaAcademico.getNombre()) / 1000 * titleFontSize;
                float titleX = (pageWidth - textWidth) / 2; // Centrar el título
                float yPosition = PDRectangle.A4.getHeight() - 50; // Empieza en la parte superior de la página
                contentStream.newLineAtOffset(titleX, yPosition);
                contentStream.showText(programaAcademico.getNombre());
                yPosition -= 30; // Avanza a la siguiente línea con más espacio

                contentStream.setFont(font, fontSize); // Cambiar al font regular

                contentStream.newLineAtOffset(0, -60);
                contentStream.showText("Director: " + programaAcademico.getDirector().getCorreo());
                yPosition -= 20;

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Pensum ID: " + pensum.getId());
                yPosition -= 20;

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Fecha de inicio: " + pensum.getFechaInicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                yPosition -= 20;

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Fecha final: " + pensum.getFechaFinal().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                yPosition -= 20;

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Créditos totales: " + pensum.getCreditosTotales());
                yPosition -= 20;

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Asignaturas:");
                yPosition -= 20;

                // Obtener los nombres de todas las asignaturas asociadas al pensum
                List<AsignaturaPensum> asignaturaPensums = asignaturaPensumRepository.findByPensumId(pensumId);
                for (AsignaturaPensum asignaturaPensum : asignaturaPensums) {
                    String asignaturaNombre = asignaturaPensum.getAsignatura().getNombre();
                    Integer codigoAsignatura = asignaturaPensum.getAsignatura().getCodigo();
                    String tipoCursoAsignatura = asignaturaPensum.getAsignatura().getTipoCurso();
                    String metodologiaAsignatura = asignaturaPensum.getAsignatura().getMetodologia();
                    String asignaturaPredecesora = asignaturaPensum.getAsignatura().getAsignaturaPredecesora();
                    String asignaturaSucesora = asignaturaPensum.getAsignatura().getAsignaturaSucesora();

                    contentStream.newLineAtOffset(0, -30);
                    contentStream.showText("- " + asignaturaNombre);
                    yPosition -= 30;

                    contentStream.newLineAtOffset(0, -20); // Ajuste para los detalles de la asignatura
                    contentStream.showText("    Código: " + codigoAsignatura);
                    yPosition -= 20;

                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("    Tipo de Curso: " + tipoCursoAsignatura);
                    yPosition -= 20;

                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("    Metodología: " + metodologiaAsignatura);
                    yPosition -= 20;

                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("    Asignatura Predecesora: " + asignaturaPredecesora);
                    yPosition -= 20;

                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText("    Asignatura Sucesora: " + asignaturaSucesora);
                    yPosition -= 20;
                }

                contentStream.endText();
            }

            // Concatena el nombre del archivo al directorio de descargas
            String filePath = downloadFolder + File.separator + fileName;

            // Guarda el documento en la ubicación especificada
            document.save(filePath);
        }
    }

    private String generateFileName(Pensum pensum) {
        String programaAcademicoNombre = pensum.getProgramaAcademico().getNombre();
        String pensumNumero = String.valueOf(pensum.getId());
        String fechaInicio = formatDate(pensum.getFechaInicio());
        return programaAcademicoNombre + "_Pensum" + pensumNumero + "_" + fechaInicio + ".pdf";
    }

    private String generateFileNameWithIndex(Pensum pensum, int index) {
        String programaAcademicoNombre = pensum.getProgramaAcademico().getNombre();
        String pensumNumero = String.valueOf(pensum.getId());
        String fechaInicio = formatDate(pensum.getFechaInicio());
        return programaAcademicoNombre + "_Pensum" + pensumNumero + "_" + fechaInicio + "_" + index + ".pdf";
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }
}
