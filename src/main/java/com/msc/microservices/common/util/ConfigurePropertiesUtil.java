//package com.msc.microservices.common.util;
//
//import com.ctrip.framework.apollo.ConfigService;
//import com.epet.microservices.common.web.ResponseBody;
//import com.msc.microservices.common.web.ResponseBody;
//import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.support.EncodedResource;
//import org.springframework.core.io.support.PropertiesLoaderUtils;
//
//import java.io.IOException;
//import java.util.Properties;
//
///**
// * 系统配置properties工具类
// *
// * @author zjl
// */
//public final class ConfigurePropertiesUtil {
//    private static final Properties CONFIGURE_PROPERTIES = new Properties();
//    private static final String WEB_BUSINESS_CODE = "web.businessCode";
//    public static final String SIMPLE_FAIL_STATUS_CODE_PROP = "simpleFailStatusCode";
//    private static final boolean IS_APOLLO_PRESENT;
//
//    static {
//        boolean exist = false;
//        try {
//            // 小心加载类的时候会触发apollo的一系列动作,造成未读取application.yml就提前去拉取配置信息,所以要保证这个类在启动后再使用
//            Class.forName("com.ctrip.framework.apollo.ConfigService");
//            exist = true;
//        } catch (ClassNotFoundException ex) {
//            // Ignore
//        }
//        IS_APOLLO_PRESENT = exist;
//        try {
//            addClasspathProperties("application.properties");
//        } catch (Throwable t) {
//            try {
//                YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
//                yamlPropertiesFactoryBean.setResources(new ClassPathResource("application.yml"));
//                Properties properties = yamlPropertiesFactoryBean.getObject();
//                CONFIGURE_PROPERTIES.putAll(properties);
//            } catch (Throwable th) {
//                throw new IllegalStateException("application.properties/yml文件不存在", th);
//            }
//        }
//        // 手动设置失败编码
//        int businessCode = getAsInt(WEB_BUSINESS_CODE);
//        if (businessCode > 0) {
//            CONFIGURE_PROPERTIES.setProperty(SIMPLE_FAIL_STATUS_CODE_PROP, ResponseBody.SUCCESS_STATUS_CODE + businessCode);
//        }
//    }
//
//    /**
//     * 增加classpath路径properties文件
//     *
//     * @param paths 路径
//     */
//    public static void addClasspathProperties(String... paths) {
//        if (paths != null) {
//            try {
//                for (String path : paths) {
//                    EncodedResource encodedResource = new EncodedResource(new ClassPathResource(path), "UTF-8");
//                    PropertiesLoaderUtils.fillProperties(CONFIGURE_PROPERTIES, encodedResource);
//                }
//            } catch (IOException ex) {
//                throw new IllegalStateException(ex);
//            }
//        }
//    }
//
//    public static String getProperty(String key) {
//        return doGetProperty(key);
//    }
//
//    public static String getProperty(String key, String defaultValue) {
//        String result = doGetProperty(key);
//        return result == null ? defaultValue : result;
//    }
//
//    public static int getAsInt(String key) {
//        Object result = doGet(key);
//        if (result != null) {
//            return result instanceof Integer ? (int) result : result instanceof String ? Integer.valueOf((String) result) : 0;
//        }
//        return 0;
//    }
//
//    public static int getAsInt(String key, int defaultValue) {
//        Object result = doGet(key);
//        if (result != null) {
//            return result instanceof Integer ? (int) result : result instanceof String ? Integer.valueOf((String) result) : defaultValue;
//        }
//        return defaultValue;
//    }
//
//    private static String doGetProperty(String key) {
//        String result = CONFIGURE_PROPERTIES.getProperty(key);
//        // 特殊处理
//        if (SIMPLE_FAIL_STATUS_CODE_PROP.equals(key) && result == null) {
//            key = WEB_BUSINESS_CODE;
//        }
//        return result == null ? (IS_APOLLO_PRESENT ? ConfigService.getAppConfig().getProperty(key, null) : null) : result;
//    }
//
//    private static Object doGet(String key) {
//        Object result = CONFIGURE_PROPERTIES.get(key);
//        return result == null ? (IS_APOLLO_PRESENT ? ConfigService.getAppConfig().getProperty(key, null) : null) : result;
//    }
//
//
//    public static void setSimpleFailStatusCode() {
//        ResponseBody.FAIL_STATUS_CODE = getProperty(SIMPLE_FAIL_STATUS_CODE_PROP, ResponseBody.FAIL_STATUS_CODE);
//    }
//}
