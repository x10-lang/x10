package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.TypeNode;
import polyglot.ast.Expr;
import polyglot.ext.jl.ast.ConstructorDecl_c;
import polyglot.types.Flags;
import polyglot.util.Position;

public class X10ConstructorDecl_c extends ConstructorDecl_c {
    protected Expr e;
    public X10ConstructorDecl_c(Position pos, Flags flags, 
            String name, List formals, List throwTypes, Block body) {
        super(pos, flags, name, formals, throwTypes, body);
    }
    public X10ConstructorDecl_c(Position pos, Flags flags, 
            String name, Expr e, List formals, List throwTypes, Block body) {
        super(pos, flags,  name, formals, throwTypes, body);
        this.e=e;
        
    }
    
}
