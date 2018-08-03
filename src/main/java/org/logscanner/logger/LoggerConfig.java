package org.logscanner.logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Victor Kadachigov
 */
class LoggerConfig implements ImportBeanDefinitionRegistrar 
{
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) 
    {
        Class<LoggerAspect> loggerAspectClass = LoggerAspect.class;

        String loggerName = (String) annotationMetadata.getAnnotationAttributes(EnableLogger.class.getName()).get("value");
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(loggerAspectClass).getBeanDefinition();
        registry.registerBeanDefinition(loggerAspectClass.getSimpleName(), beanDefinition);
    }
}
