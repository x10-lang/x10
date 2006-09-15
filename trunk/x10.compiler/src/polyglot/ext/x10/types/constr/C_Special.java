package polyglot.ext.x10.types.constr;

import polyglot.ast.Special;
import polyglot.ast.Special.Kind;
import polyglot.types.Type;
import polyglot.util.Enum;

public interface C_Special extends C_Var {
	 /** Special expression kind: either "super" or "this". */
    public static class C_Kind extends Enum {
        public C_Kind(String name) { super(name); }
        public static C_Kind trans(Kind k) {
        return k.equals(Special.SUPER) ? 
				C_Special.SUPER : 
					k.equals(Special.THIS) ?
							C_Special.THIS : C_Special.SELF;
        }
        
    }

    public static final C_Kind SUPER = new C_Kind("super");
    public static final C_Kind THIS  = new C_Kind("this");
    public static final C_Kind SELF  = new C_Kind("self");

    /** Get the kind of expression: SUPER or THIS or SELF. */
    C_Kind kind();

    /** Get the outer class qualifier of the expression. */
    Type qualifier();
}
