package rentals.jwt_auth.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    private static final String UPLOAD_DIR = "uploads";

    public String savePicture(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        Path uploadRoot = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadRoot)) {
            Files.createDirectories(uploadRoot);
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = uploadRoot.resolve(filename);
        Files.copy(file.getInputStream(), target);
        return "/" + UPLOAD_DIR + "/" + filename;
    }
}
