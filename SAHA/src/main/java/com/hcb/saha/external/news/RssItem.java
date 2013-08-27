package com.hcb.saha.external.news;

import java.util.Date;

import android.net.Uri;

/**
 * RSS POJO.
 * 
 * @author Steven Hadley
 * 
 */
public class RssItem {

	private String title;
	private String link;
	private String pubDate;
	private String description;
	private Date readDate;
	private Uri image;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public Uri getImage() {
		return this.image;
	}

	public void setImage(Uri image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

}
