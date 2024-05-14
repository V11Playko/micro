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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PdfService implements IPdfService {
    private final IPensumRepository pensumRepository;
    private final IAsignaturaPensumRepository asignaturaPensumRepository;

    public PdfService(IPensumRepository pensumRepository, IAsignaturaPensumRepository asignaturaPensumRepository) {
        this.pensumRepository = pensumRepository;
        this.asignaturaPensumRepository = asignaturaPensumRepository;
    }

    /**
     * Generar un PDF
     *
     * @param pensumId - Identificador unico del pensum sobre el que se hara el PDF.
     * @throws PensumNotFoundByIdException - Se lanza si no se encuentra el pensum por su identificador unico.
     * @throws PdfDownloadNotAllowedException - Se lanza si el programa academico al que esta asociado el pensum no permite la descarga de PDF por ahora.
     * */
    @Override
    public void generatePdf(Long pensumId) throws IOException {
        // Obtener el pensum por su ID
        Pensum pensum = pensumRepository.findById(pensumId)
                .orElseThrow(() -> new PensumNotFoundByIdException(pensumId));

        // Obtener el programa académico asociado al pensum
        ProgramaAcademico programaAcademico = pensum.getProgramaAcademico();

        // Verificar si el programa académico permite descargar PDFs
        if (!programaAcademico.getPuedeDescargarPdf()) {
            throw new PdfDownloadNotAllowedException();
        }

        // Generar el nombre del archivo PDF
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

        // Url de la imagen
        String imageUrl = "https://drive.google.com/uc?export=download&id=1Uuy59Dv8rFOyK2uZ-WD9aetSrHe-4P7Q";


        // Iniciar la creación del documento PDF
        try (PDDocument document = new PDDocument()) {
            // Obtener la lista de asignaturas asociadas al pensum
            List<AsignaturaPensum> asignaturaPensums = asignaturaPensumRepository.findByPensumId(pensumId);

            // Definir márgenes y otras configuraciones visuales
            float margin = 50;
            float marginTop = 50;
            float tableMargin = 5f;

            // Abrir conexión y obtener la entrada de la URL
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            // Convertir el InputStream en un array de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] imageData = baos.toByteArray();

            // Cargar la imagen desde el array de bytes
            PDImageXObject image = PDImageXObject.createFromByteArray(document, imageData, "image");


            // Iterar sobre las asignaturas en grupos de máximo 15 por página
            for (List<AsignaturaPensum> pageAsignaturas : partitionList(asignaturaPensums, 15)) {
                // Crear una nueva página en el PDF
                PDPage page = new PDPage();
                document.addPage(page);

                // Iniciar el flujo de contenido para esta página
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Configurar la posición inicial y dimensiones del logo
                    float yStart = page.getMediaBox().getHeight() - margin - marginTop;
                    float imageX = 50;
                    float imageY = page.getMediaBox().getHeight() - 60;
                    float imageWidth = 100;
                    float imageHeight = 50;

                    // Dibujar el logo en la página
                    contentStream.drawImage(image, imageX, imageY, imageWidth, imageHeight);

                    // Escribir el nombre de la universidad
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(imageX + imageWidth + margin - 40, imageY + imageHeight - 22);
                    contentStream.showText("Universidad Francisco de Paula Santander");
                    contentStream.endText();

                    // Escribir el subtítulo "Pensum"
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.newLineAtOffset(imageX + imageWidth + margin - 40, imageY + imageHeight - 33);
                    contentStream.showText("Pensum");
                    contentStream.endText();

                    // Escribir la fecha de generación del PDF
                    LocalDate localDate = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedLocalDate = localDate.format(formatter);
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.newLineAtOffset(imageX + imageWidth + margin + 260, imageY + imageHeight - 70);
                    contentStream.showText("Generado: " + LocalDate.now().format(DateTimeFormatter.ofPattern(formattedLocalDate)));
                    contentStream.endText();

                    // Dibujar una línea horizontal debajo del logo
                    float horizontalLineY = imageY - imageHeight + 40;
                    float horizontalLineX1 = imageX - 5;
                    float horizontalLineX2 = imageX + imageWidth + margin + 365;
                    contentStream.moveTo(horizontalLineX1, horizontalLineY);
                    contentStream.lineTo(horizontalLineX2, horizontalLineY);
                    contentStream.stroke();

                    // Definir anchos de columna y calcular dimensiones de la tabla
                    float[] columnWidths = {50, 80, 30, 30, 30, 30, 60, 30, 30, 30, 80};
                    float cellHeight = 40f;
                    float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                    float xEnd = margin + tableWidth;
                    float yEnd = yStart - (cellHeight * 2 + tableMargin);
                    float nextX = margin;

                    // Dibujar las líneas verticales de la tabla
                    for (int i = 0; i < columnWidths.length; i++) {
                        contentStream.moveTo(nextX, yStart);
                        contentStream.lineTo(nextX, yEnd - 39.5f * (pageAsignaturas.size() - 1));
                        contentStream.stroke();
                        nextX += columnWidths[i];
                    }

                    // Dibujar una línea horizontal encima de los títulos de columna
                    contentStream.moveTo(margin, yStart);
                    contentStream.lineTo(xEnd, yStart);
                    contentStream.stroke();

                    // Dibujar las líneas horizontales de la tabla
                    for (int i = 0; i < pageAsignaturas.size() + 1; i++) {
                        float y = yStart - (cellHeight) - (i * cellHeight);
                        contentStream.moveTo(margin, y);
                        contentStream.lineTo(xEnd, y);
                        contentStream.stroke();
                    }

                    // Dibujar la última línea vertical proporcional al número de filas
                    float lastVerticalLineYStart = yStart - (cellHeight / 2 + tableMargin + 16) - (pageAsignaturas.size() * cellHeight);
                    contentStream.moveTo(xEnd, yStart);
                    contentStream.lineTo(xEnd, lastVerticalLineYStart);
                    contentStream.stroke();

                    // Escribir los encabezados de las columnas
                    float nextXHeader = margin;
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.setLeading(15f);
                    yStart -= 25;

                    for (int i = 0; i < columnWidths.length; i++) {
                        float columnWidth = columnWidths[i];
                        float textX = nextXHeader + 5;
                        if (i == 0) {
                            textX -= -2;
                        } else if (i == 1) {
                            textX -= -18;
                        } else if (i == 5) {
                            textX -= -1;
                        } else if (i == 6) {
                            textX -= -12;
                        } else if (i == 7) {
                            textX -= 0;
                        } else {
                            textX = nextXHeader + (columnWidth / 2) - 5;
                        }
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
                        contentStream.newLineAtOffset(textX, yStart);
                        contentStream.showText(getHeaderTitle(i));
                        contentStream.endText();
                        nextXHeader += columnWidth;
                    }

                    // Escribir los datos de las asignaturas en la tabla
                    for (AsignaturaPensum asignaturaPensum : pageAsignaturas) {
                        Integer codigoAsignatura = asignaturaPensum.getAsignatura().getCodigo();
                        String asignaturaNombre = asignaturaPensum.getAsignatura().getNombre();
                        String htiAsignatura = asignaturaPensum.getAsignatura().getHti();

                        float nextXData = margin;
                        float lineHeight = 18f;
                        String[] rowData = {codigoAsignatura.toString(), asignaturaNombre, htiAsignatura};
                        float maxCellHeight = Math.max(cellHeight, lineHeight);

                        for (int i = 0; i < rowData.length; i++) {
                            float columnWidth = columnWidths[i];
                            float textX = margin + 5;
                            if (i == 0) {
                                textX = margin + (columnWidth / 2) - 20;
                            } else {
                                textX = nextXData + 5;
                            }
                            String rowDataText = rowData[i];
                            List<String> lines = splitTextToFitWidth(rowDataText, contentStream, columnWidth - 10);
                            float cellYStart = yStart;
                            for (String line : lines) {
                                if (cellYStart - maxCellHeight < 0) {
                                    // Agregar una nueva página si no hay suficiente espacio en la actual
                                    document.addPage(new PDPage());
                                    yStart = page.getMediaBox().getHeight() - margin - marginTop;
                                    cellYStart = yStart;
                                    contentStream.close();
                                    page = new PDPage();
                                    document.addPage(page);
                                }
                                contentStream.beginText();
                                contentStream.setFont(PDType1Font.HELVETICA, 9);
                                contentStream.newLineAtOffset(textX, cellYStart - lineHeight - 10);
                                contentStream.showText(line);
                                contentStream.endText();
                                cellYStart -= lineHeight;
                            }
                            nextXData += columnWidth;
                        }
                        yStart -= maxCellHeight; // Espacio entre filas
                    }
                }
            }
            // Concatena el nombre del archivo al directorio de descargas
            String filePath = downloadFolder + File.separator + fileName;

            // Guarda el documento en la ubicación especificada
            document.save(filePath);
        }
    }

    public static <T> List<List<T>> partitionList(List<T> list, int partitionSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
        }
        return partitions;
    }

    /**
     * Titulos que estan en el encabezado
     * */
    private String getHeaderTitle(int index) {
        switch (index) {
            case 0:
                return "Código";
            case 1:
                return "Nombre";
            case 2:
                return "Hti";
            case 3:
                return "Hp";
            case 4:
                return "Ht";
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
