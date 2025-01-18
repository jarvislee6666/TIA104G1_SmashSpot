package com.smashspot.member.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class StringToDateConverter implements Converter<String, Date> {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        try {
            java.util.Date parsed = formatter.parse(source);
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd");
        }
    }
}