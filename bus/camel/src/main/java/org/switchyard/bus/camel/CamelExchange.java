package org.switchyard.bus.camel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.util.CaseInsensitiveMap;
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
    private static final String IN_HEADERS    = "org.switchyard.bus.camel.headers.in";

    private SecurityContext _securityContext = new SecurityContext();
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
        return new CamelMessage(new SwitchYardMessage());
    }

    @Override
    public void send(Message message) {
        org.apache.camel.Message camelMsg = extract(message);

        if (getPhase() == null) {
            // specify active phase to IN
            _exchange.setProperty(PHASE, ExchangePhase.IN);

            // here is more tricky part, as we will lose our IN message with all previous headers
            // we need keep these headers somewhere - in this case in exchange property
           _exchange.setProperty(IN_HEADERS, _exchange.getIn().getHeaders());

           // as there is no phase for exchange we receive first "send" call
            // specify message for exchange to obtain message id
            _exchange.setIn(camelMsg);
            getContext().setProperty(Exchange.MESSAGE_ID, camelMsg.getMessageId(), Scope.IN);
        } else {
            // store message
            _exchange.setIn(camelMsg);
            // first of all activate OUT phase
            _exchange.setProperty(PHASE, ExchangePhase.OUT);
            // let obtain message id for IN message
            Property property = getContext().getProperty(MESSAGE_ID, Scope.IN);
            // and link outgoing message with message which is lost
            getContext().setProperty(Exchange.RELATES_TO, property.getValue(), Scope.OUT);
            // finally specify message for camel exchange
            // it doesn't meter if it's IN or OUT as camel move OUT to IN during walking
            // trough next processors - if we have no OUT message Camel will assume that
            // current IN is OUT for exchange
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
        _exchange.setOut(extract(message));
        //_exchange.getOut().setFault(true);
        _exchange.setProperty(PHASE, ExchangePhase.OUT);
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
        return _exchange.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT) != null ? ExchangeState.FAULT : ExchangeState.OK;
    }

    @Override
    public ExchangePhase getPhase() {
        return _exchange.getProperty(PHASE, ExchangePhase.class);
    }

    @Override
    public SecurityContext getSecurityContext() {
        return _securityContext;
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

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getInHeaders(org.apache.camel.Exchange exchange) {
        return exchange.getProperty(IN_HEADERS, new CaseInsensitiveMap(), Map.class);
    }

    public static void setInHeaders(org.apache.camel.Exchange exchange, Map<String, Object> headers) {
        exchange.setProperty(IN_HEADERS, headers);
    }

    public static Map<String, Object> getOutHeaders(org.apache.camel.Exchange exchange) {
        if (ExchangePhase.OUT == CamelExchange.getPhase(exchange)) {
            return exchange.getIn().getHeaders();
        }
        throw new IllegalStateException("Out properties are not available during IN phase");
    }

}
