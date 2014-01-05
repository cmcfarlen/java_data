
package us.mcfarlen.data.json;

import us.mcfarlen.data.DataReader;
import us.mcfarlen.data.Data;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class JsonDataReader implements DataReader {
   JsonFactory f = new JsonFactory();

   private Data toData(JsonParser jp) throws IOException {
      JsonToken ntoken = jp.getCurrentToken();
      if (ntoken == JsonToken.START_ARRAY) {
         Data d = Data.list();
         while (JsonToken.END_ARRAY != jp.nextToken()) {
            d.add(toData(jp));
         }
         return d;
      } else if (ntoken == JsonToken.START_OBJECT) {
         Data d = Data.map();
         while (JsonToken.END_OBJECT != jp.nextToken()) {
            String k = jp.getCurrentName();
            jp.nextToken();
            d.put(k, toData(jp));
         }
         return d;
      } else if (ntoken == JsonToken.VALUE_STRING) {
         return Data.data(jp.getValueAsString());
      } else if (ntoken == JsonToken.VALUE_NUMBER_INT) {
         return Data.data(jp.getValueAsLong());
      } else if (ntoken == JsonToken.VALUE_NUMBER_FLOAT) {
         return Data.data(jp.getValueAsDouble());
      } else if (ntoken == JsonToken.VALUE_FALSE ||
                 ntoken == JsonToken.VALUE_TRUE) {
         return Data.data(jp.getValueAsBoolean());
      }
      return null;
   }

   @Override
   public Data read(InputStream is) {
      try {
         JsonParser jp = f.createParser(is);

         jp.nextToken();
         return toData(jp);
      } catch (IOException ex) {
         Logger.getLogger(JsonDataReader.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   @Override
   public Data read(String str) {
      return read(new ByteArrayInputStream(str.getBytes()));
   }
}
