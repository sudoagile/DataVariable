package com.example.pdf_foleador.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public byte[] addFolios(MultipartFile file, int cantidad, int coordX, int coordY,
                            int inicioFolio, int finFolio, String size,
                            int copiasPorFolio, int divisionesPorHoja,
                            String orientacion) throws IOException {

        PDDocument originalDoc = PDDocument.load(file.getInputStream());
        PDDocument resultDoc = new PDDocument();

        int totalOriginalPages = originalDoc.getNumberOfPages();

        int folio = inicioFolio;
        int totalFoliar = Math.min(cantidad, finFolio - inicioFolio + 1);
        int totalDivisionesNecesarias = totalFoliar * copiasPorFolio;
        int totalPaginas = (int) Math.ceil((double) totalDivisionesNecesarias / divisionesPorHoja);

        for (int i = 0; i < totalPaginas; i++) {
            PDPage originalPage = originalDoc.getPage(i % totalOriginalPages);
            PDPage copiedPage = resultDoc.importPage(originalPage);
            PDRectangle mediaBox = copiedPage.getMediaBox();
            float pageHeight = mediaBox.getHeight();
            float pageWidth = mediaBox.getWidth();

            // Canvas base de previsualización
            float canvasWidth = 600f;
            float canvasHeight = 800f;

            // Normalizar coordenadas a 0-1
            float normX = coordX / canvasWidth;
            float normY = coordY / canvasHeight;

            int filas = (divisionesPorHoja == 4) ? 2 : divisionesPorHoja;
            int columnas = (divisionesPorHoja == 4) ? 2 : 1;

            float divWidth = pageWidth / columnas;
            float divHeight = pageHeight / filas;

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    resultDoc, copiedPage, PDPageContentStream.AppendMode.APPEND, true, true)) {

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                for (int d = 0; d < divisionesPorHoja; d++) {
                    if ((i * divisionesPorHoja + d) >= totalDivisionesNecesarias) break;

                    int row = d / columnas;
                    int col = d % columnas;

                    float offsetX = col * divWidth;
                    float offsetY = pageHeight - ((row + 1) * divHeight);

                    // Ajuste final: normalizado y con Y invertido localmente
                    float posX = offsetX + normX * divWidth;
                    float posY = offsetY + (divHeight - normY * divHeight);

                    contentStream.beginText();

                    if ("vertical".equalsIgnoreCase(orientacion)) {
                        contentStream.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(90), posX, posY));
                    } else {
                        contentStream.newLineAtOffset(posX, posY);
                    }

                    contentStream.showText(String.format("Folio: %06d", folio));
                    contentStream.endText();

                    if (((i * divisionesPorHoja + d + 1) % copiasPorFolio) == 0) {
                        folio++;
                    }
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        resultDoc.save(out);
        originalDoc.close();
        resultDoc.close();
        return out.toByteArray();
    }
}
