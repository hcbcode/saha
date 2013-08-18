package com.hcb.saha.external.source.news;

import java.lang.reflect.Type;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Converts rss response to POJOs.
 * 
 * @author Steven Hadley
 * 
 */
public class RssNewsConverter implements Converter {

	@Override
	public Object fromBody(TypedInput body, Type type)
			throws ConversionException {

		RssParseHandler handler = null;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			handler = new RssParseHandler();
			saxParser.parse(body.in(), handler);
		} catch (Exception e) {
			throw new ConversionException("News conversion error", e);
		}

		return handler.getItems();
	}

	@Override
	public TypedOutput toBody(Object object) {
		return null;
	}

}
