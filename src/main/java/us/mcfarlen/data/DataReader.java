
package us.mcfarlen.data;

import java.io.InputStream;

/**
 *
 */
public interface DataReader {
   Data read(InputStream is);
   Data read(String str);
}
