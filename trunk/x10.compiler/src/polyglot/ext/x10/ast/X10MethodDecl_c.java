package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collection;
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
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

/** A representation of a method declaration. Includes an extra field to represent the where clause
 * in the method definition.
 * 
 * @author vj
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
     
        public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        	MethodDecl nn = this;
            MethodDecl old = nn;
            
            // Step I. Typecheck the formal arguments. 
        	TypeChecker childtc = (TypeChecker) tc.enter(parent, nn);
            nn = nn.formals(nn.visitList(nn.formals(),childtc));
            // Now build the new formal arg list.
            // TODO: Add a marker to the TypeChecker which records
            // whether in fact it changed the type of any formal.
            if (tc.hasErrors()) throw new SemanticException();
            if (nn != old) {
            	List formals = nn.formals();
            	//List newFormals = new ArrayList(formals.size());
            	List formalTypes = new ArrayList(formals.size());
            	
            	Iterator it = formals.iterator();
            	while (it.hasNext()) {
            		Formal n = (Formal) it.next();
            		Type newType = n.type().type();
            		//LocalInstance li = n.localInstance().type(newType);
            		//newFormals.add(n.localInstance(li));
            		formalTypes.add(newType);
            	}
            	//nn = nn.formals(newFormals);
            	nn.methodInstance().setFormalTypes(formalTypes);
            }
 
            // Step II. Check the return tpe. 
            // Now visit the returntype to ensure that its depclause, if any is processed.
            // Visit the formals so that they get added to the scope .. the return type
            // may reference them.
        	//TypeChecker tc1 = (TypeChecker) tc.copy();
        	// childtc will have a "wrong" mi pushed in, but that doesnt matter.
        	// we simply need to push in a non-null mi here.
        	TypeChecker childtc1 = (TypeChecker) tc.enter(parent, nn);
        	// Add the formals to the context.
        	nn.visitList(nn.formals(),childtc1);
            nn = nn.returnType((TypeNode) nn.visitChild(nn.returnType(), childtc1));
            if (childtc1.hasErrors()) throw new SemanticException(); 
            if (! nn.returnType().type().isCanonical()) {
                return nn;
            }
            // Update the methodInstance with the declause-enriched returnType.
           	nn.methodInstance().setReturnType(nn.returnType().type());
           	
           	// Step III. Check the body. 
           	// We must do it with the correct mi -- the return type will be
           	// checked by return e; statements in the body.
           	
           	TypeChecker childtc2 = (TypeChecker) tc.enter(parent, nn);
           	// Add the formals to the context.
           	nn.visitList(nn.formals(),childtc2);
           	//Report.report(1, "X10MethodDecl_c: after visiting formals " + childtc2.context());
           	// Now process the body.
            nn = (MethodDecl) nn.body((Block) nn.visitChild(nn.body(), childtc2));
            if (childtc2.hasErrors()) throw new SemanticException();
            // nn = (MethodDecl) childtc2.leave(parent, old, nn, childtc2);
            return nn;
        }
       
        private static final Collection TOPICS = 
            CollectionUtil.list(Report.types, Report.context);
}
