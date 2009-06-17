package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.Ref;
import polyglot.types.Type;

public interface AnnotatedType extends Type {
    Ref<? extends Type> rootType();
    AnnotatedType rootType(Ref<? extends Type> rootType);
    
    List<Expr> propertyExprs();
    AnnotatedType propertyExprs(List<Expr> l);
    Expr propertyExpr(int i);
}
