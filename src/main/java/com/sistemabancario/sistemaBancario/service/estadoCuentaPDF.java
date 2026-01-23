package com.sistemabancario.sistemaBancario.service;

import com.lowagie.text.*;
import com.lowagie.text.Font; // Importante asegurar esta importación
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class estadoCuentaPDF {

    public void exportar(HttpServletResponse response, String nombre, String numero, BigDecimal saldo, String estado ) throws IOException {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());

        documento.open();

        // 1. Encabezado de Banco
        Font fuenteBanco = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.BOLD, new Color(0, 45, 90));
        Paragraph bancoNombre = new Paragraph("THE BANK", fuenteBanco);
        bancoNombre.setAlignment(Element.ALIGN_LEFT);
        documento.add(bancoNombre);

        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("ESTADO DE CUENTA OFICIAL", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
        documento.add(new Paragraph("Fecha de emisión: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        documento.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        // 2. Tabla de Información del Cliente
        PdfPTable tablaCliente = new PdfPTable(2);
        tablaCliente.setWidthPercentage(100);
        tablaCliente.setSpacingBefore(20);

        // AQUÍ ESTABA EL ERROR: Usamos Font.BOLD y Font.NORMAL (que ya son números)
        tablaCliente.addCell(crearCelda("NOMBRE DEL TITULAR:", Font.BOLD));
        tablaCliente.addCell(crearCelda(nombre.toUpperCase(), Font.NORMAL));
        tablaCliente.addCell(crearCelda("NÚMERO DE CUENTA:", Font.BOLD));
        tablaCliente.addCell(crearCelda(numero, Font.NORMAL));
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
        Font fuentePie = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
        Paragraph pie = new Paragraph("Este documento es un reporte generado automáticamente por los servicios centrales de THE BANK. No requiere firma para su validez legal como comprobante electrónico.", fuentePie);
        pie.setAlignment(Element.ALIGN_CENTER);
        documento.add(pie);

        documento.close();
    }

    private PdfPCell crearCelda(String texto, int estilo) {
        // Font.BOLD (1) y Font.NORMAL (0) ya son enteros, no necesitas parseInt
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA, 11, estilo);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(8);
        return celda;
    }
}
