package org.jboss.soa.esb.actions.dbutils;

import java.io.Serializable;

public class Cell implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5102071342547932055L;

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Integer getInternalId() {
		return internalId;
	}

	public void setInternalId(Integer internalId) {
		this.internalId = internalId;
	}
	
	

	@Override
	public String toString() {
		return "Cell [column=" + column + ", internalId=" + internalId
				+ ", value=" + value + "]";
	}



	private Column column;
	
	private Object value;
	
	private Integer internalId;

}
