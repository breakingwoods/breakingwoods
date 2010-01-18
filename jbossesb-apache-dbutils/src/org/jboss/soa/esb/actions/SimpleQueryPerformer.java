package org.jboss.soa.esb.actions;


import java.sql.SQLException;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.jboss.soa.esb.actions.dbutils.EsbHandler;
import org.jboss.soa.esb.actions.dbutils.ResultData;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.message.Message;

/**
 * This is the Action that executes a simple SQL and result the data that can be converted to any other format 
 * @author esilva - edgar.silva@gmail.com 
 * 
 * */

public class SimpleQueryPerformer extends AbstractActionPipelineProcessor {
	
	protected ConfigTree tree;
	protected String sql;
	protected String paramsLocation;
	protected String datasource;
	
	protected ResultData resultData ;
	
	protected DataSource datasourceObject = null;
	
	protected Logger log;
	
	public SimpleQueryPerformer(ConfigTree config) {
		
		log = Logger.getLogger(this.getClass().getName());
		
		tree = config;
		
		sql = tree.getAttribute("sql");
		
		paramsLocation = tree.getAttribute("paramsLocation");
		
		datasource = tree.getAttribute("datasource");
		
		try {
			Context ctx = new InitialContext();
			
			datasourceObject = (DataSource) ctx.lookup(datasource);
			
		} catch (NamingException e) {
			
			e.printStackTrace();
		}
		
		
		
	}
	
	public Message execute(Message message){

		EsbHandler h = new EsbHandler();
		
		ResultData result = null ;

		QueryRunner run = new QueryRunner(datasourceObject);

		try {
			
			if (null == paramsLocation) {
				
				result = run.query(sql, h);
				
			} else {
			
			result = run.query(sql, h, paramsLocation);
			
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
		}

		
		message.getBody().add(result);
		
		return message;
		
		
	}

	@Override
	public Message process(Message arg0) throws ActionProcessingException {
		// TODO Auto-generated method stub
		return execute(arg0);
	}
	
	

}  
