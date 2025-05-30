
package com.example.pdf_foleador.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public byte[] addFolios(MultipartFile file, int folios) throws IOException {
        PDDocument inputDoc = PDDocument.load(file.getInputStream());
        PDDocument outputDoc = new PDDocument();

        PDPage originalPage = inputDoc.getPage(inputDoc.getNumberOfPages() - 1);

        for (int i = 0; i < folios; i++) {
            PDPage importedPage = outputDoc.importPage(originalPage);

            try (PDPageContentStream contentStream = new PDPageContentStream(outputDoc, importedPage,
                    PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(400, 730);
                contentStream.showText(String.format("FOLIO: N°: %05d", i + 1));
                contentStream.endText();
            }
        }

        outputDoc.getDocumentCatalog().setVersion("1.5");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outputDoc.save(out);
        outputDoc.close();
        inputDoc.close();

        return out.toByteArray();
    }
}
