/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
 *
 * (C) 2005-2006, JBoss Inc.
 */

package org.jboss.soa.esb.infinispan.gateways;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.jboss.logging.Logger;

/**
 * Infinispan cache listener
 * Receive cache added event and adds to internal queue for ESB to pickup
 *
 * @author <a href="mailto:noconnor@redhat.com">noconnor@redhat.com</a>
 */

@Listener(sync = false)
public class ESBCacheListener {
    protected final static Logger m_logger = Logger.getLogger(ESBCacheListener.class);
	private LinkedBlockingQueue<Object> myQueue;
	private boolean localOnlyFlag;

	public ESBCacheListener(LinkedBlockingQueue<Object> targetQueue,boolean localOnly) {
		myQueue = targetQueue;
	}

	@CacheEntryCreated
	public void EntryAdded(CacheEntryCreatedEvent event) {
		//only notify when the value is actually added
		if (!event.isPre()) {
			
			//If only interested in local cache additions
			if (localOnlyFlag){
				if (!event.isOriginLocal())
					return;
			}
			
			m_logger.debug(this.toString() + "  New entry " + event.getKey() + " created in the cache");
			try {
				myQueue.offer(event.getKey(), 10, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}