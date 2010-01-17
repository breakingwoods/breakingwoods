package org.jboss.soa.esb.actions.dbutils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

public class EsbHandler implements ResultSetHandler<ResultData>{

	@Override
	public ResultData handle(ResultSet rs) throws SQLException {
		
        if (!rs.next()) {
            return null;
        }
    
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        ResultData result = new ResultData();
        
        Row row = new Row();
        
        Cell cell = null;

        for (int i = 0; i < cols; i++) {
        	
        	cell = new Cell();
        	cell.setColumn(new Column(meta.getColumnName(i+1), meta.getColumnTypeName(i+1)));
        	
        	cell.setInternalId(i+1);
        	
        	cell.setValue(rs.getObject(i + 1));
        	
        	row.getCells().add(cell);
            
        }
        
        result.getRows().add(row);

        return result;
    }
		
		

}
