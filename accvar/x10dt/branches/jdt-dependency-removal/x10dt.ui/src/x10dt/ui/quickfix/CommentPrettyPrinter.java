package x10dt.ui.quickfix;

import polyglot.ast.Empty;
import polyglot.ast.Node;
import polyglot.util.CodeWriter;
import polyglot.visit.PrettyPrinter;
import x10.extension.X10Ext;

public class CommentPrettyPrinter extends PrettyPrinter {
	@Override
	public void print(Node parent, Node n, CodeWriter w) {
		X10Ext ext = (X10Ext) n.ext();
		if (ext != null && ext.comment() != null)
		{
			w.write(ext.comment());
			if(n instanceof Empty)
			{
				return;
			}
		}
		super.print(parent, n, w);
	}
}
