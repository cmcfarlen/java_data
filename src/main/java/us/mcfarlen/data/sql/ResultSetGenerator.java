
package us.mcfarlen.data.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.mcfarlen.data.Data;
import us.mcfarlen.data.DataGenerator;

/**
 *
 */
public class ResultSetGenerator extends DataGenerator {
   private final ResultSet rs;
   private final List<String> columns;

   public ResultSetGenerator(ResultSet rs) {
      this.rs = rs;
      this.columns = getColumnList(rs);
   }

   @Override
   protected Data generate() {
      try {
         Data d = null;
         if (rs.next()) {
            d = Data.map();
            for (int i = 0; i < columns.size(); i++) {
               d.put(columns.get(i), Data.wrap(rs.getObject(i)));
            }
         }
         return d;
      } catch (SQLException ex) {
         Logger.getLogger(ResultSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   private List<String> getColumnList(ResultSet rs) {
      List<String> cl = new ArrayList<>();
      try {
         ResultSetMetaData mdata = rs.getMetaData();
         int cnt = mdata.getColumnCount();
         for (int c = 0; c < cnt; c++) {
            cl.add(mdata.getColumnLabel(c));
         }
      } catch (SQLException ex) {
         Logger.getLogger(ResultSetGenerator.class.getName()).log(Level.SEVERE, null, ex);
      }

      return cl;
   }
}
