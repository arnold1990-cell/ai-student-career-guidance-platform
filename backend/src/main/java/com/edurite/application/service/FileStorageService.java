package com.edurite.application.service;

import com.edurite.application.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root; private final long maxBytes;
    private final Set<String> allowed = Set.of("application/pdf","image/png","image/jpeg");
    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir, @Value("${app.max-upload-bytes:5242880}") long maxBytes) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize(); Files.createDirectories(root); this.maxBytes=maxBytes;
    }
    public String store(MultipartFile file){
        if (!allowed.contains(file.getContentType())) throw new AppException(400, "Invalid mime type");
        if (file.getSize()>maxBytes) throw new AppException(400, "File too large");
        String safe = UUID.randomUUID()+"-"+Path.of(file.getOriginalFilename()==null?"file":file.getOriginalFilename()).getFileName();
        Path target = root.resolve(safe).normalize();
        if (!target.startsWith(root)) throw new AppException(400, "Invalid path");
        try { Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING); return target.toString(); }
        catch (IOException e){ throw new AppException(500,"Failed to store file"); }
    }
}
