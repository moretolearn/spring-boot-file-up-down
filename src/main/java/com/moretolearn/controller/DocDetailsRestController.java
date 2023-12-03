package com.moretolearn.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.moretolearn.model.DocDetails;
import com.moretolearn.model.DocResponse;
import com.moretolearn.service.DocDetailsService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
public class DocDetailsRestController {

	@Autowired
	DocDetailsService docDetailsService;

	// DB
	@PostMapping("/uploadFileDB")
	public DocResponse uploadFileDB(@RequestParam("file") MultipartFile file) throws IOException {
		DocDetails docDetails = null;
		if (file.isEmpty()) {
		} else {
			docDetails = docDetailsService.uploadFileDB(file);
		}
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFileDB/")
				.path(docDetails.getDocId()).toUriString();
		return new DocResponse(file.getOriginalFilename(), fileDownloadUri, file.getContentType(), file.getSize());
	}

	@GetMapping("/downloadFileDB/{docId}")
	public ResponseEntity<byte[]> downloadFileDB(@PathVariable String docId) {
		DocDetails docDetails = docDetailsService.downloadFileDB(docId);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(docDetails.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + docDetails.getDocName() + "\"")
				.body(docDetails.getDocContent());
	}

	@PostMapping("/uploadMultipleFilesDB")
	public List<DocResponse> uploadMultipleFilesDB(@RequestParam("files") MultipartFile[] files) {
		return Arrays.asList(files).stream().map(file -> {
			try {
				return uploadFileDB(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());
	}

	// Filesystem	
	@PostMapping("/uploadFileFS")
    public DocResponse uploadFile(@RequestParam("file") MultipartFile file) {
        DocDetails docDetails = docDetailsService.uploadFileFS(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/downloadFileFS/")
            .path(docDetails.getDocId())
            .toUriString();
        return new DocResponse(file.getOriginalFilename(), fileDownloadUri,
            file.getContentType(), file.getSize());
    }

	@GetMapping("/downloadFileFS/{docId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = docDetailsService.downloadFileFS(fileName);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
		}
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
//            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
