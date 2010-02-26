package org.jboss.soa.esb.listeners.gateway.camel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.impl.DefaultCamelContext;
import org.jboss.soa.esb.ConfigurationException;
import org.jboss.soa.esb.Service;
import org.jboss.soa.esb.client.ServiceInvoker;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.listeners.lifecycle.AbstractThreadedManagedLifecycle;
import org.jboss.soa.esb.listeners.lifecycle.ManagedLifecycleException;
import org.jboss.soa.esb.listeners.message.MessageDeliverException;
import org.jboss.soa.esb.message.Message;
import org.jboss.soa.esb.message.format.MessageFactory;

/**
 * Apache Camel Listener for JBoss ESB
 * 
 * @author esilva - edgarsilva@gmail.com
 * 
 *         Supported on: JBoss ESB 4.7
 * 
 *         Warranty Note: This is an extension only, if you want to use this
 *         component into JBoss SOA Platform official release, it might not be
 *         supportable by Red Hat Support Department - Red Hat GSS - Global Support Services 
 * 
 * 
 * 
 * */

public class ApacheCamelListener extends AbstractThreadedManagedLifecycle {

	private ConfigTree listenerConfig;
	private Service service;
	private ServiceInvoker serviceInvoker;
	private String fromProtocolURI;
	private static final String PROTOCOL_URI_TAG = "protocol-uri";
	private static final String DESTINATION_CATEGORYNAME_TAG = "destination-category";
	private static final String DESTINATION_SERVICENAME_TAG = "destination-name";
	
	private static final String APACHE_CAMEL_FILE_URI_PROTOCOL = "file:";
	
	private static final String APACHE_CAMEL_IRC_URI_PROTOCOL = "irc:";
	
	
	
	
	
	CamelContext camelContext = new DefaultCamelContext();

	public ApacheCamelListener(final ConfigTree config)
			throws ConfigurationException {
		
		

		super(config);
		
		this.listenerConfig = config;

		String serviceCategory = listenerConfig
				.getRequiredAttribute(DESTINATION_CATEGORYNAME_TAG);
		
		String serviceName = listenerConfig
				.getRequiredAttribute(DESTINATION_SERVICENAME_TAG);
		
		fromProtocolURI = listenerConfig.getRequiredAttribute(PROTOCOL_URI_TAG);

		service = new Service(serviceCategory, serviceName);

	}

	@Override
	protected void doRun() {

	}
	
	@Override
	protected void doStop() throws ManagedLifecycleException {
		super.doStop();
		
		try {
			camelContext.stop();
		} catch (Exception e) {
			throw new ManagedLifecycleException(e);
		}
		
		
	}

	@Override
	protected void doInitialise() throws ManagedLifecycleException {

		try {
			
			System.out.println("Invocando de " + fromProtocolURI);

			serviceInvoker = new ServiceInvoker(service);

			try {
				camelContext.addRoutes(new RouteBuilder() {

		    	    public void configure() {
		    	    	
		    	    	from(fromProtocolURI).process(new Processor() {
		    	    		
		    	    		  public void process(Exchange exchange) throws Exception {
		    	    			  
		    	    			  System.out.println("Processando!");
		    	    			  
		    	    			if (fromProtocolURI.startsWith( ApacheCamelListener.APACHE_CAMEL_FILE_URI_PROTOCOL  )) {  
		    	    		
		    	    			GenericFile body =  (GenericFile) exchange.getIn().getBody();
		    	    		    
		    	    		    System.out.println(body.getBody()  );
		    	    		    
		    	    		    System.out.println(readTextFile(body.getBody().toString()));
		    	    		    
		    	    		    publishMessageToESB(readTextFile(body.getBody().toString()));
		    	    		    
		    	    			} else if (fromProtocolURI.startsWith(APACHE_CAMEL_IRC_URI_PROTOCOL)) {
		    	    				
			    	    			Object body =  exchange.getIn().getBody();

			    	    			publishMessageToESB(body);
		    	    				
		    	    				
		    	    				
		    	    			}
		    	    		    
		    	    		    
		    	    		  }
		    	    		});

		    	    }
		    	});
			} catch (Exception e) {

				e.printStackTrace();
			}
			
			camelContext.start();

		} catch (MessageDeliverException e) {
			throw new ManagedLifecycleException(
					"Failed to create ServiceInvoker for Service '" + service
							+ "'.");
		}
		
		catch (Exception e) {
			throw new ManagedLifecycleException(
					"Problem in Camel Layer, check the following error: " + e.getMessage() );
		}

	}
    public static String readTextFile(String file) throws IOException 
    {
        
    	File f = new File(file);
    	StringBuffer sb = new StringBuffer(1024);
        BufferedReader reader = new BufferedReader(new FileReader(f.getPath()));
        char[] chars = new char[1];
        while( (reader.read(chars)) > -1){
            sb.append(String.valueOf(chars)); 
            chars = new char[1];
        }
        reader.close();
        return sb.toString();
    }
    
    private void publishMessageToESB(Object object) {
        Message esbMessage = MessageFactory.getInstance().getMessage();
 
   try {
        
                        esbMessage.getBody().add(object);
                        
                } catch (Exception e) {
                        
                        e.printStackTrace();
                }
   try {
                        serviceInvoker.deliverAsync(esbMessage);
                        
                } catch (MessageDeliverException e) {
                        
                        e.printStackTrace();
                }
}


}
