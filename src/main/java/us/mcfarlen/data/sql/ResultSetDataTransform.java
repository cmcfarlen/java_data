
package us.mcfarlen.data.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.mcfarlen.data.Data;
import us.mcfarlen.data.DataTransform;

/**
 * ResultSet->Data
 */
public class ResultSetDataTransform implements DataTransform<ResultSet,Data> {

   private List<String> getColumnList(ResultSetMetaData mdata) throws SQLException {
      List<String> cl = new ArrayList<>();
      int cnt = mdata.getColumnCount();
      for (int c = 0; c < cnt; c++) {
         cl.add(mdata.getColumnLabel(c));
      }

      return cl;
   }

   @Override
   public Data transform(ResultSet rs) {
      try {
         Data d = Data.list();
         List<String> columns = getColumnList(rs.getMetaData());
         while (rs.next()) {
            Data e = Data.map();
            for (int i = 0; i < columns.size(); i++) {
               e.put(columns.get(i), Data.wrap(rs.getObject(i)));
            }
            d.add(e);
         }
         return d;
      } catch (SQLException ex) {
         Logger.getLogger(ResultSetDataTransform.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }
}
