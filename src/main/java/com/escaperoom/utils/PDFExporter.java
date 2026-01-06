package com.escaperoom.utils;

import com.escaperoom.interfaces.Exportable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class PDFExporter implements Exportable {
    
    private String reportTitle;
    private String reportContent;
    private List<String[]> tableData;
    private String[] tableHeaders;
    
    public PDFExporter(String reportTitle, String reportContent) {
        this.reportTitle = reportTitle;
        this.reportContent = reportContent;
    }
    
    public PDFExporter(String reportTitle, String[] tableHeaders, List<String[]> tableData) {
        this.reportTitle = reportTitle;
        this.tableHeaders = tableHeaders;
        this.tableData = tableData;
    }
    
    @Override
    public void exportToPDF(String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;
            float fontSize = 12;
            float leading = 1.5f * fontSize;
            
            // Title
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText(reportTitle);
            contentStream.endText();
            
            yPosition -= 30;
            
            // Date
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(margin, yPosition);
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            contentStream.showText("Generated: " + dateStr);
            contentStream.endText();
            
            yPosition -= 30;
            
            // Content
            if (reportContent != null && !reportContent.isEmpty()) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                
                String[] lines = reportContent.split("\n");
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leading);
                    yPosition -= leading;
                    
                    if (yPosition < margin) {
                        contentStream.endText();
                        contentStream.close();
                        
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = page.getMediaBox().getHeight() - margin;
                        
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
                        contentStream.newLineAtOffset(margin, yPosition);
                    }
                }
                contentStream.endText();
            }
            
            // Table data
            if (tableHeaders != null && tableData != null) {
                yPosition -= 20;
                
                // Headers
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.join(" | ", tableHeaders));
                contentStream.endText();
                
                yPosition -= 15;
                
                // Draw line
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
                contentStream.stroke();
                
                yPosition -= 15;
                
                // Data rows
                contentStream.setFont(PDType1Font.HELVETICA, 9);
                for (String[] row : tableData) {
                    if (yPosition < margin + 50) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }
                    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(String.join(" | ", row));
                    contentStream.endText();
                    yPosition -= 12;
                }
            }
            
            // Footer - page number
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.newLineAtOffset(margin, margin - 30);
            contentStream.showText("Page " + document.getNumberOfPages());
            contentStream.endText();
            
            contentStream.close();
            document.save(filePath);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to export PDF: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append(reportTitle).append("\n");
        report.append("=".repeat(reportTitle.length())).append("\n\n");
        
        if (reportContent != null) {
            report.append(reportContent).append("\n");
        }
        
        if (tableHeaders != null && tableData != null) {
            report.append("\n");
            report.append(String.join(" | ", tableHeaders)).append("\n");
            report.append("-".repeat(tableHeaders.length * 15)).append("\n");
            
            for (String[] row : tableData) {
                report.append(String.join(" | ", row)).append("\n");
            }
        }
        
        return report.toString();
    }
}
