package org.logscanner.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for logging
 * @author Victor Kadachigov
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Logged {
    /**
     * Log name
     */
    String value() default "";

    /**
     * Log class name instead of method<p>
     * <code>false</code> by default
     */
    boolean asClass() default false;

    /**
     * Log method parameters<p>
     * <code>true</code> by default
     */
    boolean includeParams() default true;
    
    /**
     * Prefix
     */
    String prefix() default "";
    
    /**
     * Log level<p>
     * <code>INFO</code> by default
     */
    Level level() default Level.INFO;
    
    enum Level {
    	TRACE,
    	DEBUG,
    	INFO,
    	WARN,
    	ERROR
    }
}
