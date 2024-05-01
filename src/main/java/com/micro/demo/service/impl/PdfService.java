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

import java.awt.*;
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

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Obtener los nombres de todas las asignaturas asociadas al pensum
                List<AsignaturaPensum> asignaturaPensums = asignaturaPensumRepository.findByPensumId(pensumId);


                // Definir el tamaño de la tabla y las celdas
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float cellHeight = 20f;
                float tableMargin = 5f;

                // Definir el número de columnas y su ancho
                float[] columnWidths = {50, 80, 30, 30, 30, 30, 60, 30, 30, 30, 80};
                float tableHeight = cellHeight * 2 + tableMargin;
                float yPosition = yStart;

                // Dibujar las líneas horizontales y verticales de la tabla
                contentStream.setLineWidth(0.5f);
                float xEnd = margin + tableWidth;
                float yEnd = yStart - tableHeight;
                float nextX = margin;

                // Dibujar las líneas verticales
                for (int i = 0; i < columnWidths.length; i++) {
                    contentStream.moveTo(nextX, yStart);
                    contentStream.lineTo(nextX, yEnd - cellHeight * (asignaturaPensums.size() -1)); // Ajustado para que termine antes de la última línea horizontal
                    contentStream.stroke();
                    nextX += columnWidths[i];
                }

                // Dibujar una línea horizontal encima de los títulos
                contentStream.moveTo(margin, yStart);
                contentStream.lineTo(xEnd, yStart);
                contentStream.stroke();

                // Ajustar yEnd para que llegue hasta la última línea horizontal
                yEnd -= (asignaturaPensums.size() + 1) * cellHeight;

                // Dibujar las líneas horizontales
                for (int i = 0; i < asignaturaPensums.size() + 1; i++) {
                    float y = yStart - (tableHeight / 2) - (i * cellHeight);
                    contentStream.moveTo(margin, y);
                    contentStream.lineTo(xEnd, y);
                    contentStream.stroke();
                }

                // Dibujar la última línea vertical proporcional al número de filas
                float lastVerticalLineYStart = yStart - (tableHeight / 2) - ((asignaturaPensums.size()) * cellHeight);
                contentStream.moveTo(xEnd, yStart);
                contentStream.lineTo(xEnd, lastVerticalLineYStart);
                contentStream.stroke();



                // Escribir los encabezados de las columnas
                float nextXHeader = margin;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8); // Fuente Helvetica Bold para los encabezados
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.setLeading(15f);
                yStart -= 15; // Ajustar la posición hacia abajo
                for (int i = 0; i < columnWidths.length; i++) {
                    float columnWidth = columnWidths[i];
                    float textX = nextXHeader + 5; // Alineación a la izquierda con un pequeño margen
                    if (i == 0) {
                        textX -= -2; // Ajuste adicional hacia la izquierda para el primer encabezado
                    } else if (i == 1) {
                        textX -= -18;
                    } else if (i == 6) {
                        textX -= -12;
                    } else {
                        textX = nextXHeader + (columnWidth / 2) - 5; // Alinear en el centro de la columna
                    }
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8); // Cambiar la fuente a Helvetica Bold
                    contentStream.newLineAtOffset(textX, yStart);
                    contentStream.showText(getHeaderTitle(i));
                    contentStream.endText();
                    nextXHeader += columnWidth;
                }

                // Escribir los datos de las asignaturas en la tabla
                for (AsignaturaPensum asignaturaPensum : asignaturaPensums) {
                    Integer codigoAsignatura = asignaturaPensum.getAsignatura().getCodigo();
                    String asignaturaNombre = asignaturaPensum.getAsignatura().getNombre();
                    String htiAsignatura = asignaturaPensum.getAsignatura().getHti();

                    // Escribir la fila de datos
                    float nextXData = margin;
                    float lineHeight = 18f; // Altura de línea para separar las filas
                    String[] rowData = {codigoAsignatura.toString(), asignaturaNombre, htiAsignatura};
                    for (int i = 0; i < rowData.length; i++) { // Iterar sobre el tamaño correcto de rowData
                        float columnWidth = columnWidths[i];
                        float textX = margin + 5; // Alineación a la izquierda
                        if (i == 0) {
                            textX = margin + (columnWidth / 2) - 20; // Alineación centrada para la primera columna
                        } else {
                            textX = nextXData + 5; // Alineación a la izquierda para las demás columnas
                        }
                        String rowDataText = rowData[i];
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA, 8); // Cambiar la fuente a Helvetica normal
                        contentStream.newLineAtOffset(textX, yStart - lineHeight);
                        contentStream.showText(rowDataText != null ? rowDataText : ""); // Manejo de valores nulos
                        contentStream.endText();
                        nextXData += columnWidth;
                    }
                    yStart -= cellHeight;
                }
            }

            document.save(fileName);
        }
    }
    private String getHeaderTitle(int index) {
        switch (index) {
            case 0:
                return "Código";
            case 1:
                return "Nombre";
            case 2:
                return "Ht";
            case 3:
                return "Hp";
            case 4:
                return "Hti";
            case 5:
                return "Cre";
            case 6:
                return "Prereq";
            case 7:
                return "Sim";
            case 8:
                return "Rc";
            case 9:
                return "Te";
            case 10:
                return "Equis";
            default:
                return "";
        }
    }

    private String generateFileName(Pensum pensum) {
        String programaAcademicoNombre = pensum.getProgramaAcademico().getNombre();
        String pensumNumero = String.valueOf(pensum.getId());
        String fechaInicio = formatDate(pensum.getFechaInicio());
        return programaAcademicoNombre + "_Pensum" + pensumNumero + "_" + fechaInicio + ".pdf";
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }
}
