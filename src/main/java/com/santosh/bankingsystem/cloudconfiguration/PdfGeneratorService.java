package com.santosh.bankingsystem.cloudconfiguration;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.santosh.bankingsystem.entity.User;
import com.santosh.bankingsystem.usertransaction.entity.Transaction;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] generateStatementPdf(List<Transaction> transactions, User user) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Bank Statement").setBold().setFontSize(18));
        document.add(new Paragraph("User: " + user.getUsername()));
        document.add(new Paragraph("Email: " + user.getEmail()));
        document.add(new Paragraph(" "));

        Table table = new Table(4);
        table.addHeaderCell("Date");
        table.addHeaderCell("Type");
        table.addHeaderCell("Amount");
        table.addHeaderCell("Description");

        for (Transaction tx : transactions) {
            table.addCell(tx.getTimestamp().toString());
            table.addCell(tx.getTransactionType());
            table.addCell(tx.getAmount().toString());
            table.addCell(tx.getDescription());
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }
}