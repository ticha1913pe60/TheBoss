package com.petarzlatev.languageclasses.dao.hibernate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, String>  {

	@Override
	public String convertToDatabaseColumn(LocalDate attribute) {
		return attribute.format(DateTimeFormatter.ISO_LOCAL_DATE);	
	}

	@Override
	public LocalDate convertToEntityAttribute(String dbData) {
		return LocalDate.parse(dbData, DateTimeFormatter.ISO_LOCAL_DATE);
	}

}
