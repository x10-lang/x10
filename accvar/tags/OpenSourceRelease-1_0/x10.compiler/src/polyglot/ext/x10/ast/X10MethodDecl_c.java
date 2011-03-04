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
import polyglot.ast.MethodDecl_c;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
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
        
        protected boolean listIsDisambiguated(List l) {
        	for (Iterator i = l.iterator(); i.hasNext(); ) {
        	    Node o = (Node) i.next();
        	    if (! o.isDisambiguated()) {
        		return false;
        	    }
        	}

        	return true;
            }
        /**
         * A methoddecl is disambiguated if its formals are disambiguated. Note that we cannot
         * just check that the mi is disambiguated since the formal types in there dont yet
         * reflect deptypes.
         */
        public boolean isDisambiguated() {
        	
            return  listIsDisambiguated(formals)
            && (thisClause == null || thisClause.isDisambiguated() )
            && (whereClause == null || whereClause.isDisambiguated())
            && super.isDisambiguated();
        }
        public Node buildTypes(TypeBuilder tb) throws SemanticException {
            X10TypeSystem xts = (X10TypeSystem) tb.typeSystem();

            ParsedClassType ct = tb.currentClass();

            if (ct == null) {
                return this;
            }

            int numFormals= formals == null ? 0 : formals.size();
            List formalTypes = new ArrayList(numFormals);
            for (int i = 0; i < numFormals; i++) {
                formalTypes.add(xts.unknownType(position()));
            }

            List throwTypes = new ArrayList(throwTypes().size());
            for (int i = 0; i < throwTypes().size(); i++) {
                throwTypes.add(xts.unknownType(position()));
            }

    	Flags f = this.flags;

    	if (ct.flags().isInterface()) {
    	    f = f.Public().Abstract();
    	}

            MethodInstance mi = xts.methodInstance(position(), ct, f,
                                                  xts.unknownType(position()),
                                                  name, formalTypes,  throwTypes);
            ct.addMethod(mi);
            return flags(f).methodInstance(mi);
        }

        public void translate(CodeWriter w, Translator tr) {
        	
                Context c = tr.context();
                Flags flags = flags();
                
                // Hack to ensure that X10Flags are not printed out .. javac will
                // not know what to do with them.
                
               this.flags = X10Flags.toX10Flags(flags);
               
                if (c.currentClass().flags().isInterface()) {
                    flags = flags.clearPublic();
                    flags = flags.clearAbstract();
                }
                this.del().prettyPrint(w, tr);
        }
     
        public Node typeCheck(TypeChecker tc) throws SemanticException {
       	  if ( Report.should_report(TOPICS, 5)) {
       		    Report.report(5, "X10MethodDecl_c: typechecking " + name);
       		    Formal first  = (Formal) formals.get(0);
       		 Report.report(5, "X10MethodDecl_c: first arg's type is " + first.type().type());
       	  }
       	 Node result = super.typeCheck(tc);

       
       	 if (  Report.should_report(TOPICS, 5))
    		    Report.report(5, "X10MethodDecl_c: typechecking returning " + result);
       	 return result;
       	 
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
            	 // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
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
            // Update the methodInstance with the depclause-enriched returnType.
           	nn.methodInstance().setReturnType(nn.returnType().type());
           // Report.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
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
             nn = (MethodDecl) childtc2.leave(parent, old, nn, childtc2);
             
            return nn;
        }
       
        private static final Collection TOPICS = 
            CollectionUtil.list(Report.types, Report.context);
}
