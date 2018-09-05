package org.logscanner.util;

import java.util.Objects;

import org.logscanner.Resources;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Класс для работы со Spring-контекстом приложения
 * 
 * @author Victor Kadachigov
 */
public class ServiceHelper implements ApplicationContextAware {

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean(String name, Class<T> tClass) {
    	Objects.requireNonNull(context, Resources.getStr("error.context_is_null"));
        return context.getBean(name, tClass);
    }

    public static <T> T getBean(Class<T> tClass) {
    	Objects.requireNonNull(context, Resources.getStr("error.context_is_null"));
        return context.getBean(tClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
