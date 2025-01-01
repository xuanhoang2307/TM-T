package huce.edu.web.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {
	
	private final Path rootLocation;
	
	public FileSystemStorageService () {
		this.rootLocation = Paths.get("src/main/resources/static/uploads");
	}
	@Override
	public String store(MultipartFile file) {
		 try {
	            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	            Path destinationFile = this.rootLocation.resolve(
	                    Paths.get(fileName))
	                    .normalize().toAbsolutePath();

	            try (InputStream inputStream = file.getInputStream()) {
	                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
	            }
	            return fileName; // Trả về tên file
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null; // Hoặc có thể ném exception
	        }
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		try {
			Files.createDirectories(rootLocation);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

}
