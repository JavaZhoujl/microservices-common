package com.msc.microservices.common.web.mvc.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * java 8 LocalDateTime转换器
 *
 * @author zjl
 */
public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        return LocalDateTime.parse(text, formatter);
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return formatter.format(object);
    }
}
