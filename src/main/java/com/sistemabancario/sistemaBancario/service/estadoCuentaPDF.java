package com.sistemabancario.sistemaBancario.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;


@Service
public class estadoCuentaPDF {

    public void exportar(HttpServletResponse response, String nombre, String numero, @NotNull(message = "El saldo inicial es obligatorio") @PositiveOrZero(message = "El saldo no puede ser negativo ") @DecimalMin(value = "0.0", message = "El saldo no puede ser negativo") BigDecimal saldo) throws IOException {
        Document documento = new Document(PageSize.A4);
        PdfWriter pdfWriter = PdfWriter.getInstance(documento, response.getOutputStream());

        documento.open();

        try {
            Image logo = Image.getInstance("src/main/resources/static/logo.png");
            logo.scaleToFit(80, 80);
            logo.setAbsolutePosition(450f, 750f);
            documento.add(logo);
            //3. Marca de agua
            logo.setAbsolutePosition(200f, 400f);
            logo.scaleToFit(200, 200);
            documento.add(logo);

        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo, continuando sin imagen...");
        }
        //Fuentes
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fuenteTexto = FontFactory.getFont(FontFactory.HELVETICA, 12);

        //Contenido del PDF
        Paragraph titulo = new Paragraph("ESTADO DE CUENTA", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);

        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Titular de la cuenta: " + nombre, fuenteTexto));
        documento.add(new Paragraph("Numero de cuenta: " + numero, fuenteTexto));
        documento.add(new Paragraph("Saldo actual: $" + saldo, fuenteTexto));
        documento.add(new Paragraph("Estado: ACTIVA", fuenteTexto));
        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Este documento es un reporte oficial generado por el sistema central bancario.",  fuenteTexto));

        documento.close();
    }
}
