package org.switchyard.bus.camel;

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangePhase;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.SecurityExchange;

public class CamelExchange implements SecurityExchange {

    private org.apache.camel.Exchange _exchange;


    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public ServiceReference getConsumer() {
        return null;
    }

    @Override
    public Service getProvider() {
        return null;
    }

    @Override
    public ExchangeContract getContract() {
        return null;
    }

    @Override
    public Exchange consumer(ServiceReference consumer, ServiceOperation operation) {
        return null;
    }

    @Override
    public Exchange provider(Service provider, ServiceOperation operation) {
        return null;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public Message createMessage() {
        return new CamelMessage();
    }

    @Override
    public void send(Message message) {
        
    }

    @Override
    public void sendFault(Message message) {

    }

    @Override
    public ExchangeState getState() {
        return null;
    }

    @Override
    public ExchangePhase getPhase() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SecurityContext getSecurityContext() {
        return null;
    }

}
