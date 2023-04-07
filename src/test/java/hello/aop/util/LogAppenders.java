package hello.aop.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogAppenders {
    public static final String ROOT_PACKAGE_NAME = "hello.aop";
    protected ListAppender<ILoggingEvent> listAppender;
    private List<Logger> loggers;
    private LoggerContext loggerContext;

    @BeforeEach
    void setUp() {
        setLogAppenderInfo();
        logAppendStart();
    }

    @AfterEach
    public void tearDown() {
        logAppendEnd();
    }

    private void setLogAppenderInfo() {
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggers = loggerContext.getLoggerList()
                .stream()
                .filter(LogAppenders::isSaveLogTarget)
                .toList();
        listAppender = new ListAppender<>();
    }

    private static boolean isSaveLogTarget(Logger logger) {
        String loggerName = logger.getName();
        boolean isUserLogger = loggerName.startsWith(ROOT_PACKAGE_NAME);
        boolean isClassLogger = Pattern.compile("[A-Z]").matcher(loggerName).find();
        return isUserLogger && isClassLogger;
    }

    private void logAppendStart() {
        listAppender.start();
        loggers.stream()
                .forEach(o -> o.addAppender(listAppender));
    }

    private void logAppendEnd() {
        loggers.stream()
                .forEach(o -> o.detachAppender(listAppender));
        listAppender.stop();
    }

    protected Optional<ILoggingEvent> getContainsLog(String expectedLog) {
        return listAppender.list
                .stream()
                .filter(o -> {
                    if (o != null) return o.getFormattedMessage().contains(expectedLog);
                    return false;
                })
                .findAny();
    }

    protected List<String> getOrderedLogs() {
        if (ObjectUtils.isEmpty(listAppender.list)) {
            return null;
        }
        List<String> logs = listAppender.list
                .stream()
                .map(o -> o.toString())
                .collect(Collectors.toList());
        return logs;
    }
}
