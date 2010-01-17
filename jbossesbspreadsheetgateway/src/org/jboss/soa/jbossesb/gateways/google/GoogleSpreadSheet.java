package org.jboss.soa.jbossesb.gateways.google;

import java.net.URL;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;



public class GoogleSpreadSheet implements Job {
	
	protected static SpreadsheetService myService ;

	protected URL metafeedUrl ;

	protected SpreadsheetFeed feed ;
	 
	protected List<SpreadsheetEntry> spreadsheets;

	protected SpreadsheetEntry entry = null;
	
	private String service; 
	private String username; 
	private String password;
	private String feedurl;
	private String documentName; 
	private String lastUpdate;
	
	protected boolean on = false;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFeedurl() {
		return feedurl;
	}

	public void setFeedurl(String feedurl) {
		this.feedurl = feedurl;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	



	public GoogleSpreadSheet() {
		


	}
	
	public void initialize() throws Exception {
		
		myService = new SpreadsheetService(service);
		
		//System.out.println("Initializing Connection between JBoss ESB and Google Docs...");

		myService.setUserCredentials(username,password);


		metafeedUrl = new URL(feedurl);

		feed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);

		spreadsheets = feed.getEntries();
		
		entry = null;
		
		for (int i = 0; i < spreadsheets.size(); i++) {
			
			  entry = spreadsheets.get(i);
			  
			  if (entry.getTitle().getPlainText().equalsIgnoreCase(documentName)) {
				  
	                setLastUpdate(entry.getUpdated().toString());
					  
			 }
		}
		
		
		
	}
	
	public String load() throws Exception {
	
		StringBuilder builderOut = new StringBuilder();
		
		entry = null;

		for (int i = 0; i < spreadsheets.size(); i++) {

			entry = spreadsheets.get(i);

			if (entry.getTitle().getPlainText().equalsIgnoreCase(documentName)) {
				
				List<WorksheetEntry> worksheets = entry.getWorksheets();

				for (int j = 0; j < worksheets.size(); j++) {

					WorksheetEntry worksheet = worksheets.get(j);

					URL listFeedUrl = worksheet.getListFeedUrl();

					ListFeed listFeed = myService.getFeed(listFeedUrl, ListFeed.class);

					if (listFeed.getEntries().size() > 0) {
						
		
						StringBuilder header = new StringBuilder();

						for (String tag : listFeed.getEntries().get(0)
								.getCustomElements().getTags()) {

							header.append(tag + ",");

						}

						builderOut.append(header.toString().substring(0,
								header.lastIndexOf(",")));

					}

					for (ListEntry entrada : listFeed.getEntries()) {

						StringBuilder row = new StringBuilder();

						for (String tag : entrada.getCustomElements().getTags()) {

							row.append(entrada.getCustomElements()
									.getValue(tag)
									+ ",");

						}

						builderOut.append(row.toString().substring(0,
								row.lastIndexOf(",")));

					}
				}

			}

		}
		

		return builderOut.toString();

	}
		
		
	
	
	public  boolean hasChanges() throws Exception {
		
		SpreadsheetFeed feed = myService.getFeed(metafeedUrl, SpreadsheetFeed.class);
		
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		
		
		entry = null;
		
		for (int i = 0; i < spreadsheets.size(); i++) {
			
			  entry = spreadsheets.get(i);
			  
			  if (entry.getTitle().getPlainText().equalsIgnoreCase(documentName)) {
				  
			  if (getLastUpdate().equalsIgnoreCase(entry.getUpdated().toString())) {
				  
				   ////System.out.println(getLastUpdate() + "===========" + entry.getUpdated().toString());
					  
					  return false;
					  
				  } else {
					  
					  this.setLastUpdate(entry.getUpdated().toString());
					  //System.out.println("Changes arriving: " + getLastUpdate() + "===========" + entry.getUpdated().toString());
					  return true;
					  
				  }
			  
				  
			  }
			  
		}
		return false;
		

		
			  
				  
	  
	}
	
	public GoogleSpreadSheet(String service, String username, String password,
			String feedurl, String documentName, String lastUpdate) {

	}

	public String load(String service, String username, String password,
			String feedurl, String documentName, String lastUpdate)
			throws Exception {

		SpreadsheetService myService = new SpreadsheetService(service);

		myService.setUserCredentials(username, password);

		URL metafeedUrl = new URL(feedurl);

		SpreadsheetFeed feed = myService.getFeed(metafeedUrl,
				SpreadsheetFeed.class);

		List<SpreadsheetEntry> spreadsheets = feed.getEntries();

		SpreadsheetEntry entry = null;

		StringBuilder builderOut = new StringBuilder();

		for (int i = 0; i < spreadsheets.size(); i++) {

			entry = spreadsheets.get(i);

			if (entry.getTitle().getPlainText().equalsIgnoreCase(documentName)) {

				//System.out.println("Last Update: " + entry.getUpdated());
				

				List<WorksheetEntry> worksheets = entry.getWorksheets();

				for (int j = 0; j < worksheets.size(); j++) {

					WorksheetEntry worksheet = worksheets.get(j);

					URL listFeedUrl = worksheet.getListFeedUrl();

					ListFeed listFeed = myService.getFeed(listFeedUrl,
							ListFeed.class);

					if (listFeed.getEntries().size() > 0) {

						StringBuilder header = new StringBuilder();

						for (String tag : listFeed.getEntries().get(0)
								.getCustomElements().getTags()) {

							header.append(tag + ",");

						}


					}

					for (ListEntry entrada : listFeed.getEntries()) {

						StringBuilder row = new StringBuilder();

						for (String tag : entrada.getCustomElements().getTags()) {

							row.append(entrada.getCustomElements()
									.getValue(tag)
									+ ",");

						}



					}
				}

			}

		}
		return builderOut.toString();

	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		
		
		try {
			//System.out.println("Has Changes: " + this.hasChanges());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

}
