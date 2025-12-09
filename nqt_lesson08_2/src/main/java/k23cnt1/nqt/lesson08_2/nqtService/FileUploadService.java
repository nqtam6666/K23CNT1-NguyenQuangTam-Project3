package k23cnt1.nqt.lesson08_2.nqtService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // Lấy đường dẫn hiện tại của project
        String userDir = System.getProperty("user.dir");
        
        // Lưu vào src/main/resources/static (để persist)
        Path sourcePath = Paths.get(userDir, "src/main/resources/static", uploadDir);
        if (!Files.exists(sourcePath)) {
            Files.createDirectories(sourcePath);
        }
        
        // Lưu vào target/classes/static (để serve khi chạy)
        Path targetPath = Paths.get(userDir, "target/classes/static", uploadDir);
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }
        
        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        
        // Lưu file vào cả 2 thư mục
        Path sourceFilePath = sourcePath.resolve(filename);
        Path targetFilePath = targetPath.resolve(filename);
        
        // Lưu vào source trước
        Files.copy(file.getInputStream(), sourceFilePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Copy từ source sang target (đọc lại từ file đã lưu)
        if (Files.exists(sourceFilePath)) {
            try {
                Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // Nếu không copy được vào target, vẫn trả về URL (file đã lưu trong source)
                System.err.println("Warning: Could not copy to target directory: " + e.getMessage());
            }
        }
        
        // Trả về đường dẫn URL
        return "/" + uploadDir + "/" + filename;
    }
    
    public void deleteFile(String filePath) {
        if (filePath != null && filePath.startsWith("/")) {
            try {
                String userDir = System.getProperty("user.dir");
                Path sourcePath = Paths.get(userDir, "src/main/resources/static" + filePath);
                Path targetPath = Paths.get(userDir, "target/classes/static" + filePath);
                
                if (Files.exists(sourcePath)) {
                    Files.delete(sourcePath);
                }
                if (Files.exists(targetPath)) {
                    Files.delete(targetPath);
                }
            } catch (IOException e) {
                // Log error nhưng không throw exception
                System.err.println("Error deleting file: " + filePath + " - " + e.getMessage());
            }
        }
    }
}

