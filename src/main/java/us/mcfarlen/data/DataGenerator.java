
package us.mcfarlen.data;

import java.util.Iterator;

/**
 * Generate some data.
 *
 * Could be used to lazily generate data from some source.
 */
public abstract class DataGenerator implements Iterable<Data> {
   /**
    * This should generate some data each time generate is called or null if no more data can be generated.
    *
    * @return data
    */
   protected abstract Data generate();

   @Override
   public Iterator<Data> iterator() {
      return new Iterator<Data>() {
         Data d = null;

         @Override
         public boolean hasNext() {
            d = generate();
            return d != null;
         }

         @Override
         public Data next() {
            if (d == null) {
               d = generate();
            }
            Data tmp = d;
            d = null;
            return tmp;
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Not supported.");
         }
      };
   }
}
