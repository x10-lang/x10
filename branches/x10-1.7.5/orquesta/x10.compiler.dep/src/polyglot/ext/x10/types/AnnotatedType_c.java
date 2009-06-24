package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.Type_c;
import polyglot.types.Types;

public class AnnotatedType_c extends Type_c implements AnnotatedType {
    protected Ref<? extends Type> rootType;
    protected List<Expr> propertyExprs;

    public Ref<? extends Type> rootType() {
        return rootType;
    }

    public AnnotatedType rootType(Ref<? extends Type> rootType) {
        AnnotatedType_c t = (AnnotatedType_c) copy();
        t.rootType = rootType;
        return t;        
    }

    public List<Expr> propertyExprs() {
        return propertyExprs;
    }

    public Expr propertyExpr(int i) {
        List<Expr> l = this.propertyExprs();
        if (i < l.size()) {
            return (Expr) l.get(i);
        }
        return null;
    }

    public AnnotatedType propertyExprs(List<Expr> l) {
        if (l == null || l.isEmpty()) {
            return this;
        }

        AnnotatedType_c t = (AnnotatedType_c) copy();
        t.propertyExprs = l;
        return t;
    }
    
    public String translate(Resolver c) {
        return Types.get(rootType).translate(c);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        for (Expr e : propertyExprs) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(e.toString());
        }
        
        return rootType.toString() + "(" + sb.toString() + ")";
    }
}
