package us.mcfarlen.javassist;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Data is a simple variant class.  The value it holds can be a single value
 * (number, string, boolean), a List<Data> or a Map<String,Data>.  Since lists
 * and maps are available, a Data can represent a heirarchy of values.
 *
 * Data is modeled somewhat on clojure data concepts, but Data is mutable.  This
 * is mostly because I don't have structure sharing containers like clojure and
 * it would be expensive to create deep copies for every update.
 *
 * Data should provide enough information to output to various document or binary
 * formats, but does not concern itself directly with any sort of serialization.
 *
 * Data ends up dumping a lot of type safety for convenience.
 */
public class Data {
   private Object o;

   private Data(Object o) {
      this.o = o;
   }

   public boolean isValue() {
      return (o != null && (!isMap() && !isList()));
   }

   public boolean isMap() {
      return (o != null && (o instanceof Map));
   }

   public boolean isList() {
      return (o != null && (o instanceof List));
   }

   public boolean isValid() {
      return o != null;
   }

   public boolean isString() {
      return o != null && o instanceof String;
   }

   public boolean isInteger() {
      return o != null && o instanceof Integer;
   }

   public boolean isLong() {
      return o != null && o instanceof Long;
   }

   public boolean isFloat() {
      return o != null && o instanceof Float;
   }

   public boolean isNumber() {
      return o != null && o instanceof Number;
   }

   public boolean isDouble() {
      return o != null && o instanceof Double;
   }

   public boolean isBoolean() {
      return o != null && o instanceof Boolean;
   }

   /**
    * @return The number of elements if a collection or 1 if a valid value.
    */
   public int size() {
      if (isList()) {
         return asList().size();
      } else if (isMap()) {
         return asMap().size();
      } else if (o != null) {
         return 1;
      }
      return 0;
   }

   public Map<String, Data> asMap() {
      if (!isMap()) {
         // can't coerce to a map
         o = new HashMap<>();
      }
      return (Map<String,Data>)o;
   }

   public List<Data> asList() {
      if (!isList()) {
         List<Data> l = new ArrayList<>();
         l.add(new Data(o));
         o = l;
      }
      return (List<Data>)o;
   }

   /**
    * Coerce this Data to a specific type providing a default value.
    *
    * @param def The default value to use if this Data cannot be coerced.
    * @return A value or def.
    */
   public <T> T asValue(T def) {
      if (!isValue()) {
         // probably a logic error
         if (isList()) {
            List<Data> l = asList();
            if (!l.isEmpty()) {
               o = l.get(0).o;
            } else {
               o = def;
            }
         } else {
            Map<String, Data> m = asMap();
            if (!m.isEmpty()) {
               o = m.values().iterator().next();
            } else {
               o = def;
            }
         }
      }
      if (o == null) {
         o = def;
      }
      return coerce(def.getClass(), def);
   }

   /**
    * Convenience for asMap().put(...)
    */
   public Data put(String key, Data v) {
      asMap().put(key, v);
      return this;
   }

   /**
    * Convenience for asMap().put(...)
    */
   public <T> Data put(String key, T v) {
      asMap().put(key, new Data(v));
      return this;
   }

   /**
    * Convenience for asMap().get(...)
    */
   Data get(String key) {
      return asMap().get(key);
   }

   /**
    * Traverses this Data using a given sequence of keys and returns the resulting Data object.
    * This method will create missing Data elements on the way to the end of the sequence.
    *
    * elist can contain integers or strings.  If an integer is encountered, the data element
    * will be treated as a list.  If a string is encounter, as a map.  If the element specified
    * by that key doesn't exist, it will be created.
    *
    * @param elist
    * @return The data object.
    */
   Data getIn(Object... elist) {
      Data d = this;
      for (Object o: elist) {
         if (o instanceof String) {
            String k = (String)o;
            Data tmp = d.asMap().get(k);
            if (tmp == null) {
               tmp = new Data(null);
               d.put(k, tmp);
            }
            d = tmp;
         } else if (o instanceof Integer) {
            int idx = (Integer)o;
            List<Data> dl = d.asList();
            if (dl.size() > idx) {
               d = dl.get(idx);
            } else {
               for (int i = dl.size(); i <= idx; i++) {
                  d = new Data(null);
                  dl.add(d);
               }
            }
         } else {
            throw new RuntimeException("only strings or integers allowed in getIn");
         }
      }
      return d;
   }

