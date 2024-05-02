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
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import jakarta.transaction.Transactional;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                float marginTop = 50;
                float yStart = page.getMediaBox().getHeight() - margin - marginTop;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float cellHeight = 32f;
                float tableMargin = 5f;

                // Crear un objeto PDImageXObject desde el archivo de imagen
                PDImageXObject image = PDImageXObject.createFromFile("C:\\Users\\heinn\\Documents\\personalProjects\\micro\\docs\\images\\logo.png", document);

                // Especificar la posición y el tamaño de la imagen
                float imageX = 50;
                float imageY = page.getMediaBox().getHeight() - 60; // Posición vertical de la imagen
                float imageWidth = 100; // Ancho de la imagen
                float imageHeight = 50; // Alto de la imagen

                // Dibujar la imagen en la página
                contentStream.drawImage(image, imageX, imageY, imageWidth, imageHeight);

                // Escribir el título "Universidad Francisco de Paula Santander"
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(imageX + imageWidth + margin - 40, imageY + imageHeight - 22); // Alinea el texto según necesites
                contentStream.showText("Universidad Francisco de Paula Santander");
                contentStream.endText(); // Finaliza la secuencia de texto

                // Escribir el subtítulo "Pensum" debajo del título
                contentStream.beginText(); // Iniciar una nueva secuencia de texto
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(imageX + imageWidth + margin - 40, imageY + imageHeight -33); // Espacio entre el título y el subtítulo
                contentStream.showText("Pensum");
                contentStream.endText(); // Finalizar la secuencia de texto

                LocalDate localDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedLocalDate = localDate.format(formatter);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(imageX + imageWidth + margin + 260, imageY + imageHeight -70);
                contentStream.showText("Generado: "+ LocalDate.now().format(DateTimeFormatter.ofPattern(formattedLocalDate)));
                contentStream.endText();

                // Dibujar una línea horizontal debajo de la imagen
                float horizontalLineY = imageY - imageHeight + 40; // Posición vertical de la línea
                float horizontalLineX1 = imageX - 5; // Margen a la izquierda
                float horizontalLineX2 = imageX + imageWidth + margin + 365; // Margen a la derecha
                contentStream.moveTo(horizontalLineX1, horizontalLineY); // Mover a la posición inicial de la línea
                contentStream.lineTo(horizontalLineX2, horizontalLineY); // Dibujar la línea horizontal
                contentStream.stroke();

                // CUADRO CUADRICULADO - Definir el número de columnas y su ancho
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
                    } else if (i == 5) {
                        textX -= -1;
                    } else if (i == 6) {
                        textX -= -12;
                    } else if (i == 7) {
                        textX -= 0;
                    } else {
                        textX = nextXHeader + (columnWidth / 2) - 5; // Alinear en el centro de la columna
                    }
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11); // Cambiar la fuente a Helvetica Bold
                    contentStream.newLineAtOffset(textX, yStart);
                    contentStream.showText(getHeaderTitle(i));
                    contentStream.endText();
                    nextXHeader += columnWidth;
                }

                for (AsignaturaPensum asignaturaPensum : asignaturaPensums) {
                    Integer codigoAsignatura = asignaturaPensum.getAsignatura().getCodigo();
                    String asignaturaNombre = asignaturaPensum.getAsignatura().getNombre();
                    String htiAsignatura = asignaturaPensum.getAsignatura().getHti();

// Escribir la fila de datos
                    float nextXData = margin;
                    float lineHeight = 18f; // Altura de línea para separar las filas
                    String[] rowData = {codigoAsignatura.toString(), asignaturaNombre, htiAsignatura};
                    float maxCellHeight = Math.max(cellHeight, lineHeight); // Ajuste para la altura máxima de la celda
                    for (int i = 0; i < rowData.length; i++) { // Iterar sobre el tamaño correcto de rowData
                        float columnWidth = columnWidths[i];
                        float textX = margin + 5; // Alineación a la izquierda
                        if (i == 0) {
                            textX = margin + (columnWidth / 2) - 20; // Alineación centrada para la primera columna
                        } else {
                            textX = nextXData + 5; // Alineación a la izquierda para las demás columnas
                        }
                        String rowDataText = rowData[i];
                        // Separar el texto en líneas para ajustarlo dentro del ancho de la celda
                        List<String> lines = splitTextToFitWidth(rowDataText, contentStream, columnWidth - 10); // 10 es un margen
                        float cellYStart = yStart; // Guardar la posición inicial de la celda
                        for (String line : lines) {
                            if (cellYStart - maxCellHeight < 0) { // Comprobar si hay espacio suficiente en la página
                                // Agregar nueva página si no hay suficiente espacio
                                document.addPage(new PDPage());
                                // Reiniciar las coordenadas para comenzar desde la parte superior de la nueva página
                                yStart = page.getMediaBox().getHeight() - margin - marginTop;
                                cellYStart = yStart;
                                contentStream.close(); // Cerrar el contentStream actual
                                page = new PDPage(); // Crear una nueva página
                                document.addPage(page); // Agregar la nueva página al documento
                            }
                            contentStream.beginText();
                            contentStream.setFont(PDType1Font.HELVETICA, 9); // Establecer la fuente para el texto de la información
                            contentStream.newLineAtOffset(textX, cellYStart - lineHeight - 10); // Escribir la línea en la posición actual
                            contentStream.showText(line); // Mostrar la línea de texto
                            contentStream.endText();
                            cellYStart -= lineHeight; // Mover a la siguiente línea dentro de la celda
                        }
                        nextXData += columnWidth;
                    }
                    yStart -= maxCellHeight;  // Espacio entre filas
                }
            }
            document.save(fileName);
        }
    }

    public static <T> List<List<T>> partitionList(List<T> list, int partitionSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
        }
        return partitions;
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

    private List<String> splitTextToFitWidth(String text, PDPageContentStream contentStream, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        int lastSpace = -1;
        while (text.length() > 0) {
            int spaceIndex = text.indexOf(' ', lastSpace + 1);
            if (spaceIndex < 0) {
                spaceIndex = text.length();
            }
            String subString = text.substring(0, spaceIndex);
            float size = subString.length() * 4.5f; // Ajusta el tamaño según la fuente
            if (size > maxWidth) {
                if (lastSpace < 0) {
                    lastSpace = spaceIndex;
                }
                subString = text.substring(0, lastSpace);
                lines.add(subString);
                text = text.substring(lastSpace).trim();
                lastSpace = -1;
            } else if (spaceIndex == text.length()) {
                lines.add(text);
                text = "";
            } else {
                lastSpace = spaceIndex;
            }
        }
        return lines;
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
