//package com.msc.microservices.common.spring.converter;
//
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.core.convert.converter.ConverterFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 可反序列化枚举的转换器工厂
// *
// * @author zjl
// */
//public class DeserializableEnumConverterFactory implements ConverterFactory<String, DeserializableEnum> {
//    private static final Map<Class, Converter> CONVERTER_CACHE = new HashMap<>();
//
//    @Override
//    public <T extends DeserializableEnum> Converter<String, T> getConverter(Class<T> targetType) {
//        return CONVERTER_CACHE.computeIfAbsent(targetType, IntegerToEnumConverter::new);
//    }
//
//    class IntegerToEnumConverter<T extends DeserializableEnum> implements Converter<String, T> {
//        private final Class<T> enumType;
//        private final Map<String, T> enumCache = new HashMap<>();
//
//        public IntegerToEnumConverter(Class<T> enumType) {
//            this.enumType = enumType;
//            T[] enums = enumType.getEnumConstants();
//            for (T e : enums) {
//                enumCache.put(String.valueOf(e.getValue()), e);
//            }
//        }
//
//        @Override
//        public T convert(String source) {
//            T result = enumCache.get(source);
//            // 直接返回空,即使抛异常也会被上层方法捕捉，减少异常出栈消耗
//            return result;
//        }
//    }
//}
