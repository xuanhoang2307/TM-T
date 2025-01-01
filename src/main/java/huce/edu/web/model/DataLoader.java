package huce.edu.web.model;

import org.springframework.boot.CommandLineRunner;

import huce.edu.web.repository.SizeRepository;

public class DataLoader implements CommandLineRunner {
	private final SizeRepository sizeRepository;

    public DataLoader(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Thêm các size cố định vào cơ sở dữ liệu
        if (sizeRepository.count() == 0) {
            sizeRepository.save(new Size("S"));
            sizeRepository.save(new Size("M"));
            sizeRepository.save(new Size("L"));
            sizeRepository.save(new Size("XL"));
            sizeRepository.save(new Size("2XL"));
        }
    }
}
