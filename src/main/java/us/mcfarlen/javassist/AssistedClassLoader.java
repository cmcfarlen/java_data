
package us.mcfarlen.javassist;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.NotFoundException;

/**
 *
 */
public class AssistedClassLoader extends ClassLoader {
   private final ClassGenerator gen;
   private final Map<String,Class<?>> generatedClasses;

   public AssistedClassLoader(ClassGenerator gen, ClassLoader parent) {
      super(parent);
      this.generatedClasses = new HashMap<>();
      this.gen = gen;
   }

   @Override
   public Class<?> loadClass(String name) throws ClassNotFoundException {
      try {
         return super.loadClass(name); //To change body of generated methods, choose Tools | Templates.
      } catch (ClassNotFoundException ex) {
         Class<?> cls = generatedClasses.get(name);
         if (cls == null) {
            throw ex;
         }
         return cls;
      }
   }

   public String generate(Class<?> iface) {
      try {
         String gname = iface.getCanonicalName() + "Impl";
         byte[] gc = gen.generate(iface, gname);

         Class<?> gcls = defineClass(gname, gc, 0, gc.length);

         generatedClasses.put(gname, gcls);

         return gname;
      } catch (NotFoundException | CannotCompileException | IOException ex) {
         Logger.getLogger(AssistedClassLoader.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

}
