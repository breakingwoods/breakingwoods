package org.jboss.soa.esb.infinispan.gateways;

import org.jboss.soa.esb.ConfigurationException;
import org.jboss.soa.esb.Service;
import org.jboss.soa.esb.client.ServiceInvoker;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.listeners.ListenerTagNames;
import org.jboss.soa.esb.listeners.lifecycle.AbstractThreadedManagedLifecycle;
import org.jboss.soa.esb.listeners.lifecycle.ManagedLifecycleException;

public class InfinispanGateway extends AbstractThreadedManagedLifecycle {

	private static final long serialVersionUID = 1L;
	
	private ConfigTree listenerConfig;
    private Service service;
    private ServiceInvoker serviceInvoker;

	protected InfinispanGateway(ConfigTree config) throws ConfigurationException {
		super(config);
        this.listenerConfig = config;
 
        String serviceCategory = listenerConfig.getRequiredAttribute(ListenerTagNames.TARGET_SERVICE_CATEGORY_TAG);
        String serviceName = listenerConfig.getRequiredAttribute(ListenerTagNames.TARGET_SERVICE_NAME_TAG);
 
        service = new Service(serviceCategory, serviceName);

        
	}

	@Override
	protected void doRun() {
	}

	@Override
	protected void doInitialise() throws ManagedLifecycleException {
		
	}
	
	

}
