package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Import;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile_c;
import polyglot.ast.TopLevelDecl;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class X10SourceFile_c extends SourceFile_c {
    
    public X10SourceFile_c(Position pos, PackageNode package_, List<Import> imports, List<TopLevelDecl> decls) {
	super(pos, package_, imports, decls);
    }
    
    /** Type check the source file. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
	boolean hasPublic = false;
	
	// Don't check for more than one public declaration in package.x10.
//	if (source() != null && source().name().equals("package.x10")) {
//	    return this;
//	}
	
	// Override method to not check for duplicate declarations.  This will be caught during type building.  But, we need to allow duplicates to handle overloaded typedefs.
	for (Iterator i = decls.iterator(); i.hasNext();) {
	    TopLevelDecl d = (TopLevelDecl) i.next();

	    if (d.flags().flags().isPublic()) {
		if (hasPublic) {
		    throw new SemanticException(
			"The source contains more than one public declaration.",
			d.position());
		}

		hasPublic = true;
	    }
	}
     
	return this;
    }
}
