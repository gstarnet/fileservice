package org.cloudbreeze.fileservice.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileDataService {

    private final static Logger logger = LoggerFactory.getLogger(FileDataService.class);

    public void uploadFile(MultipartFile file, String fileName) {

        File targetFile = new File(fileName);

        if (targetFile.exists()) {
            targetFile.delete();
        }

        try {
            InputStream initialStream = file.getInputStream();
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);

            try (OutputStream outStream = new FileOutputStream(targetFile)) {
                outStream.write(buffer);
            }
            logger.debug(String.format("File %s saved.",fileName));
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public Resource getFile(String fileName) {

        File targetFile = new File(fileName);
        if (targetFile.exists()) {

            Path path = Paths.get(targetFile.getAbsolutePath());
            try {
                Resource resource = new ByteArrayResource(Files.readAllBytes(path));

                logger.debug(String.format("File %s retrieved.",fileName));

                return resource;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    public long contentLength(String fileName) {
        File targetFile = new File(fileName);
        if (targetFile.exists()) {
            return targetFile.length();
        }
        return 0;
    }





}
