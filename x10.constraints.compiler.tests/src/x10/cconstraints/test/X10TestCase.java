package x10.cconstraints.test;

import java.util.LinkedHashSet;
import java.util.List;

import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Globals;
import polyglot.main.Main;
import polyglot.types.ContainerType;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.constraint.XTerm;
import x10.types.ThisDef;
import x10.types.X10FieldDef;
import x10.types.X10FieldDef_c;
import x10.types.constraints.CConstraint;
import junit.framework.TestCase;

public class X10TestCase extends TestCase {
    protected ExtensionInfo ext;
    protected TypeSystem ts;
    Compiler compiler;
    public X10TestCase(String name) {
        super(name);
        Main main = new Main();
        compiler = main.getCompiler(new String[]{"-extclass",
                "x10c.ExtensionInfo", "Foo.x10"}, null, null, new LinkedHashSet<String>());
        Globals.initialize(compiler);
        ext = compiler.sourceExtension();
        ts = ext.typeSystem();
    }
    public X10FieldDef makeField(String name) {
        return makeField(name, null);
    }
    public X10FieldDef makeField(String name, Type type) {
        return new X10FieldDef_c(ts, Position.COMPILER_GENERATED, 
                          (Ref <? extends ContainerType>) Types.ref((ContainerType) null), 
                          Flags.NONE, 
                          (Ref<? extends Type>) Types.ref(type), 
                          Name.make(name), 
                          (ThisDef) null);
    }
    public void print(CConstraint c) {
        System.out.print("{");
        boolean notFirst=false;
        List<? extends XTerm> terms = c.constraints();
        for (XTerm x : terms) {
            System.out.print((notFirst? ",":"") + x);
            notFirst=true;
        }
        System.out.println("}");
    }
}
