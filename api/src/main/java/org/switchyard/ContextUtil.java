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
package org.switchyard;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for context's management.
 */
public class ContextUtil {

    private ContextUtil() { }

    /**
     * Merge exchange and message properties.
     *
     * @param contexts Contexts to be merged.
     * @return Properties from both contexts.
     */
    public static Set<Property> properties(Context ... contexts) {
        Set<Property> props = new HashSet<Property>();
        for (Context context : contexts) {
            props.addAll(context.getProperties());
        }
        return props;
    }

}
