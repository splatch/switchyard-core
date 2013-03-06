package org.switchyard.bus.camel;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.camel.Exchange;
import org.switchyard.Message;

public class CamelMessage implements Message {

    private final org.apache.camel.Message _message;

    public CamelMessage(Exchange exchange) {
        this(exchange.getIn());
    }

    public CamelMessage(org.apache.camel.Message message) {
        _message = message;
    }

    @Override
    public Message setContent(Object content) {
        _message.setBody(content);
        return this;
    }

    @Override
    public Object getContent() {
        return _message.getBody();
    }

    @Override
    public <T> T getContent(Class<T> type) {
        return _message.getBody(type);
    }

    @Override
    public Message addAttachment(String name, DataSource attachment) {
        _message.addAttachment(name, new DataHandler(attachment));
        return this;
    }

    @Override
    public DataSource getAttachment(String name) {
        DataHandler attachement = _message.getAttachment(name);
        return attachement != null ? attachement.getDataSource() : null;
    }

    @Override
    public DataSource removeAttachment(String name) {
        DataSource attachment = getAttachment(name);
        if (attachment != null) {
            _message.removeAttachment(name);
        }
        return attachment;
    }

    @Override
    public Map<String, DataSource> getAttachmentMap() {
        Map<String, DataSource> attachements = new HashMap<String, DataSource>();
        for (Entry<String, DataHandler> attachement : _message.getAttachments().entrySet()) {
            attachements.put(attachement.getKey(), attachement.getValue().getDataSource());
        }
        return attachements;
    }

    public org.apache.camel.Message getMessage() {
        return _message;
    }

}
