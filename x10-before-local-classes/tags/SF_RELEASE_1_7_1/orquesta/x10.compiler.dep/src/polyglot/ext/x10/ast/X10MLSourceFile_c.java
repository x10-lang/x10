package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile_c;
import polyglot.util.Position;

/** Subclass of SourceFile_c that adds the X10MLSourceFile marker interface.
 * This represents source files read from XML files rather than .x10 files.
 */
public class X10MLSourceFile_c extends SourceFile_c implements X10MLSourceFile {

	public X10MLSourceFile_c(Position pos, PackageNode package_, List imports,
			List decls) {
		super(pos, package_, imports, decls);
	}

}
