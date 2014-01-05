
package yinst.yahoo.javassist_test;

import java.io.IOException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 *
 */
public class ClassGenerator {

   private CtField findField(CtClass cls, String name, CtClass type) throws CannotCompileException {
      try {
         return cls.getField(name);
      } catch (NotFoundException ex) {
         CtField f = CtField.make("private " + type.getSimpleName() + " " + name + ";", cls);
         cls.addField(f);
         return f;
      }
   }

   public byte[] generate(Class iface, String gname) throws NotFoundException, CannotCompileException, IOException {
      ClassPool cpool = ClassPool.getDefault();

      CtClass iface_cls = cpool.get(iface.getCanonicalName());
      CtClass impl_cls = cpool.makeClass(gname);

      impl_cls.addInterface(iface_cls);

      for (CtMethod m: iface_cls.getMethods()) {
         if (m.getDeclaringClass() == iface_cls) {
            String mname = m.getName();
            if (mname.startsWith("get")) {
               String fname = mname.substring(3).toLowerCase();
               CtField f = findField(impl_cls, fname, m.getReturnType());
               CtMethod om = CtNewMethod.getter(mname, f);
               impl_cls.addMethod(om);
            } else if (mname.startsWith("set")) {
               String fname = mname.substring(3).toLowerCase();
               CtField f = findField(impl_cls, fname, m.getParameterTypes()[0]);
               CtMethod om = CtNewMethod.setter(mname, f);
               impl_cls.addMethod(om);
            } else {
               CtMethod om = CtNewMethod.make(m.getReturnType(), m.getName(), m.getParameterTypes(), m.getExceptionTypes(), "{ System.out.println(\"generated - "+m.getName()+"\"); }", impl_cls);
               impl_cls.addMethod(om);
            }
         }
      }

      return impl_cls.toBytecode();
   }
}
