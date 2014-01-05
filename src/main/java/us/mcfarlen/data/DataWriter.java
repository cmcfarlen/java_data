
package us.mcfarlen.data;

import java.io.OutputStream;

/**
 *
 */
public interface DataWriter {
   boolean writeData(Data d, OutputStream os);
   String writeToString(Data d);
}
