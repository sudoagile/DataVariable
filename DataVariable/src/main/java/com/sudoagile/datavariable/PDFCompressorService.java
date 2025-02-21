package com.sudoagile.datavariable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
@Service
@Component
public class PDFCompressorService {

    /**
     * Comprime un archivo PDF reduciendo la calidad de las imágenes.
     *
     * @param inputPath  Ruta del archivo PDF de entrada.
     * @param outputPath Ruta donde se guardará el archivo PDF comprimido.
     * @param dpi        Resolución en puntos por pulgada (DPI) para las imágenes.
     * @param quality    Factor de calidad para la compresión (valor entre 0 y 1).
     * @throws IOException Si hay un error en la lectura/escritura del archivo.
     */
    public void compressPDF(String inputPath, String outputPath, int dpi, float quality) throws IOException {
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);

        // Cargar el PDF original
        PDDocument document = PDDocument.load(inputFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        // Crear un nuevo documento PDF comprimido
        PDDocument compressedDocument = new PDDocument();

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            // Renderizar la página como imagen con la resolución deseada
            BufferedImage image = pdfRenderer.renderImageWithDPI(i, dpi);

            // Crear una nueva página en el PDF comprimido
            PDPage newPage = new PDPage(document.getPage(i).getMediaBox());
            compressedDocument.addPage(newPage);

            // Convertir la imagen a JPEG con la calidad especificada
            compressedDocument.getPage(i).getResources().add(JPEGFactory.createFromImage(compressedDocument, image, quality));
        }

        // Guardar el PDF comprimido
        compressedDocument.save(outputFile);
        compressedDocument.close();
        document.close();
    }
}
