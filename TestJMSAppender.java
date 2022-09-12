package com.fmr.test;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

public class TestJMSAppender implements MessageListener {

	private static Logger logger = null;

        public TestJMSAppender() {
                try {

                        for (int i = 0; i < 1; i++) {
                                logger.info("Testing ....");
                                logger.info("Hello World!");
                        }
                        Thread.sleep(1000);
                       LogManager.shutdown();
                        System.out.println("*********END**********");
                       
                }
                catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        public void onMessage(Message msg) {

        }

        public static void main(String... args) {
                 logger = LogManager.getLogger("com.fmr.jms");

                new TestJMSAppender();
        }
}
