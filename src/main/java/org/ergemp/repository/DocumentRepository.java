package org.ergemp.repository;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.ergemp.configuration.TrinoConfig;
import org.ergemp.mapper.DocumentMapper;
import org.ergemp.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DocumentRepository {

    @Autowired
    @Qualifier("jdbcTemplate1")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TrinoConfig trinoConfig;

    public void addDocumentPg(Document document) {
        jdbcTemplate.update("INSERT INTO document(\"text\", file, file_name, content_type) VALUES (?,?,?,?)",
                document.getText(), document.getFile(), document.getFileName(), document.getContentType());
    }

    public String addDocumentMinio(Document document) {

        String retVal = "";
        MinioClient minioClient;

        try {

            minioClient =
                    MinioClient.builder()
                            .endpoint(new URL("http://127.0.0.1:9000"))
                            //.endpoint("http://127.0.0.1:9000")
                            .credentials("minioadmin", "minioadmin")
                            .build();

            minioClient.putObject(
                    PutObjectArgs.builder().bucket("objectstorage").object(document.getFileName()).stream(
                                    new ByteArrayInputStream(document.getFile()), -1, 10485760)
                            .contentType(document.getContentType())
                            .build());

            retVal = "objectstorage-" + document.getFileName();

        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            return retVal;
        }
    }

    public Document getDocumentByTextTrino(String text) {

        Document document = new Document();

        try {
            PreparedStatement pStmt = trinoConfig.getConnection().prepareStatement("select text, file, file_name, content_type from minio.default.document where text = ?");

            pStmt.setString(1, text);
            ResultSet rs = pStmt.executeQuery();

            document = DocumentMapper.map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            return document;
        }
    }

    public Document getDocumentByTextMinio(String text) {

        Document document = new Document();
        MinioClient minioClient;

        String bucket = "";
        String fileName = "";

        if (text.split("-").length == 2) {
            String[] path = text.split("-");
            bucket = path[0];
            fileName = path[1];
        }
        else {
            return null;
        }

        try {
            minioClient =
                    MinioClient.builder()
                            .endpoint(new URL("http://127.0.0.1:9000"))
                            //.endpoint("http://127.0.0.1:9000")
                            .credentials("minioadmin", "minioadmin")
                            .build();

            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build());

            byte[] fdocument = stream.readAllBytes();

            document.setFile(fdocument);
            document.setFileName(fileName);

        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            return document;
        }
    }
}
