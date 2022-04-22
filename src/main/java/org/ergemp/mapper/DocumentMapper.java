package org.ergemp.mapper;

import org.ergemp.model.Document;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentMapper {
    public static Document map(ResultSet rs) {

        Document document = new Document();

        try {
            while (rs.next()) {
                document.setFile(rs.getBytes("file"));
                document.setText(rs.getString("text"));
                document.setContentType(rs.getString("content_type"));
                document.setFileName(rs.getString("file_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return document;
        }
    }
}
