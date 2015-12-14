/**
 * 
 */
package polyglot.frontend;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import polyglot.ast.Node;
import polyglot.main.Reporter;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.parser.antlr.ASTBuilder;

/**
 * @author lmandel
 *
 */
public class ParserTrainer {

	private static final long serialVersionUID = 6721064386214504002L;

	x10.ExtensionInfo extensionInfo;
	Collection<Source> sources;

	public ParserTrainer(ExtensionInfo extensionInfo, Collection<Source> sources) {
		this.extensionInfo = extensionInfo;
		this.sources = sources;
	}

	public boolean runTask() {
		ErrorQueue eq = extensionInfo.compiler.errorQueue();

		try {
			for (Source source : this.sources) {
				if (!(source instanceof FileSource)) { continue; }
				FileSource src = (FileSource) source;
				Reader reader = src.open();
				Parser p = extensionInfo.parser(reader, src, eq);
				p.parse();
				src.close();
			}
			ASTBuilder.writeState();
		} catch (Exception exn) {
			eq.enqueue(ErrorInfo.WARNING, "unable to save the parser state cache ("+exn+").");
		}

		return true;
	}

}
