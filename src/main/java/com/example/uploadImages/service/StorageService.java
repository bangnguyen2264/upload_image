package com.example.uploadImages.service;

import com.example.uploadImages.model.entity.FileData;
import com.example.uploadImages.model.entity.ImageData;
import com.example.uploadImages.repository.FileDataRepository;
import com.example.uploadImages.repository.StorageRepository;
import com.example.uploadImages.utils.ImageUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StorageService {

    private StorageRepository repository;

    private FileDataRepository fileDataRepository;

    private final String FOLDER_PATH="D:\\ImageFiles";

    public String uploadImage(MultipartFile file) throws IOException {
        ImageData imageData = repository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        if (imageData != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        }
        return null;
    }



    public byte[] downloadImage(String fileName) {
        Optional<ImageData> dbImageData = repository.findByName(fileName);
        if (dbImageData.isEmpty()) throw new RuntimeException(
                STR."\{fileName}not found"
        );
        return ImageUtils.decompressImage(dbImageData.get().getImageData());

    }


    public String uploadImageToFileSystem(MultipartFile file) throws IOException {
        String filePath=FOLDER_PATH+file.getOriginalFilename();

        FileData fileData=fileDataRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath).build());

        file.transferTo(new File(filePath));

        return STR."file uploaded successfully : \{filePath}";
    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<FileData> fileData = fileDataRepository.findByName(fileName);
        if (fileData.isEmpty()) throw new RuntimeException(
                STR."\{fileName}not found"
        );
        String filePath=fileData.get().getFilePath();
        return Files.readAllBytes(new File(filePath).toPath());
    }



}
