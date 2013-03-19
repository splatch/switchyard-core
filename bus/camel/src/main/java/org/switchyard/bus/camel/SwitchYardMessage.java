package org.switchyard.bus.camel;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.switchyard.ExchangePhase;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.java.JavaService;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;

public class SwitchYardMessage extends DefaultMessage {

    private Logger _logger = LoggerFactory.getLogger(SwitchYardMessage.class);

    public SwitchYardMessage(Exchange exchange) {
        setExchange(exchange);
    }

    @Override
    public void setHeaders(Map<String, Object> headers) {
        super.setHeaders(headers);

        if (getExchange() != null) {
            if (ExchangePhase.IN == CamelExchange.getPhase(getExchange())) {
                CamelExchange.setInHeaders(getExchange(), getHeaders());
            }
        }
    }

    @Override
    protected <T> T getBody(Class<T> type, Object body) {
        if (type == null) {
            throw new IllegalArgumentException("null 'type' argument.");
        }
        if (body == null) {
            return null;
        }
        if (type.isInstance(body)) {
            return type.cast(body);
        }

        TransformerRegistry transformerRegistry = getTransformerRegistry();
        if (transformerRegistry == null) {
            throw new SwitchYardException("Cannot convert from '" + body.getClass().getName() + "' to '" + type.getName() + "'.  No TransformRegistry available.");
        }

        QName fromType = JavaService.toMessageType(body.getClass());
        QName toType = JavaService.toMessageType(type);
        Transformer transformer = transformerRegistry.getTransformer(fromType, toType);
        if (transformer == null) {
            _logger.trace("Falling back type conversion to camel");
            T camelBody = super.getBody(type, body);
            if (camelBody == null) {
                throw new SwitchYardException("Cannot convert from '" + body.getClass().getName() + "' to '" + type.getName() + "'.  No registered Transformer available for transforming from '" + fromType + "' to '" + toType + "'.  A Transformer must be registered.");
            }
            return camelBody;
        }

        Object transformedContent = transformer.transform(body);
        if (transformedContent == null) {
            throw new SwitchYardException("Error converting from '" + body.getClass().getName() + "' to '" + type.getName() + "'.  Transformer '" + transformer.getClass().getName() + "' returned null.");
        }
        if (!type.isInstance(transformedContent)) {
            throw new SwitchYardException("Error converting from '" + body.getClass().getName() + "' to '" + type.getName() + "'.  Transformer '" + transformer.getClass().getName() + "' returned incompatible type '" + transformedContent.getClass().getName() + "'.");
        }

        return type.cast(transformedContent);
    }

    private TransformerRegistry getTransformerRegistry() {
        CamelContext context = getExchange().getContext();
        if (context instanceof SwitchYardCamelContext) {
            return ((SwitchYardCamelContext) context).getServiceDomain().getTransformerRegistry();
        }
        return null;
    }
}
