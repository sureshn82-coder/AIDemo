package com.example.demo;

import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class PdfFileReader {
    private final VectorStore vectorStore;

    @Value("classpath:leavepolicy.pdf")
    private Resource pdfResource;

    public PdfFileReader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    @GetMapping("/content/print")
    public void init() {

        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter.Builder()
                                .build())
                .build();

        var pdfReader = new PagePdfDocumentReader(pdfResource, config);
        var textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pdfReader.get()));
        vectorStore.similaritySearch("sick leave");
        //System.out.println("vectorStore"+ vectorStore.similaritySearch("sick leave"));

    }
}