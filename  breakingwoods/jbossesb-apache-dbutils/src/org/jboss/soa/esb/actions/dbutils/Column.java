package org.jboss.soa.esb.actions.dbutils;

public class Column {

	private String name;
	
	private String dataType;
	
	public Column() {
		// TODO Auto-generated constructor stub
	}
	
	

	public Column(String name, String dataType) {
		super();
		this.name = name;
		this.dataType = dataType;
	}



	@Override
	public String toString() {
		return "Column [dataType=" + dataType + ", name=" + name + "]";
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
