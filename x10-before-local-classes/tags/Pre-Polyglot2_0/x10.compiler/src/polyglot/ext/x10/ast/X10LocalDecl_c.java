package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.LocalDecl_c;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class X10LocalDecl_c extends LocalDecl_c {

        public X10LocalDecl_c(Position pos, Flags flags, TypeNode type,
                        String name, Expr init) {
                super(pos, flags, type, name, init);
        }

        public String shortToString() {
             return "<LocalDecl_c #" + hashCode() 
        	     // + " flags= |" + flags + "|"
        	 	+(type() == null ? "" : " <TypeNode #" + type().hashCode()+"type=" + type().type() + ">")
        		+ " name=|" + name() + "|"
        		+ (init() == null ? "" : " <Expr #" + init().hashCode() +">")
        		+ (localInstance() == null ? "" : " <LocalInstance #" + localInstance().hashCode() +">")
        		+ ">";
            }

        public LocalDecl type(TypeNode type) {
                // System.out.println("[LocalDecl_c] ... setting type on |" + this.shortToString() + "|.");
                // System.out.println("[LocalDecl_c] ... to |" + type + "|.");
                LocalDecl d= super.type(type);
                // System.out.println("[LocalDecl_c] ... returns|" + d + "|.");
                return d;
        }
        public LocalDecl localInstance(LocalInstance li) {
                // System.out.println("[LocalDecl_c] ... setting localInstance |" + this.shortToString() + "|.");
                // System.out.println("[LocalDecl_c] ... to |" + li + "|.");
                // new SemanticException("", position()).printStackTrace(System.out);
                LocalDecl d= super.localInstance(li);
                // System.out.println("[LocalDecl_c] ... returning|" + d.shortToString() + "|.");
                return d;
        }
        public Node buildTypes(TypeBuilder tb) throws SemanticException {
                // System.out.println("[LocalDecl_c] Building type " + this.shortToString() + ":");
                Node result= super.buildTypes(tb);
                // System.out.println("[LocalDecl_c] ... produces li=|" + result.localInstance() + "|(#"+li.hashCode()+")");
                return result;
        }
        public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
                // System.out.println("[LocalDecl_c] Disambiguating |" + this.shortToString() + ":");
                // System.out.println("[LocalDecl_c] ... i.e. |" + this + "|.");
                // System.out.println("[LocalDecl_c] ... declType=|" + declType() + "|.");
                // System.out.println("[LocalDecl_c] ... li=|" + li + "|.");
                Node result= super.disambiguate(ar);
                // System.out.println("[LocalDecl_c] ... returning with li=|" + result.localInstance() + "|.");
                // System.out.println("[LocalDecl_c] ... returning node |" + result + "|.");
                return result;
        }
        public Node typeCheck(TypeChecker tc) throws SemanticException {
                // System.out.println("[LocalDecl_c] Type checking |" + this.shortToString() + ":");
                // System.out.println("[LocalDecl_c] ... i.e.|" + this + "|.");
                // System.out.println("[LocalDecl_c] ... li is |" + li + "|.");
                // System.out.println("[LocalDecl_c] ... init.type() is |" + init.type() + "|.");
                // System.out.println("[LocalDecl_c] ... type.type() is |" + type.type() + "|.");
                // System.out.println("[LocalDecl_c] ... li.iflags().isFinal() is |" + this.li.flags().isFinal() + "|.");
                // System.out.println("[LocalDecl_c] ... init.isConstant() is |" + init.isConstant() + "|.");
                Node result= super.typeCheck(tc);
                // System.out.println("[LocalDecl_c] ... li is |" + result.localInstance() + "|.");
                // System.out.println("[LocalDecl_c] ... returns with li=|" + li + "|(#" + li.hashCode()+")).");
                return result;
        }
}
