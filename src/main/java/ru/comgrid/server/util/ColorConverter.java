package ru.comgrid.server.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ColorConverter implements AttributeConverter<Color, String>{
    @Override
    public String convertToDatabaseColumn(Color attribute){
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public Color convertToEntityAttribute(String dbData){
        return dbData == null ? null : Color.ofString(dbData);
    }
}
