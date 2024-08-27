package com.example.rentallife.service;

import com.example.rentallife.repository.FileUploadRepository;
import com.example.rentallife.entity.FileUploadModel;
import com.example.rentallife.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class UploadService {

    @Autowired
    private UserService userService;
    @Autowired
    private FileUploadRepository fileUploadRepository;

    private final static String FILE_EXTENSION = ".pdf";

    public void encryptPDFFile(String path,
                               String fileName,
                               MultipartFile multipartFile,
                               String clientID,
                               User landlord,
                               User tenant) throws IOException {
        boolean isFilePDF = isFilePDF(fileName);
        if (isFilePDF) {
            String fullPath = path + "/" + clientID;
            // Create client directory
            createDirectory(fullPath);
            // Loading the pdf file
            InputStream inputStream = multipartFile.getInputStream();
            PDDocument pdDocument = PDDocument.load(inputStream);
            // Creating instance of AccessPermission
            AccessPermission ap = new AccessPermission();
            // Creating instance
            StandardProtectionPolicy stpp = new StandardProtectionPolicy("test", "test", ap);
            // Setting the length of Encryption key
            stpp.setEncryptionKeyLength(128);
            // Setting the permission
            stpp.setPermissions(ap);
            // Protecting the PDF file
            pdDocument.protect(stpp);
            // Saving and closing the PDF Document
            pdDocument.save(fullPath + "/" + fileName);
            pdDocument.close();

            // 将文件信息保存到数据库
            FileUploadModel fileUploadModel = new FileUploadModel();
            fileUploadModel.setFileName(fileName);
            fileUploadModel.setFilePath(fullPath + "/" + fileName);
            fileUploadModel.setLandlord(landlord);
            fileUploadModel.setTenant(tenant);
            fileUploadRepository.save(fileUploadModel);
        } else {
            throw new IOException("The file should have PDF format.");
        }
    }
    private static void createDirectory(String dirPath) {
        Path p = Paths.get(dirPath);
        if (!Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS)) {
            try { Files.createDirectory(p);
            } catch (IOException e) {
                log.error("Creating directory failed: {} ", e.getMessage());
            }
        }
    }
    private static boolean isFilePDF(String fileName) {
        return fileName != null && fileName.endsWith(FILE_EXTENSION);
    }
    public FileUploadModel getFileById(Long fileId) {
        return fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Not Fund: " + fileId));
    }
    public List<FileUploadModel> findFilesByTenantId(Long tenantId) {
        // 获取租户对象
        User tenant = userService.findUserById(tenantId);
        // 调用 repository 方法，根据租户查找文件
        return fileUploadRepository.findByTenant(tenant);
    }
}

