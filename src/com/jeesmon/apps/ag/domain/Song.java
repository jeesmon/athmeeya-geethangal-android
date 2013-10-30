package com.jeesmon.apps.ag.domain;

import java.io.Serializable;

public class Song implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ID = "_id";
	public static final String SONG_ID = "song_id";
	public static final String TITLE_ML = "title_ml";
	public static final String TITLE_EN = "title_en";
	public static final String FILENAME_ML = "filename_ml";
	public static final String FILENAME_EN = "filename_en";
	
	private int id;
	private String titleMl;
	private String titleEn;
	private String filenameMl;
	private String filenameEn;

	public Song(int id, String titleMl, String titleEn, String filenameMl,
			String filenameEn) {
		super();
		this.id = id;
		this.titleMl = titleMl;
		this.titleEn = titleEn;
		this.filenameMl = filenameMl;
		this.filenameEn = filenameEn;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitleMl() {
		return titleMl;
	}

	public void setTitleMl(String titleMl) {
		this.titleMl = titleMl;
	}

	public String getTitleEn() {
		return titleEn;
	}

	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}

	public String getFilenameMl() {
		return filenameMl;
	}

	public void setFilenameMl(String filenameMl) {
		this.filenameMl = filenameMl;
	}

	public String getFilenameEn() {
		return filenameEn;
	}

	public void setFilenameEn(String filenameEn) {
		this.filenameEn = filenameEn;
	}
}
