package org.ergemp.controller;

import org.ergemp.model.Document;
import org.ergemp.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DocumentController {

    @Autowired
    DocumentRepository documentRepo;

    @PostMapping("/api/document/pg")
    public ResponseEntity uploadDocumentPg(@RequestParam String text, @RequestPart("file") MultipartFile file) {
    //public ResponseEntity uploadDocumentPg(Document document) {

        ResponseEntity retVal = ResponseEntity.status(HttpStatus.OK).body("OK");

        Document document = new Document();

        if (file.isEmpty()) {
            retVal =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("file is empty");
            return retVal;
        }

        try {
            //byte[] bytes = document.getFile().getBytes();
            document.setText(text);
            document.setFile(file.getBytes());
            document.setFileName(file.getOriginalFilename());
            document.setContentType(file.getContentType());
            documentRepo.addDocumentPg(document);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            return retVal;
        }
    }

    @PostMapping("/api/document/minio")
    public ResponseEntity uploadDocumentMinio(@RequestParam String text, @RequestPart("file") MultipartFile file) {
        //public ResponseEntity uploadFile(Document document) {

        ResponseEntity retVal = ResponseEntity.status(HttpStatus.OK).body("OK");
        String path = "";

        Document document = new Document();

        if (file.isEmpty()) {
            retVal =  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("file is empty");
            return retVal;
        }

        try {
            //byte[] bytes = document.getFile().getBytes();
            document.setText(text);
            document.setFile(file.getBytes());
            document.setFileName(file.getOriginalFilename());
            document.setContentType(file.getContentType());
            path = documentRepo.addDocumentMinio(document);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        finally {
            return ResponseEntity.status(HttpStatus.OK).body(path);
        }
    }

    @GetMapping("/api/document/trino/{text}")
    public ResponseEntity getDocumentTrino(@PathVariable String text) {
        ResponseEntity retVal = ResponseEntity.status(HttpStatus.OK).body("OK");

        try {
            Document document = documentRepo.getDocumentByTextTrino(text);

            retVal =  ResponseEntity.ok().
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + document.getFileName() + "\"").
                    body(new ByteArrayResource(document.getFile()));
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            return retVal;
        }
    }


    @GetMapping("/api/document/minio/{text}")
    public ResponseEntity getDocumentMinio(@PathVariable String text) {
        ResponseEntity retVal = ResponseEntity.status(HttpStatus.OK).body("OK");

        try {
            Document document = documentRepo.getDocumentByTextMinio(text);

            if (document != null) {

                retVal = ResponseEntity.ok().
                        header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + document.getFileName() + "\"").
                        body(new ByteArrayResource(document.getFile()));
            }
            else {
                retVal = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid Path");
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            return retVal;
        }
    }
}
