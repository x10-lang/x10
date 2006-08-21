package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.MethodDecl_c;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.Translator;

public class X10MethodDecl_c extends MethodDecl_c {

        public X10MethodDecl_c(Position pos, Flags flags, TypeNode returnType,
                        String name, List formals, List throwTypes, Block body) {
                super(pos, flags, returnType, name, formals, throwTypes, body);
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
}
