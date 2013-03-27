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
package org.switchyard.internal;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Executor service which block execution untill submited task is completed.
 */
public class SynchronousExecutorService extends AbstractExecutorService {

    private boolean _shutdown;

    @Override
    public void shutdown() {
        _shutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.<Runnable>emptyList();
    }

    @Override
    public boolean isShutdown() {
        return _shutdown;
    }

    @Override
    public boolean isTerminated() {
        return _shutdown;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

}
