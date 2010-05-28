package org.jboss.soa.esb.actions;

import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.message.Message;

import twitter4j.Twitter;

public class TwitterAdapter extends AbstractActionPipelineProcessor {
	
	public static final String USER_NAME = "user-name";
	public static final String PASS_WORD = "password";
	public static final String EMBE_CRED = "embedded-credentials";
	
	private String userName;
	private String password;
	private boolean embeddedCredentials;
	
	public TwitterAdapter(ConfigTree configTree) {
		userName = configTree.getAttribute(USER_NAME);
		password = configTree.getAttribute(PASS_WORD);
		embeddedCredentials = Boolean.parseBoolean(configTree.getAttribute(EMBE_CRED));
	}

	public Message process(Message message) throws ActionProcessingException {
		Twitter twitter = null;
		String status = null;
		try {
			if (embeddedCredentials) {
				userName = (String) message.getBody().get(USER_NAME);
				password = (String) message.getBody().get(PASS_WORD);
			}
			status = (String) message.getBody().get();
			twitter = new Twitter(userName, password);
			twitter.updateStatus(status);
		} catch (Exception ex) {
			throw new ActionProcessingException(ex);
		}
		return message;
	}

}