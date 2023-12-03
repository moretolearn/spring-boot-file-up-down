package com.moretolearn.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocResponse {

	private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}
