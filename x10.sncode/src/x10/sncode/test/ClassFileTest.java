package x10.sncode.test;

import junit.framework.TestCase;
import x10.sncode.ByteBuffer;
import x10.sncode.ClassEditor;
import x10.sncode.InvalidClassFileException;
import x10.sncode.LocalEditor;
import x10.sncode.MethodEditor;
import x10.sncode.SnFile;
import x10.sncode.Type;

public class ClassFileTest extends TestCase {
    public void test1() throws InvalidClassFileException {
        SnFile sn = new SnFile();
        ClassEditor c = new ClassEditor();
        c.setSuperClass(TypeTest.parse("*x10/lang/Object;"));
        c.setName("C");
        c.setSuperClass(Type.parse("*D;"));
        MethodEditor m = new MethodEditor();
        c.methods().add(m);
        m.setName("m");
        m.setFormals(new LocalEditor[0]);
        m.setReturnType(TypeTest.parse("V"));
        sn.addClass(c);

        ByteBuffer b = new ByteBuffer();
        sn.writeInto(b);
        b.seek(0);

        SnFile sn2 = new SnFile();
        sn2.readFrom(b);
        b.seek(0);
        
        sn2.classes();
        assert sn2.classes().size() == 1;
        ClassEditor c2 = sn2.classes().get(0);
        assert c2.getName().equals("C");
        
        assert c2.methods().size() == 1;
        MethodEditor m2 = c2.methods().get(0);
        assert m2.getName().equals("m");
        assert m2.getFormals().size() == 0;
        assert m2.getReturnType().equals(TypeTest.parse("V"));
    }
}
