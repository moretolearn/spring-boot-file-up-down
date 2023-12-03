package com.moretolearn.controller;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.moretolearn.model.DocDetails;
import com.moretolearn.service.DocDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class DocDetailsController {

	@Autowired
	DocDetailsService docDetailsService;

	// DB
	@PostMapping("/uploadFileDB")
	public String uploadFileDB(@RequestParam("file") MultipartFile file, ModelMap map) throws IOException {
		String message = "";
		DocDetails docDetails = null;
		if (file.isEmpty()) {
			message = "Could not upload the file!!";
		} else {
			docDetails = docDetailsService.uploadFileDB(file);
			message = "Uploaded the file successfully: " + file.getOriginalFilename();
		}
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFileDB/")
				.path(docDetails.getDocId()).toUriString();
		map.put("message", message);
		map.put("downloadurl", fileDownloadUri);
		return "success";

	}

	@GetMapping("/downloadFileDB/{docId}")
	public void downloadFileDB(@PathVariable("docId") String docId, HttpServletResponse response) throws IOException {

		DocDetails docDetails = docDetailsService.downloadFileDB(docId);
		try {
			response.setHeader("Content-Disposition", "inline;filename=\"" + docDetails.getDocName() + "\"");
			response.setContentType(docDetails.getDocType());
			InputStream ins = new ByteArrayInputStream(docDetails.getDocContent());
			IOUtils.copy(ins, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/")
	public String showIndex() {

		return "index";
	}

	// Filesystem
	@PostMapping("/uploadFileFS")
	public String uploadFileFS(@RequestParam("file") MultipartFile file, ModelMap map) {
		String message = "";
		String docId = null;
		if (file.getSize() > 0) {
			try {
				String orgId = "2023";
				String userId = "12";
				String docCatagory = "docs";
				DocDetails docDetails = docDetailsService.uploadFileFS(file, userId, orgId, docCatagory);
				docId = docDetails.getDocId();
				message = "Uploaded the file successfully: " + file.getOriginalFilename();
			} catch (Exception e) {
				e.printStackTrace();
				message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			}
		}
		if (null != docId && !docId.isEmpty()) {
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFileFS/")
					.path(docId).toUriString();
			map.put("downloadurl", fileDownloadUri);
		}
		map.put("message", message);
		return "success";

	}

	@GetMapping("/downloadFileFS/{docId}")
	public ResponseEntity<Resource> downloadFileFS(@PathVariable String docId, HttpServletResponse response)
			throws IOException {
		DocDetails docDetails = docDetailsService.downloadFileDB(docId);
		String folderPath = docDetails.getPath();
		String fileName = docDetails.getDocName();
		response.setHeader("Content-Disposition", "inline;filename=\"" + fileName + "\"");
		InputStreamResource resource = new InputStreamResource(new FileInputStream(folderPath));
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}
}
