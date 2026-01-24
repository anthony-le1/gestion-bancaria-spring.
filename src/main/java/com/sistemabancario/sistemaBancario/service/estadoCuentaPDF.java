package com.sistemabancario.sistemaBancario.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class estadoCuentaPDF {

    public void exportar(HttpServletResponse response, String nombre, String numero, BigDecimal saldo,
                         String estado, String Direccion, String correo) throws IOException {

        Document documento = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(documento, response.getOutputStream());

        // --- MARCA DE AGUA (AL FONDO) ---
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                Font fuenteMarca = new Font(Font.HELVETICA, 60, Font.BOLD, new Color(240, 240, 240));
                ColumnText.showTextAligned(writer.getDirectContentUnder(), Element.ALIGN_CENTER,
                        new Phrase("THE BANK - OFICIAL", fuenteMarca), 297, 421, 45);
            }
        });

        documento.open();

        // 1. Título del Banco (Sin Logo y sin tabla)
        Font fuenteBanco = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD, new Color(0, 45, 90));
        Paragraph titulo = new Paragraph("THE BANK", fuenteBanco);
        titulo.setAlignment(Element.ALIGN_LEFT);
        documento.add(titulo);

        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("ESTADO DE CUENTA OFICIAL", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
        documento.add(new Paragraph("Fecha de emisión: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        documento.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        // 2. Tabla de Información Detallada
        PdfPTable tablaCliente = new PdfPTable(2);
        tablaCliente.setWidthPercentage(100);
        tablaCliente.setSpacingBefore(15);

        tablaCliente.addCell(crearCelda("TITULAR:", Font.BOLD));
        tablaCliente.addCell(crearCelda(nombre.toUpperCase(), Font.NORMAL));

        tablaCliente.addCell(crearCelda("NÚMERO DE CUENTA:", Font.BOLD));
        tablaCliente.addCell(crearCelda(numero, Font.NORMAL));

        tablaCliente.addCell(crearCelda("CORREO ELECTRÓNICO:", Font.BOLD));
        tablaCliente.addCell(crearCelda(correo != null ? correo : "No registrado", Font.NORMAL));

        tablaCliente.addCell(crearCelda("ESTADO DE CUENTA:", Font.BOLD));
        tablaCliente.addCell(crearCelda(estado, Font.NORMAL));

        documento.add(tablaCliente);

        // 3. Cuadro de Resumen de Saldo
        PdfPTable tablaSaldo = new PdfPTable(1);
        tablaSaldo.setWidthPercentage(100);
        tablaSaldo.setSpacingBefore(30);

        Font fuenteSaldo = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD, Color.WHITE);
        PdfPCell celdaSaldo = new PdfPCell(new Phrase("SALDO TOTAL DISPONIBLE: $" + saldo, fuenteSaldo));
        celdaSaldo.setBackgroundColor(new Color(0, 45, 90));
        celdaSaldo.setPadding(20);
        celdaSaldo.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaSaldo.setBorder(Rectangle.NO_BORDER);
        tablaSaldo.addCell(celdaSaldo);

        documento.add(tablaSaldo);

        // 4. Pie de página legal
        documento.add(new Paragraph(" "));
        documento.add(new Paragraph(" "));
        Font fuentePie = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
        Paragraph pie = new Paragraph("Este documento es un reporte generado automáticamente por los servicios centrales de THE BANK. La información aquí presentada refleja el estado de sus activos a la fecha de emisión.", fuentePie);
        pie.setAlignment(Element.ALIGN_CENTER);
        documento.add(pie);

        documento.close();
    }

    private PdfPCell crearCelda(String texto, int estilo) {
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA, 10, estilo);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(5);
        return celda;
    }
}