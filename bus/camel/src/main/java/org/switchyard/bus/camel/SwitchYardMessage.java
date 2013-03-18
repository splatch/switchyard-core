package org.switchyard.bus.camel;

import java.util.Map;

import org.apache.camel.impl.DefaultMessage;
import org.switchyard.ExchangePhase;

public class SwitchYardMessage extends DefaultMessage {

    @Override
    public void setHeaders(Map<String, Object> headers) {
        super.setHeaders(headers);

        if (getExchange() != null) {
            if (ExchangePhase.IN == CamelExchange.getPhase(getExchange())) {
                CamelExchange.setInHeaders(getExchange(), getHeaders());
            }
        }
    }

}
