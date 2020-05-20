package com.petarzlatev.languageclasses.dao.hibernate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

	@Override
	public String convertToDatabaseColumn(LocalDateTime attribute) {
		return attribute.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);	
	}

	@Override
	public LocalDateTime convertToEntityAttribute(String dbData) {
		return LocalDateTime.parse(dbData, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

}
