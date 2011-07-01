/**
 * 
 */
package polyglot.frontend;

import java.util.Collections;
import java.util.List;

public class SetResolverGoal extends SourceGoal_c {
	private static final long serialVersionUID = -3338244389002898144L;

    
	public SetResolverGoal(Job job) {
        super(job);
	}

	@Override
	public List<Goal> prereqs() {
		return Collections.singletonList(job.extensionInfo().scheduler().PreTypeCheck(job));
	}

	@Override
	public boolean runTask() {
		return true;
	}
}