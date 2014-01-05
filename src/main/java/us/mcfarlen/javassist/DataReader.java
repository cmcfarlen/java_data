
package us.mcfarlen.javassist;

import java.io.InputStream;

/**
 *
 */
public interface DataReader {
   Data read(InputStream is);
   Data read(String str);
}
