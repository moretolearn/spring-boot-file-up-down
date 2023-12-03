package com.moretolearn.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.moretolearn.model.DocDetails;
import com.moretolearn.model.FileDir;
import com.moretolearn.repository.DocDetailsRepository;

@Service
public class DocDetailsService {

	@Value("${data.filepath}")
	private String filepath;

	@Autowired
	DocDetailsRepository docDetailsRepository;

	// DB
	public DocDetails uploadFileDB(MultipartFile file) throws IOException {
		DocDetails doc = null;
		if (!file.isEmpty()) {
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			DocDetails docDetails = new DocDetails(fileName, file.getContentType(), filepath, file.getBytes());
			doc = docDetailsRepository.save(docDetails);
		}
		return doc;
	}

	public DocDetails downloadFileDB(String docId) {
		DocDetails docDetails = docDetailsRepository.findById(docId).get();
		return docDetails;
	}

	// Filesystem -1
	public DocDetails uploadFileFS(MultipartFile file, String userId, String orgId, String docCatagory) {
		String filePathForDb = "";
		DocDetails docDetails = null;
		String stringfolderPath = genFolderPath(orgId, userId, docCatagory);
		Path folderPath = Paths.get(stringfolderPath);
		try {
			if (!folderExists(stringfolderPath)) {
				createFolders(stringfolderPath);
			}
			Files.copy(file.getInputStream(), folderPath.resolve(file.getOriginalFilename()));
			filePathForDb = stringfolderPath.concat(File.separator).concat(file.getOriginalFilename());
			System.out.println("Data :" + filePathForDb);
			DocDetails documentDetails = new DocDetails();
			documentDetails.setDocName(file.getOriginalFilename());
			documentDetails.setPath(filePathForDb);
			documentDetails.setDocType(file.getContentType());
			docDetails = docDetailsRepository.save(documentDetails);
		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
		return docDetails;

	}

	public String genFolderPath(String orgId, String userId, String docCatagory) {

		String path = filepath.concat(File.separator).concat(orgId).concat(File.separator).concat(userId)
				.concat(File.separator).concat(docCatagory);

		return path;
	}

	public boolean createFolders(String path) {
		return new File(path).mkdirs();
	}

	public boolean folderExists(String path) {
		File directory = new File(path);
		return directory.exists();
	}

	//Filesystem -2
	private Path fileStorageLocation = null;

	@Autowired
	public DocDetailsService(FileDir fileDir) {
		this.fileStorageLocation = Paths.get(fileDir.getUploadDir()).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public DocDetails uploadFileFS(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		DocDetails doc = null;
		try {
			if (fileName.contains("..")) {
			}
			DocDetails docDetails = new DocDetails(fileName, file.getContentType(), fileStorageLocation.getParent()+"\\"+fileStorageLocation.getFileName(), file.getBytes());
			doc = docDetailsRepository.save(docDetails);
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
		}
		return doc;
	}

	public Resource downloadFileFS(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
			}
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
