/**
 * 
 */
package polyglot.frontend;

import x10.parser.antlr.ASTBuilder;

/**
 * @author lmandel
 *
 */
public class ParserCleanupGoal extends SourceGoal_c {

	private static final long serialVersionUID = -1766221295003729438L;

	public ParserCleanupGoal(Job job) {
		super(job);
	}

	@Override
	public boolean runTask() {
		if (!((x10.ExtensionInfo) scheduler.extensionInfo()).getOptions().x10_config.ANTLR_CACHE_WRITE) {
			ASTBuilder.clearState();
		}
		return true;
	}

}
