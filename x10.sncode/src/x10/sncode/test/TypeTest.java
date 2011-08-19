package x10.sncode.test;

import junit.framework.TestCase;
import x10.sncode.InvalidClassFileException;
import x10.sncode.Type;
import x10.sncode.Type.FunType;
import x10.sncode.Type.ParamType;
import x10.sncode.Type.RefType;
import x10.sncode.Type.StructType;

public class TypeTest extends TestCase {

    public static Type parse(String s) throws InvalidClassFileException {
        Type t = Type.parse(s);
        assert s.equals(t.desc());
        return t;
    }
    
    public void test1() throws InvalidClassFileException {
        Type t = parse("(*C;!D[?X;];)?X;");
        assert t instanceof FunType;
        assert ((FunType) t).getArgs().size() == 2;
        assert ((FunType) t).getRet() instanceof ParamType;
    }
    public void test2() throws InvalidClassFileException {
        Type t = parse("*C;");
        assert t instanceof RefType;
        assert ((RefType) t).getName().equals("C");
    }
    public void test3() throws InvalidClassFileException {
        Type t = parse("!C;");
        assert t instanceof StructType;
        assert ((RefType) t).getName().equals("C");
    }
    public void test6() throws InvalidClassFileException {
        Type t = parse("?Param;");
        assert t instanceof ParamType;
        assert ((ParamType) t).getName().equals("Param");
    }
    public void test4() throws InvalidClassFileException {
        Type t = parse("*C[?X;];");
        assert t instanceof RefType;
        assert ((RefType) t).getName().equals("C");
    }
    public void test5() throws InvalidClassFileException {
        Type t = parse("!C[?X;];");
        assert t instanceof StructType;
        assert ((RefType) t).getName().equals("C");
    }
}
