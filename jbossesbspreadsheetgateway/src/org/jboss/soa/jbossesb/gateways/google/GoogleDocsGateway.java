package org.jboss.soa.jbossesb.gateways.google;



import java.util.Timer;
import java.util.TimerTask;

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
 *  GoogleSpreadSheets Gateway Class  
 *  
 * @author esilva  */


public class GoogleDocsGateway extends AbstractThreadedManagedLifecycle  {
	
	  private ConfigTree listenerConfig;
	  private Service service;
	  private ServiceInvoker serviceInvoker;
	  
	  private GoogleSpreadSheet sheet = new GoogleSpreadSheet();
	  
	  protected boolean firstExecution = true ;
	  
	  private int pollingtime = 0;
	  

	public GoogleDocsGateway(ConfigTree config) throws ConfigurationException {
		super(config);
		
		this.listenerConfig = config;
		 
        String serviceCategory = listenerConfig.getRequiredAttribute(ListenerTagNames.TARGET_SERVICE_CATEGORY_TAG);
        
        String serviceName = listenerConfig.getRequiredAttribute(ListenerTagNames.TARGET_SERVICE_NAME_TAG);
 
        service = new Service(serviceCategory, serviceName);
        
        sheet.setService(config.getAttribute("servicename"));
        
        sheet.setUsername(config.getAttribute("username"));
        
        sheet.setPassword(config.getAttribute("password"));
        
        sheet.setFeedurl(config.getAttribute("feedurl"));
        
        sheet.setDocumentName(config.getAttribute("documentname"));
        
        pollingtime = Integer.valueOf(config.getAttribute("polling-in-seconds"));
        
        

	}  
	  
	@Override
	protected void doRun() {
		
		    publishMessageToESB(); 

			int delay = 10000;   
    	    int period = pollingtime * 1000;  // repeat every sec.
    	    Timer timer = new Timer();
    	    
    	    timer.scheduleAtFixedRate(new TimerTask() {
    	            public void run() {
    	            	
    	            	try {
							if (sheet.hasChanges() ) {
								
								System.out.println("Changes found, publishing the message again");
								
								publishMessageToESB();
								
								
							} 
							
							else {
								
								System.out.println("No updates found");
								
							}
    	            		
    	            		
							
							
						} catch (Exception e) {
							
							e.printStackTrace();
						}
    	            	
    	            	
    	            }
    
    	        }, delay, period);
			
	            
	            }

	private void publishMessageToESB() {
		Message esbMessage = MessageFactory.getInstance().getMessage();
   	 
           try {
            	
				esbMessage.getBody().add(sheet.load());
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
           try {
				serviceInvoker.deliverAsync(esbMessage);
				
			} catch (MessageDeliverException e) {
				
				e.printStackTrace();
			}
	}
	                
		
	
	

	@Override
	protected void doInitialise() throws ManagedLifecycleException {
		
		 try {
	            serviceInvoker = new ServiceInvoker(service);
	            
	            sheet.initialize();
	            
	            
	        } catch (MessageDeliverException e) {
	        	
	            throw new ManagedLifecycleException("Failed to create ServiceInvoker for Service listening Google Service: " + service + "'.");
	        }
	        
	        catch (Exception e) {
	 
	        	throw new ManagedLifecycleException(e);
	        }
		
	}


}
