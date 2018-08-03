package org.logscanner.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Анотация для вызов интерцептора отвечающего за логирование
 * 
 * @author Victor Kadachigov
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Logged {
    /**
     * Имя выводимое в лог. При установке перекрывает остальные настройки
     */
    String value() default "";

    /**
     * Выводить в лог имя класса вместо названия метода<p>
     * По-умолчанию <code>false</code>
     */
    boolean asClass() default false;

    /**
     * Выводить в лог параметры вызова<p>
     * По-умолчанию <code>true</code>
     */
    boolean includeParams() default true;
    
    /**
     * Префикс, который будет ставится перед строкой лога
     */
    String prefix() default "";
    
    /**
     * Уровень логгирования
     * По-умолчанию <code>INFO</code>
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
