package yinst.yahoo.javassist_test;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Hello world!
 *
 */
public class App
{
   public static void describeClass(CtClass c) throws ClassNotFoundException, NotFoundException {
      StringBuilder bb = new StringBuilder();

      bb.append("name: ").append(c.getName()).append("\n");
      bb.append("annotations: ");
      Object[] ann = c.getAnnotations();
      if (ann.length == 0) {
         bb.append("none\n");
      } else {
         for (Object o: ann) {
            bb.append(o.toString()).append(",");
         }
         bb.append("\n");
      }

      bb.append("fields: ");
      CtField[] flds = c.getFields();
      if (flds.length == 0) {
         bb.append("none\n");
      } else {
         for (CtField f: flds) {
            bb.append(f.getName()).append("(").append(f.getType().getName()).append(")");
         }
         bb.append("\n");
      }

      bb.append("methods: ");
      CtMethod[] mthds = c.getMethods();
      if (mthds.length == 0) {
         bb.append("none\n");
      } else {
         bb.append("\n");
         for (CtMethod m: mthds) {
            if (m.getDeclaringClass() == c) {
               bb.append("   ");
               bb.append(m.getReturnType().getSimpleName()).append(" ");
               bb.append(m.getName()).append("(");
               for (CtClass p: m.getParameterTypes()) {
                  bb.append(p.getSimpleName()).append(",");
               }
               bb.append(")\n");
            }
         }
      }

      System.out.println(bb.toString());
   }

    public static void main( String[] args ) throws CannotCompileException, NotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
       /*
        System.out.println( "Hello World!" );
        ClassPool cpool = ClassPool.getDefault();
        CtClass base = cpool.get("yinst.yahoo.javassist_test.HelloWorld");
        CtClass cls = cpool.makeClass("yinst.yahoo.javassist_test.HelloWorldImpl");

        describeClass(base);


        cls.addInterface(base);

        //CtField intfield = CtField.make("int v", cls);
        //cls.addField(intfield);

        CtMethod getValue = CtMethod.make("public int getValue() { return 42; }", cls);
        cls.addMethod(getValue);

        CtMethod getStringValue = CtMethod.make("public int getStringValue() { return \"hello\"; }", cls);
        cls.addMethod(getStringValue);
               */

       ClassGenerator gen = new ClassGenerator();
       AssistedClassLoader loader = new AssistedClassLoader(gen, App.class.getClassLoader());

       String impl = loader.generate(SimpleClass.class);

       Class<?> cls = loader.loadClass(impl);

       SimpleClass s = (SimpleClass)cls.newInstance();

       s.foo();
       s.setStrValue("hello world");

       System.out.println("Value = " + s.getStrValue());

    }
}
