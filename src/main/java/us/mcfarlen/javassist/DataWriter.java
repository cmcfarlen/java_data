
package us.mcfarlen.javassist;

import java.io.OutputStream;

/**
 *
 */
public interface DataWriter {
   boolean writeData(Data d, OutputStream os);
   String writeToString(Data d);
}
