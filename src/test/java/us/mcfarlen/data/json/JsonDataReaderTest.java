
package us.mcfarlen.data.json;

import us.mcfarlen.data.json.JsonDataReader;
import us.mcfarlen.data.Data;
import org.junit.Test;

/**
 *
 * @author mcfarlen
 */
public class JsonDataReaderTest {

   public JsonDataReaderTest() {
   }

   @Test
   public void testRead() {
      String json = "[1, [2, 3, 4], 3, 4 ]";

      JsonDataReader r = new JsonDataReader();

      Data d = r.read(json);

      assert(d.isList());
      assert(d.asList().get(0).asValue(0) == 1);

      assert(d.asList().get(1).isList());
   }

   @Test
   public void testReadObject() {
      String json = "{ \"a\": \"b\", \"c\": \"d\" }";

      JsonDataReader r = new JsonDataReader();

      Data d = r.read(json);

      assert ("b".equals(d.get("a").asValue("")));
      assert ("d".equals(d.get("c").asValue("")));
   }
}
