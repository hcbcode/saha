package com.hcb.saha.external.news;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.net.Uri;

/**
 * Generic RSS 2.0 parser.
 * 
 * @author Steven Hadley
 * 
 */
public class RssParseHandler extends DefaultHandler {

	private List<NewsItem> rssItems;
	private NewsItem currentItem;
	private boolean parsingTitle;
	private boolean parsingDescription;
	private boolean parsingLink;
	private boolean parsingDdate;

	public RssParseHandler() {
		rssItems = new ArrayList<NewsItem>();
	}

	public List<NewsItem> getItems() {
		return rssItems;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("item".equals(qName)) {
			currentItem = new NewsItem();
		} else if ("title".equals(qName)) {
			parsingTitle = true;
		} else if ("link".equals(qName)) {
			parsingLink = true;
		} else if ("pubDate".equals(qName)) {
			parsingDdate = true;
		} else if ("description".equals(qName)) {
			parsingDescription = true;
		} else if ("enclosure".equals(qName)) {
			if (currentItem != null)
				currentItem.setImage(Uri.parse(attributes.getValue("url")));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("item".equals(qName)) {
			rssItems.add(currentItem);
			currentItem = null;
		} else if ("title".equals(qName)) {
			parsingTitle = false;
		} else if ("link".equals(qName)) {
			parsingLink = false;
		} else if ("pubDate".equals(qName)) {
			parsingDdate = false;
		} else if ("description".equals(qName)) {
			parsingDescription = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (parsingTitle) {
			if (currentItem != null)
				currentItem.setTitle(new String(ch, start, length));
		} else if (parsingLink) {
			if (currentItem != null) {
				currentItem.setLink(new String(ch, start, length));
				parsingLink = false;
			}
		} else if (parsingDdate) {
			if (currentItem != null) {
				currentItem.setPubDate(new String(ch, start, length));
				parsingDdate = false;
			}
		} else if (parsingDescription) {
			if (currentItem != null) {
				currentItem.setDescription(new String(ch, start, length));
				parsingDescription = false;
			}
		}
	}
}