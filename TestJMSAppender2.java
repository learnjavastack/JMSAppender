package com.fmr.test;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.fmr.jms.CustomJmsAppender;

public class TestJMSAppender2 {

    public static void main(String[] args) {
        configure();
        Logger logger = LogManager.getLogger(TestJMSAppender2.class.getName());
        logger.trace("Hello DON!");
        logger.info("Hello DON! Hello DON! Hello DON!");
        LogManager.shutdown();
    }

    public static void configure() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        AbstractConfiguration config = (AbstractConfiguration) ctx.getConfiguration();
        Appender appender = CustomJmsAppender.createAppender("jmsQueue","logQueue",PatternLayout.createDefaultLayout(), null);
        appender.start();
        config.addAppender(appender);
        AppenderRef[] refs = new AppenderRef[] { AppenderRef.createAppenderRef(appender.getName(), null, null) };
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.INFO, LogManager.ROOT_LOGGER_NAME, "true", refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        config.addLogger(LogManager.ROOT_LOGGER_NAME, loggerConfig);
        ctx.updateLoggers();
    }
}