package org.switchyard.bus.camel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePhase;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.Service;
import org.switchyard.ServiceReference;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.security.SecurityContext;
import org.switchyard.security.SecurityExchange;

public class CamelExchange implements SecurityExchange {

    private static final String DISPATCHER    = "org.switchyard.bus.camel.dispatcher";
    private static final String CONSUMER      = "org.switchyard.bus.camel.consumer";
    private static final String PROVIDER      = "org.switchyard.bus.camel.provider";
    private static final String CONTRACT      = "org.switchyard.bus.camel.contract";
    private static final String REPLY_HANDLER = "org.switchyard.bus.camel.replyHandler";
    private static final String PHASE         = "org.switchyard.bus.camel.phase";
    private static final String FAULT         = "org.switchyard.bus.camel.fault";
    private static final String SECURITY_CONTEXT = "org.switchyard.bus.camel.securityContext";
    public static final String MESSAGE        = "org.switchyard.bus.camel.message";

    private org.apache.camel.Exchange _exchange;

    public CamelExchange(ExchangeDispatcher dispatch, org.apache.camel.Exchange exchange, ExchangeHandler replyHandler) {
        _exchange = exchange;

        Map<String, Object> properties = exchange.getProperties();
        if (!properties.containsKey(DISPATCHER)) {
            _exchange.setProperty(DISPATCHER, dispatch);
        }
        if (!properties.containsKey(REPLY_HANDLER)) {
            _exchange.setProperty(REPLY_HANDLER, replyHandler);
        }
    }

    public CamelExchange(org.apache.camel.Exchange exchange) {
        this(exchange.getProperty(DISPATCHER, ExchangeDispatcher.class), exchange, exchange.getProperty(REPLY_HANDLER, ExchangeHandler.class));
    }

    @Override
    public Context getContext() {
        return new CamelContext(_exchange);
    }

    @Override
    public ServiceReference getConsumer() {
        return _exchange.getProperty(CONSUMER, ServiceReference.class);
    }

    @Override
    public Service getProvider() {
        return _exchange.getProperty(PROVIDER, Service.class);
    }

    @Override
    public BaseExchangeContract getContract() {
        if (_exchange.getProperty(CONTRACT) == null) {
            _exchange.setProperty(CONTRACT, new BaseExchangeContract());
        }
        return _exchange.getProperty(CONTRACT, BaseExchangeContract.class);
    }

    @Override
    public Exchange consumer(ServiceReference consumer, ServiceOperation operation) {
        _exchange.setProperty(CONSUMER, consumer);
        getContract().setConsumerOperation(operation);
        return this;
    }

    @Override
    public Exchange provider(Service provider, ServiceOperation operation) {
        _exchange.setProperty(PROVIDER, provider);
        getContract().setProviderOperation(operation);
        return this;
    }

    @Override
    public Message getMessage() {
        return new CamelMessage(_exchange.getIn());
    }

    @Override
    public Message createMessage() {
        return new CamelMessage(new SwitchYardMessage(_exchange));
    }

    @Override
    public void send(Message message) {
        org.apache.camel.Message camelMsg = extract(message);

        if (getPhase() == null) {
            if (camelMsg != _exchange.getIn()) {
                camelMsg.getHeaders().putAll(_exchange.getIn().getHeaders());
            }
            _exchange.setProperty(PHASE, ExchangePhase.IN);
            _exchange.setIn(camelMsg);
            getContext().setProperty(Exchange.MESSAGE_ID, camelMsg.getMessageId(), Scope.IN);
        } else {
            Property property = getContext().getProperty(MESSAGE_ID, Scope.IN);
            if (camelMsg != _exchange.getIn()) {
                camelMsg.getHeaders().putAll(_exchange.getIn().getHeaders());
            }
            _exchange.setIn(camelMsg);
            getContext().setProperty(Exchange.RELATES_TO, property.getValue(), Scope.OUT);
            getContext().setProperty(Exchange.MESSAGE_ID, camelMsg.getMessageId(), Scope.OUT);

        }

        initInContentType();
        sendInternal();
    }

    private org.apache.camel.Message extract(Message message) {
        if (message instanceof CamelMessage) {
            return ((CamelMessage) message).getMessage();
        }
        throw new IllegalArgumentException("CamelExchange accepts only CamelMessages");
    }

    @Override
    public void sendFault(Message message) {
        org.apache.camel.Message extract = extract(message);
        if (getPhase() == ExchangePhase.OUT && extract != _exchange.getIn()) {
            extract.getHeaders().putAll(_exchange.getIn().getHeaders());
        }
        activateOut();
        _exchange.setOut(extract);
        _exchange.setProperty(FAULT, true);

        //_exchange.getOut().setFault(true);
        sendInternal();
    }

    private void initInContentType() {
        QName exchangeInputType = getContract().getConsumerOperation().getInputType();

        if (exchangeInputType != null) {
            getContext().setProperty(Exchange.CONTENT_TYPE, exchangeInputType, Scope.IN)
                .addLabels(BehaviorLabel.TRANSIENT.label());
        }
    }

    private void sendInternal() {
         _exchange.getProperty(DISPATCHER, ExchangeDispatcher.class).dispatch(this);
    }

    @Override
    public ExchangeState getState() {
        return _exchange.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT) != null || _exchange.isFailed() 
            || _exchange.getProperty(FAULT, false, Boolean.class) ? ExchangeState.FAULT : ExchangeState.OK;
    }

    @Override
    public ExchangePhase getPhase() {
        return _exchange.getProperty(PHASE, ExchangePhase.class);
    }

    @Override
    public SecurityContext getSecurityContext() {
        if (!_exchange.getProperties().containsKey(SECURITY_CONTEXT)) {
            _exchange.setProperty(SECURITY_CONTEXT, new SecurityContext());
        }
        return _exchange.getProperty(SECURITY_CONTEXT, SecurityContext.class);
    }

    public org.apache.camel.Exchange getExchange() {
        return _exchange;
    }

    @Override
    public ExchangeHandler getReplyHandler() {
        return _exchange.getProperty(REPLY_HANDLER, ExchangeHandler.class);
    }

    public static ExchangePhase getPhase(org.apache.camel.Exchange exchange) {
        return exchange.getProperty(PHASE, ExchangePhase.class);
    }

    public void activateOut() {
        if (ExchangePhase.OUT != getPhase()) {
            _exchange.setProperty(MESSAGE, _exchange.getIn().copy());
            _exchange.setProperty(PHASE, ExchangePhase.OUT);
        }
    }

}