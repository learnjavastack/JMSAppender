package com.fmr.jms;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;


@Plugin(name = "CustomJmsAppender", category = "Core", elementType = "appender", printObject = true)
public class CustomJmsAppender extends AbstractAppender {
    private String brokerUri = "tcp://localhost:61616";
    private String queueName = "";
    Session session;
	
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.brokerUri);
    javax.jms.Connection connection;
    Destination destination;
    MessageProducer producer;
    
protected CustomJmsAppender(String name, String queueName, Filter filter, Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
    super(name, filter, layout, ignoreExceptions);
    this.queueName = queueName;
    init();
}

@Override
public void append(LogEvent le) {
    try {
        if (connection == null) {
            init();
        }
        TextMessage message = session.createTextMessage(le.getMessage().getFormattedMessage());
        // Tell the producer to send the message
        producer.send(message);
    } catch (Exception e) {
        e.printStackTrace();
    }
}


@Override
public void stop() {
    super.stop(); 
    try {
    	producer.close();
        session.close();
        connection.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

@Override
public boolean stop(long timeout, TimeUnit timeUnit) {
	System.err.println("stop(" + timeout + "," + timeUnit + ") called on " + System.identityHashCode(this));
    try {
    	producer.close();
        session.close();
        connection.close();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
    	producer = null;
    	session = null;
    	connection = null;
    }
	return super.stop(timeout, timeUnit);

}

@PluginFactory
public static CustomJmsAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("queueName") String queueName,
        @PluginElement("PatternLayout") Layout<? extends Serializable> layout, @PluginElement("Filter") final Filter filter) {
    if (name == null) {
        LOGGER.error("No name provided for CustomJmsAppender");
        return null;
    }
    if (layout == null) {
        layout = PatternLayout.createDefaultLayout();
    }
    return new CustomJmsAppender(name, queueName, filter, getLayout(layout), true);
}



private static Layout<? extends Serializable> getLayout(Layout<? extends Serializable> layout) {
    Layout<? extends Serializable> finalLayout = layout;
    if (finalLayout == null) {
        finalLayout = PatternLayout.createDefaultLayout();
    }
    return finalLayout;
}

private void init() {
    try {

        connection = connectionFactory.createConnection();
        connection.start();
        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(this.queueName);
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
