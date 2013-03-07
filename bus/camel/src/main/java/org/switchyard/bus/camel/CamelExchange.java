package org.switchyard.bus.camel;

import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.model.ModelCamelContext;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
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

    private SecurityContext         _securityContext = new SecurityContext();
    private org.apache.camel.Exchange _exchange;
    private Service _provider;
    private ServiceReference _consumer;

    public CamelExchange(ModelCamelContext context, ExchangeHandler handler) {
        _exchange = new DefaultExchange(context);
    }

    @Override
    public Context getContext() {
        return new CamelContext(_exchange);
    }

    @Override
    public ServiceReference getConsumer() {
        return null;
    }

    @Override
    public Service getProvider() {
        return _provider;
    }

    @Override
    public ExchangeContract getContract() {
        return null;
    }

    @Override
    public Exchange consumer(ServiceReference consumer, ServiceOperation operation) {
        _consumer = consumer;
        return this;
    }

    @Override
    public Exchange provider(Service provider, ServiceOperation operation) {
        _provider = provider;
        return this;
    }

    @Override
    public Message getMessage() {
        return new CamelMessage(_exchange, _exchange.getIn());
    }

    @Override
    public Message createMessage() {
        return new CamelMessage(_exchange);
    }

    @Override
    public void send(Message message) {
        
    }

    @Override
    public void sendFault(Message message) {

    }

    @Override
    public ExchangeState getState() {
        return _exchange.isFailed() ? ExchangeState.FAULT : ExchangeState.OK;
    }

    @Override
    public ExchangePhase getPhase() {
        return _exchange.hasOut() ? ExchangePhase.OUT : ExchangePhase.IN;
    }

    @Override
    public SecurityContext getSecurityContext() {
        return _securityContext;
    }

    public org.apache.camel.Exchange getExchange() {
        return _exchange;
    }

}
