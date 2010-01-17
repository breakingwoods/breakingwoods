package org.jboss.soa.esb.actions.dbutils;

import java.util.ArrayList;
import java.util.List;

public class ResultData implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public List<Row> rows = new ArrayList<Row>();
	
	public ResultData() {
		
	}
	


	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}



	@Override
	public String toString() {
		return "ResultData [getRows()=" + getRows() + "]";
	}
	
	
	

}
