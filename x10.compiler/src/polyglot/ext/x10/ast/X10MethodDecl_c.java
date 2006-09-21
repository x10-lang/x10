package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.MethodDecl_c;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

/** A representation of a method declaration. Includes an extra field to represent the where clause
 * in the method definition.
 * 
 * @author VijaySaraswat
 *
 */
public class X10MethodDecl_c extends MethodDecl_c {
    // The representation of this( DepParameterExpr ) in the production.
    DepParameterExpr thisClause;
    // The reprsentation of the : Constraint in the parameter list.
    Expr whereClause;
     /*   public X10MethodDecl_c(Position pos, 
                Flags flags, TypeNode returnType,
                String name, List formals, List throwTypes, Block body) {
                super(pos, flags, returnType, name, formals, throwTypes, body);
        }*/
        public X10MethodDecl_c(Position pos, DepParameterExpr thisClause, 
                Flags flags, TypeNode returnType,
                String name, List formals, Expr e, List throwTypes, Block body) {
        super(pos, flags, returnType, name, formals, throwTypes, body);
        whereClause = e;
}
        public void translate(CodeWriter w, Translator tr) {
                Context c = tr.context();
                Flags flags = flags();

                if (c.currentClass().flags().isInterface()) {
                    flags = flags.clearPublic();
                    flags = flags.clearAbstract();
                }
                this.del().prettyPrint(w, tr);
        }
        public Node typeCheck(TypeChecker tc) throws SemanticException {
      	  TypeSystem ts = tc.typeSystem();
              MethodDecl result= (MethodDecl) super.typeCheck(tc);
              // Now after type checking the types of the formals and the return type may have
              // changed -- e.g. they acquired depclause constraints.
              // Ensure that the associated MethodInstance is destructively updated.
              MethodInstance mi = result.methodInstance();
              mi.setReturnType(result.returnType().type());
              List formals = result.formals();
              List formalTypes = new ArrayList(formals.size());
              
              Iterator it = formals.iterator();
              while (it.hasNext()) formalTypes.add(((Formal)it.next()).type().type());
              mi.setFormalTypes(formalTypes);
              
             // Report.report(1, "X10MethodDecl.typeCheck returns " 
            //		  + result + " mi=" + result.methodInstance());
              return result;
      }
}
