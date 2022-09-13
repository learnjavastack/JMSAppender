package com.fmr.test;
import java.util.zip.Deflater;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CronTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.fmr.jms.CustomJmsAppender;

public class TestJMSRollingAppender {

    public static void main(String[] args) {
        configure();
        Logger logger = LogManager.getLogger(TestJMSRollingAppender.class.getName());
        logger.trace("Hello trace!");
        logger.info("Hello info! Hello info! Hello info!");
        logger.warn("Hello WARN! Hello WARN! Hello WARN!");
        logger.debug("Hello Debugggggggggg");
        logger.error("Hello errorrrrrrrrrrrrr");
        LogManager.shutdown();
    }

    public static void configure() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        AbstractConfiguration config = (AbstractConfiguration) ctx.getConfiguration();
        Appender appender = CustomJmsAppender.createAppender("jmsQueue","logQueue",PatternLayout.createDefaultLayout(), null);
        appender.start();
        config.addAppender(appender);
        AppenderRef[] refs = new AppenderRef[] { AppenderRef.createAppenderRef(appender.getName(), null, null) };
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.ALL, LogManager.ROOT_LOGGER_NAME, "true", refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        config.addLogger(LogManager.ROOT_LOGGER_NAME, loggerConfig);
//        ctx.updateLoggers();
        
        
	    DefaultRolloverStrategy strategy = DefaultRolloverStrategy.newBuilder()
	            .withMax("7")
	            .withMin("1")
	            .withFileIndex("max")
	            .withConfig(config)
	            .withCompressionLevelStr(Deflater.NO_COMPRESSION + "")
	            .build();
        RollingFileAppender.Builder builder = RollingFileAppender.newBuilder();
        PatternLayout layout = PatternLayout.newBuilder().withConfiguration(config).withPattern("%d [%t] %-5level: %msg%n%throwable").build();
	    builder.withFileName("E:\\log4j2-POC\\rolling.log");
	    builder.withFilePattern("rolling-%d{MM-dd-yy}.log.gz");
	    builder.withPolicy(CronTriggeringPolicy.createPolicy(config, Boolean.TRUE.toString(), "0 0 12 1/1 * ? *"));
	    builder.withStrategy(strategy);
	    builder.setLayout(layout);
	    builder.setName("DailyRollingFileJMSAppender");
	    builder.setConfiguration(config);
	    RollingFileAppender raf = builder.build();
	    config.addAppender(raf);
        AppenderRef[] rafs = new AppenderRef[] { AppenderRef.createAppenderRef(raf.getName(), null, null) };
        LoggerConfig rafloggerConfig = LoggerConfig.createLogger("false", Level.ALL, LogManager.ROOT_LOGGER_NAME, "true", rafs, null, config, null);
        loggerConfig.addAppender(raf, null, null);
        config.addLogger(LogManager.ROOT_LOGGER_NAME, rafloggerConfig);
	    ctx.updateLoggers();
	    
    }
    
}