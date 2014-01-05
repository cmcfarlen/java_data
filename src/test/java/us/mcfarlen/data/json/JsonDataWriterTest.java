
package us.mcfarlen.data.json;

import us.mcfarlen.data.json.JsonDataWriter;
import us.mcfarlen.data.Data;
import org.junit.Test;

/**
 *
 * @author mcfarlen
 */
public class JsonDataWriterTest {
   public JsonDataWriterTest() {
   }

   @Test
   public void testWriteToString() {
      JsonDataWriter w = new JsonDataWriter();
      Data d = Data.list(1, 2, 3, 4);
      String s = w.writeToString(d);
      assert("[1,2,3,4]".equals(s));

      d = Data.map("a", Data.list(1, 2.34, false, "str"), "b", "foo");
      s = w.writeToString(d);

      assert("{\"b\":\"foo\",\"a\":[1,2.34,false,\"str\"]}".equals(s));
   }
}
