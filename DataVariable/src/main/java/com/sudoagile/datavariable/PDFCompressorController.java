package com.sudoagile.datavariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
public class PDFCompressorController {

	private final PDFCompressorService pdfCompressorService;

	@Autowired
	public PDFCompressorController(PDFCompressorService pdfCompressorService) {
		this.pdfCompressorService = pdfCompressorService;
	}

	@GetMapping("/compress-pdf")
	public String compressPDF() {
		String inputPath = "C:\\Users\\erdl1\\AppData\\Local\\Temp\\BITACORA.pdf";
		String outputPath = "C:\\Users\\erdl1\\AppData\\Local\\Temp\\BITACORA_COMPRESSED.pdf";

		try {
			pdfCompressorService.compressPDF(inputPath, outputPath, 150, 0.85f);
			return "PDF compression successful!";
		} catch (IOException e) {
			e.printStackTrace();
			return "Error during PDF compression: " + e.getMessage();
		}
	}

}
