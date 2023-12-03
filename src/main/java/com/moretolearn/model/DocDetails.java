package com.moretolearn.model;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class DocDetails {

	@Id
	@GenericGenerator(name = "doc_id", strategy =  "com.moretolearn.service.DocGen")
	@GeneratedValue(generator = "doc_id")
	@Column(name = "doc_id")
	private String docId;
	
	private String docName;
	
	private String docType;
	
	private String path;

	@Lob
	private byte[] docContent;

	public DocDetails(String docName, String docType, String path, byte[] docContent) {
		super();
		this.docName = docName;
		this.docType = docType;
		this.path = path;
		this.docContent = docContent;
	}
	
	
}
