package org.cloudbreeze.fileservice.controllers;


import java.io.File;

import org.cloudbreeze.fileservice.services.FileDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/files")
public class FileServiceController {

    private final static Logger logger = LoggerFactory.getLogger(FileServiceController.class);

    @Autowired
    private FileDataService fileDataService;

    @ApiOperation( value = "Download file")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved file"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    @GetMapping()
    public ResponseEntity<Resource> getFile(@RequestParam(name = "fileName", required = true) String fileName) {

        logger.debug(String.format("download %s", fileName ));

        Resource resource = fileDataService.getFile(fileName);

        if(resource != null) {

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            return ResponseEntity.ok()
            .headers(header)
            .contentLength(fileDataService.contentLength(fileName))
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(resource);

        }

        return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
    }


    @ApiOperation( value = "Upload file")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Successfully created file"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    @PostMapping()
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file , @RequestParam(name = "fileName", required = true) String fileName) {

        logger.debug(String.format("upload %s", fileName ));

        fileDataService.uploadFile(file, fileName);

        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

}
