package com.movieflex.controller;

import com.movieflex.config.DataSourceConfig;
import com.movieflex.constants.MessageCodes;
import com.movieflex.dto.Response;
import com.movieflex.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@CrossOrigin("*")
@RestController
@RequestMapping("/file/")
public class FileController {

    private final FileService fileService;
    private final String path;

    public FileController(FileService fileService, DataSourceConfig config) {
        this.fileService = fileService;
        this.path = config.getDataSource().getPath();
    }

    @PostMapping("upload")
    public ResponseEntity<Response> uploadFileHandler(@RequestPart MultipartFile file) throws IOException {
        String uploadedFileName = fileService.uploadFile(path, file);
        Response response = Response.builder()
                .statusCode(MessageCodes.OK)
                .statusDescription("File uploaded successfully")
                .data(uploadedFileName)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("get/{fileName}")
    public void serveFileHandler(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        InputStream resourcefile = fileService.getResourceFile(path, fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourcefile, response.getOutputStream());
    }

}
