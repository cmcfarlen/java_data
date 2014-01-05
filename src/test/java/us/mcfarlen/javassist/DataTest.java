package us.mcfarlen.javassist;

import static junit.framework.Assert.*;
import org.junit.Test;

/**
 *
 */
public class DataTest {

   @Test
   public void testIsMap() {
      assertTrue(Data.map("a", 42, "b", "foo").isMap());
   }

   @Test
   public void testIsList() {
      assertTrue(Data.list("a", 42, "b", "foo").isList());
   }

   @Test
   public void testAsMap() {
      Data d = Data.map("a", 42);
      int v = d.get("a").asValue(53);
      assertEquals(42, v);
   }

   @Test
   public void testAsList() {
      assertEquals(42, Data.list(42).asList().get(0).toInt());
      assertEquals(42, Data.data(42).asList().get(0).toInt());
   }

   @Test
   public void testPut() {
      Data d = Data.map();
      d.put("a", Data.data(42));
      int v = d.get("a").asValue(53);
      assertEquals(42, v);
   }

   @Test
   public void testEquality() {
      Data d1 = Data.data(42);
      Data d2 = Data.data(42);
      Data d3 = Data.data(43);

      assertEquals(d1, d2);
      assert(!d2.equals(d3));
   }

   @Test
   public void testAdd() {
      Data d = Data.list(1, 2, 3, 4);

      d.add(5);

      assert(d.isList());
      assert(d.asList().size() == 5);
   }

   @Test
   public void testGetIn() {
      Data d = Data.list(1, Data.list(2, 3, 4), 5, 6, 7);

      System.out.println(d);
      assert(d.getIn(1, 2).asValue(0) == 4);

      d = Data.map("a", Data.list(Data.map("n", "bob"), Data.map("n", "jon"), Data.map("n", "fred")));
      System.out.println(d);
      assert(d.getIn("a", 1, "n").asValue("").equals("jon")) : "Wrong data got: " + d.getIn("a", 1, "n");
   }

   @Test
   public void testUpdateIn() {
      Data d = Data.map("a", Data.list(Data.map("n", "bob"), Data.map("n", "jon"), Data.map("n", "fred")));

      d.updateIn(Data.data("sue"), "a", 1, "n");

      assert(d.getIn("a", 1, "n").asValue("").equals("sue")) : "Wrong data got: " + d.getIn("a", 1, "n");
   }

   @Test
   public void testSet() {
      Data d = Data.data("bob");

      d.set(Data.data("sue"));

      assert(d.asValue("").equals("sue"));
   }

   @Test
   public void testIsValue() {
      Data d = Data.data(0);
      assert(d.isValue());
   }

   @Test
   public void testAsValue() {
      Data d = Data.data(42);
      assert(d.asValue(0) == 42);
      assert(d.toInt() == 42);

      d = Data.list();
      assert(d.asValue(0) == 0);
   }

   @Test
   public void testToString() {
      Data d = Data.map("a", Data.list(1, 2, 3, 4), "b", "string", "c", 42);
      System.out.println(d);
   }

   @Test
   public void testToBoolean() {
      assert(Data.data(true).isBoolean());
      assert(Data.data(0).toBoolean() == false);
      assert(Data.data(1).toBoolean() == true);
      assert(Data.data("true").toBoolean() == true);
      assert(Data.data("false").toBoolean() == false);
   }
}
