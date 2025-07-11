
package com.example.pdf_foleador.controller;

import com.example.pdf_foleador.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public ResponseEntity<InputStreamResource> uploadPdf(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("folios") int folios,
                                                         @RequestParam("coordX") int coordX,
                                                         @RequestParam("coordY") int coordY,
                                                         @RequestParam("startPage") int startPage,
                                                         @RequestParam("endPage") int endPage,
                                                         @RequestParam("size") String size,
                                                         @RequestParam("copiasPorFolio") int copiasPorFolio,
                                                         @RequestParam("division") int division,
                                                         @RequestParam("orientacion") String orientacion) throws IOException {

        byte[] modifiedPdf = pdfService.addFolios(file, folios, coordX, coordY, startPage, endPage, size, copiasPorFolio, division, orientacion);

        ByteArrayInputStream bis = new ByteArrayInputStream(modifiedPdf);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=foleado.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
