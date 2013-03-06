/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.switchyard.bus.camel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.switchyard.Exchange;
import org.switchyard.Message;

/**
 * Class to test interoperability between SwitchYard and Camel exchanges.
 */
public class SwitchYardApiTest {

    private static final String PROPERTY_VALUE = "Some dummy value";
    private static final String PROPERTY_KEY = "some key";
    private static final String[] LABELS = new String[] {"one", "two", "three"};

    public Message createMessage() {
//        return new CamelMessage();
        return null;
    }

    public Exchange createExchange() {
//        return new CamelExchange(null, null, null);
        return null;
    }

    @Test
    public void testProperies() {
        
//        Exchange exchange = createExchange();
//        exchange.getContext().setProperty(PROPERTY_KEY, PROPERTY_VALUE).addLabels(LABELS);
//
//        assertEquals(PROPERTY_VALUE, exchange.getContext().getProperty(PROPERTY_KEY));
    }

}
