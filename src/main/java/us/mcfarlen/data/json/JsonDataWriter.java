
package us.mcfarlen.data.json;

import us.mcfarlen.data.Data;
import us.mcfarlen.data.DataWriter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class JsonDataWriter implements DataWriter {
   private final JsonFactory f = new JsonFactory();

   private void outputData(Data d, JsonGenerator jg) throws IOException {
      if (d.isList()) {
         List<Data> dl = d.asList();
         jg.writeStartArray();
         for (Data dd: dl) {
            outputData(dd, jg);
         }
         jg.writeEndArray();
      } else if (d.isMap()) {
         Map<String, Data> dm = d.asMap();
         jg.writeStartObject();
         for (Map.Entry<String,Data> e: dm.entrySet()) {
            jg.writeFieldName(e.getKey());
            outputData(e.getValue(), jg);
         }
         jg.writeEndObject();
      } else if (d.isValue()) {
         if (d.isBoolean()) {
            jg.writeBoolean(d.toBoolean());
         } else if (d.isDouble()) {
            jg.writeNumber(d.toDouble());
         } else if (d.isFloat()) {
            jg.writeNumber(d.toFloat());
         } else if (d.isLong()) {
            jg.writeNumber(d.toLong());
         } else if (d.isInteger()) {
            jg.writeNumber(d.toInt());
         } else if (d.isString()) {
            jg.writeString(d.asValue(""));
         }
      }
   }

   @Override
   public boolean writeData(Data d, OutputStream os) {
      try {
         JsonGenerator jg = f.createGenerator(os);

         outputData(d, jg);

         jg.flush();

         return true;
      } catch (IOException ex) {
         Logger.getLogger(JsonDataWriter.class.getName()).log(Level.SEVERE, null, ex);
      }
      return false;
   }

   @Override
   public String writeToString(Data d) {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      if (writeData(d, os)) {
         return os.toString();
      }
      return null;
   }

}
