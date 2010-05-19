package org.jboss.soa.esb.infinispan.actions;

import java.io.IOException;
import java.util.HashMap;

import org.infinispan.Cache;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.soa.esb.actions.AbstractActionPipelineProcessor;
import org.jboss.soa.esb.actions.ActionProcessingException;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.message.Body;
import org.jboss.soa.esb.message.Message;

public class InfinispanAdpater extends AbstractActionPipelineProcessor {

	public static final String CACHE_NAME = "cache-name";
    public static final String INFINISPAN_CONFIG_FILE = "infinispan-config-file";
    public static final String KEY_PARAMETER = "key-parameter";
    public static final String VALUE_PARAMETER = "value-parameter";
    
    private String cacheName;
    private String infinispanConfigFile;
    private String key;
    private String value;
	
	public InfinispanAdpater(ConfigTree configTree) {
		cacheName = configTree.getAttribute(CACHE_NAME);
		infinispanConfigFile = configTree.getAttribute(INFINISPAN_CONFIG_FILE);
		key = configTree.getAttribute(KEY_PARAMETER);
		value = configTree.getAttribute(VALUE_PARAMETER);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Message process(Message message) throws ActionProcessingException {
	try {
		
		CacheManager cacheManager = new DefaultCacheManager(infinispanConfigFile);
		Cache<Object, Object> cache = cacheManager.getCache(cacheName);
		cache.start();
		
		HashMap<String,Object> objects = (HashMap<String, Object>) message.getBody().get();
		cache.put(objects.get(key), objects.get(value));
		
		cacheManager.stop();
		
	} catch (Exception e) {
		throw new ActionProcessingException(e);
	}
		return message;
	}

}
