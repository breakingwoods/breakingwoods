package org.jboss.soa.esb.infinispan.gateways;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.logging.Logger;
import org.jboss.soa.esb.ConfigurationException;
import org.jboss.soa.esb.Service;
import org.jboss.soa.esb.client.ServiceInvoker;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.listeners.ListenerTagNames;
import org.jboss.soa.esb.listeners.lifecycle.AbstractThreadedManagedLifecycle;
import org.jboss.soa.esb.listeners.lifecycle.ManagedLifecycleException;
import org.jboss.soa.esb.listeners.message.MessageDeliverException;
import org.jboss.soa.esb.message.Message;
import org.jboss.soa.esb.message.format.MessageFactory;

/**
 * Infinispan cache gateway
 * 
 * 
 * @author <a href="mailto:noconnor@redhat.com">noconnor@redhat.com</a>
 */
public class InfinispanGateway extends AbstractThreadedManagedLifecycle {

	protected final static Logger m_logger = Logger.getLogger(InfinispanGateway.class);

	private static final long serialVersionUID = -2529098365009491054L;
	private ConfigTree listenerConfig;
	private Service service;
	private ServiceInvoker serviceInvoker;
	private String cacheName;
	private String JNDIName;
	private DefaultCacheManager dcManager;
	private Cache<?, ?> targetCache;
	private LinkedBlockingQueue<Object> itemQueue;
	private ESBCacheListener cacheListener;
	private boolean localOnly;

	public InfinispanGateway(final ConfigTree config) throws ConfigurationException {
		super(config);
		this.listenerConfig = config;
		localOnly = false;

		String serviceCategory = listenerConfig.getRequiredAttribute(ListenerTagNames.TARGET_SERVICE_CATEGORY_TAG);
		String serviceName = listenerConfig.getRequiredAttribute(ListenerTagNames.TARGET_SERVICE_NAME_TAG);
		JNDIName = listenerConfig.getRequiredAttribute("JNDIName");
		cacheName = listenerConfig.getAttribute("TargetCacheName");
		localOnly = listenerConfig.getBooleanAttribute("LocalOnly", false);

		service = new Service(serviceCategory, serviceName);
	}

	protected void doInitialise() throws ManagedLifecycleException {
		// Create the ServiceInvoker instance for the target service....
		try {
			serviceInvoker = new ServiceInvoker(service);
		} catch (MessageDeliverException e) {
			throw new ManagedLifecycleException("Failed to create ServiceInvoker for Service '" + service + "'.");
		}

		// retrieve the jndi binding
		try {
			InitialContext ctx2;
			ctx2 = new InitialContext();
			dcManager = (DefaultCacheManager) ctx2.lookup(JNDIName);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if ((cacheName == null) || (cacheName.isEmpty()))
			targetCache = dcManager.getCache();
		else
			targetCache = dcManager.getCache(cacheName);

		itemQueue = new LinkedBlockingQueue<Object>();

		cacheListener = new ESBCacheListener(itemQueue, localOnly);

		Set<?> y = targetCache.getListeners();
		m_logger.debug("Cache has " + y.size() + " listeners before attaching InfinispanListner");

		targetCache.addListener(cacheListener);
	}

	protected void doRun() {
		while (isRunning()) {
			// Wait for a message....
			Object payloadObject = null;
			try {
				payloadObject = waitForPayload();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}

			// Send the message to the target service's Action Pipeline via
			// the ServiceInvoker...
			if (payloadObject != null) {
				try {
					Message esbMessage = MessageFactory.getInstance().getMessage();

					Object cacheObject = targetCache.get(payloadObject);

					if (cacheObject != null) {
						esbMessage.getBody().add("CacheKey", payloadObject);
						esbMessage.getBody().add("CacheValue", cacheObject);
						serviceInvoker.deliverAsync(esbMessage);
					} else
						m_logger.warn("Null cache object...not sending ESB message");

				} catch (MessageDeliverException e) {
					e.printStackTrace();
				}
			}
		}
		m_logger.info("doRun exiting");

		// Tidy up
		targetCache.removeListener(cacheListener);
		itemQueue.clear();
	}

	private Object waitForPayload() throws InterruptedException {
		Object retObj = itemQueue.poll(10, TimeUnit.MILLISECONDS);
		return retObj;
	}
}