   /**
    * Like getIn, but set the value of the resulting data to be that of the given value
    * @param v
    * @param elist
    * @return The data after it is set to v (so essentially v).
    */
   Data updateIn(Data v, Object... elist) {
      Data d = getIn(elist);
      return d.set(v);
   }

   /**
    * Convenience for asList().add(...)
    */
   public Data add(Data v) {
      asList().add(v);
      return this;
   }

   /**
    * Convenience for asList().add(...)
    */
   public <T> Data add(T v) {
      asList().add(new Data(v));
      return this;
   }

   /**
    * Assigns this value to be the same value as v.
    */
   public Data set(Data v) {
      o = v.o;
      return this;
   }

   @Override
   public String toString() {
      if (isValue()) {
         return o.toString();
      }
      if (isMap()) {
         boolean first = true;
         StringBuilder bld = new StringBuilder();
         bld.append("{");
         for (Map.Entry<String, Data> e: asMap().entrySet()) {
            if (first) {
               first = false;
            } else {
               bld.append(", ");
            }
            bld.append(e.getKey()).append(":").append(e.getValue());
         }
         return bld.append("}").toString();
      }
      if (isList()) {
         return "[" + Joiner.on(", ").join(asList()) + "]";
      }
      return null;
   }

   public static <T> Data data(T v) {
      return new Data(v);
   }

   /**
    * Construct a list Data
    */
   public static Data list(Object... data) {
      List<Data> l = new ArrayList<>();
      for (Object t: data) {
         if (t instanceof Data) {
            l.add((Data)t);
         } else {
            l.add(new Data(t));
         }
      }
      return new Data(l);
   }

   /**
    * Construct a map data.  This requires an even number of elements in the data array.
    * (i.e. key1, value1, key2, value2, ...)
    */
   public static Data map(Object... data) {
      if (data.length % 2 != 0) {
         throw new RuntimeException("Map requires pairs of elements");
      }
      Map<String, Data> m = new HashMap<>();
      for (int i = 0; i < data.length; i += 2) {
         Object o = data[i+1];
         Data d;
         if (o instanceof Data) {
            d = (Data)o;
         } else {
            d = new Data(o);
         }
         m.put((String)data[i], d);
      }
      return new Data(m);
   }

   public int toInt() {
      return coerce(Integer.class, 0);
   }

   public long toLong() {
      return coerce(Integer.class, 0l);
   }

   public float toFloat() {
      return coerce(Float.class, 0f);
   }

   public double toDouble() {
      return coerce(Double.class, 0.0);
   }

   public boolean toBoolean() {
      return coerce(Boolean.class, Boolean.FALSE);
   }

   public <T> T coerce(Class<?> aClass, T def) {
      Object t = o;

      if (String.class.equals(aClass)) {
         return (T)t.toString();
      }
      if (Integer.class.equals(aClass)) {
         if (t instanceof Number) {
            return (T) new Integer(((Number)t).intValue());
         } else {
            return (T) new Integer(o.toString());
         }
      }
      if (Double.class.equals(aClass)) {
         if (t instanceof Number) {
            return (T) new Double(((Number)t).doubleValue());
         } else {
            return (T) new Double(o.toString());
         }
      }
      if (Float.class.equals(aClass)) {
         if (t instanceof Number) {
            return (T) new Float(((Number)t).doubleValue());
         } else {
            return (T) new Float(o.toString());
         }
      }
      if (Long.class.equals(aClass)) {
         if (t instanceof Number) {
            return (T) new Long(((Number)t).longValue());
         } else {
            return (T) new Long(o.toString());
         }
      }
      if (Boolean.class.equals(aClass)) {
         if (t instanceof Boolean) {
            return (T)o;
         } else if (t instanceof String) {
            return (T) new Boolean(Boolean.parseBoolean((String)t));
         } else if (t instanceof Number) {
            int v = ((Number)t).intValue();
            return (T) new Boolean(v != 0);
         }
      }

      return def;
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 61 * hash + Objects.hashCode(this.o);
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Data other = (Data) obj;
      return Objects.equals(this.o, other.o);
   }
}
