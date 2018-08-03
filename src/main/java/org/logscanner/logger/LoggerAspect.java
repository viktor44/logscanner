package org.logscanner.logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.logscanner.logger.Logged.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author Victor Kadachigov
 */
@Aspect
class LoggerAspect {
    private static Logger log = LoggerFactory.getLogger(Logged.class);

    @Around(value = "@annotation(Logged) || @within(Logged)")
    public Object logMethodEntry(ProceedingJoinPoint joinPoint) throws Throwable {
        Monitor monitor = null;
        MethodDescription methodDescription = new MethodDescription();
        try {
            methodDescription = tryToGetMethodDescription(joinPoint);
            // Method method = invocationContext.getTarget();
            writeLog(" ==> " + methodDescription.getName() + methodDescription.getDescription(), methodDescription);
            monitor = MonitorFactory.start(methodDescription.getMonitorName());
        } catch (Exception ex) {
            // vk: ошибки логирования не пускаем наружу
            log.error("Ошибка логирования: " + ex.getClass().getName() + ", " + ex.getMessage());
        }
        Object result = null;
        Throwable exception = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception ex) {
            exception = ex;
            throw ex;
        } finally {
            try {
                if (monitor != null)
                    monitor.stop();
                StringBuilder msg = (new StringBuilder()).append(" <== ").append(methodDescription.getName());
                if (exception != null) {
                    msg.append(" ошибка. (").append(exception.getMessage()).append(")");
                } else {
                	msg.append(" : ");
                	if (result == null) {
                        msg.append("null");
                    } else {
                    	if (!log.isDebugEnabled()) {
                            msg.append(result.toString());
                    	} else {
                      	  msg.append(ReflectionToStringBuilder.toString(result, new InfoToStringStyle()));
//                            LogUtils.toLog(result, msg);
                        }
                    }
                }
                msg.append("; Time ").append(formatTime(monitor));
                writeLog(msg.toString(), methodDescription);
            } catch (Exception ex1) {
                // vk: ошибки логирования не пускаем наружу
                log.error("Ошибка логирования: " + ex1.getClass().getName() + ", " + ex1.getMessage());
            }
        }
        return result;
    }

    private void writeLog(String line, MethodDescription methodDescription) {
        if (StringUtils.isNotBlank(methodDescription.getPrefix()))
            line = methodDescription.getPrefix() + " " + line;
        switch (methodDescription.getLevel()) {
        case TRACE:
            log.trace(line);
            break;
        case DEBUG:
            log.debug(line);
            break;
        case WARN:
            log.warn(line);
            break;
        case ERROR:
            log.error(line);
            break;
        case INFO:
        default:
            log.info(line);
            break;
        }
    }

    private MethodDescription tryToGetMethodDescription(ProceedingJoinPoint joinPoint) {
        MethodDescription result = new MethodDescription();

        Class<?> targetClass = joinPoint.getTarget().getClass();
        MethodSignature methodStinature = (MethodSignature) joinPoint.getSignature();
        Method method = methodStinature.getMethod();

        result.setName(method.getName());
        result.setMonitorName(targetClass.getSimpleName() + '.' + method.getName());

        Logged logAs = method.getAnnotation(Logged.class);
        if (logAs == null) // получаем аннотацию на классе, если метод не
                           // зааннотирован
            logAs = targetClass.getAnnotation(Logged.class);

        if (logAs != null) // vk: не знаю как так может быть, но вдруг?
        {
            if (StringUtils.isNotBlank(logAs.value()))
                result.setName(logAs.value());
            else if (logAs.asClass()) {
                result.setName(targetClass.getSimpleName());
                result.setMonitorName(result.getName());
            }

            result.setIncludeParams(logAs.includeParams());
            result.setPrefix(logAs.prefix());
            result.setLevel(logAs.level());

            StringBuilder descr = new StringBuilder();
            if (result.isIncludeParams() && joinPoint.getArgs() != null) {
                boolean firstParamLogged = false;
                StringBuilder params = new StringBuilder();
                for (int i = 0; i < joinPoint.getArgs().length; i++) {
                    Object arg = joinPoint.getArgs()[i];
                    if (!firstParamLogged)
                        firstParamLogged = true;
                    else
                        params.append(", ");
                    LogUtils.toLog(arg, params);
                }
                if (params.length() > 0)
                    descr.append('(').append(params).append(')');
                result.setDescription(descr.toString());
            }
        }

        return result;

    }

    private String formatTime(Monitor monitor) {
        return monitor != null ? String.format("%1$.1f %2$s (cnt %3$.0f; avg %4$.1f)", monitor.getLastValue(), monitor.getUnits(),
                monitor.getHits(), monitor.getAvg()) : "";
    }

    private class MethodDescription {
        private String name = "";
        private String description = "";
        private boolean includeParams = true;
        private String monitorName = "???";
        private String prefix;
        private Level level;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isIncludeParams() {
            return includeParams;
        }

        public void setIncludeParams(boolean includeParams) {
            this.includeParams = includeParams;
        }

        public String getMonitorName() {
            return monitorName;
        }

        public void setMonitorName(String monitorName) {
            this.monitorName = monitorName;
        }

        /**
         * @return the prefix
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * @param prefix
         *            the prefix to set
         */
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public Level getLevel() {
            return level;
        }

        public void setLevel(Level level) {
            this.level = level;
        }
    }
}
