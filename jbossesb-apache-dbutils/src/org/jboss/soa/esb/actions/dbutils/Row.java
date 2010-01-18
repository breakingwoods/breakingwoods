package org.jboss.soa.esb.actions.dbutils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Row implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4811892056356937052L;
	private List<Cell> cells = new ArrayList<Cell>();
	
	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
	public List<Cell> getCells() {
		return cells;
	}
	@Override
	public String toString() {
		return "Row [cells=" + cells + "]";
	}
	
	
	
	
	


}
