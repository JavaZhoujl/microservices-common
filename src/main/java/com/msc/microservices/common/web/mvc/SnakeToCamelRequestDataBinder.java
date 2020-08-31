package com.msc.microservices.common.web.mvc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msc.microservices.common.util.StringUtil;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;

/**
 * snake参数转换为camel参数数据绑定器
 *
 * @author zjl
 */
public class SnakeToCamelRequestDataBinder extends ExtendedServletRequestDataBinder {
    public SnakeToCamelRequestDataBinder(Object target, String objectName) {
        super(target, objectName);
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        super.addBindValues(mpvs, request);

        Class<?> targetClass = getTarget().getClass();
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null && mpvs.contains(jsonProperty.value())) {
                if (!mpvs.contains(field.getName())) {
                    mpvs.add(field.getName(), mpvs.getPropertyValue(jsonProperty.value()).getValue());
                }
            }
        }
        // TODO 后续可能需要关闭这项特性
        List<PropertyValue> convertValues = new ArrayList<>();
        for (PropertyValue propertyValue : mpvs.getPropertyValueList()) {
            if (propertyValue.getName().contains("_")) {
                String camelName = StringUtil.snake2Camel(propertyValue.getName());
                if (!mpvs.contains(camelName)) {
                    convertValues.add(new PropertyValue(camelName, propertyValue.getValue()));
                }
            }
        }
        mpvs.getPropertyValueList().addAll(convertValues);
    }
}
