/**
 * 
 */
package polyglot.frontend;

import java.util.Collections;
import java.util.List;

public class SetResolverGoal extends AbstractGoal_c {
	Job job;

	public SetResolverGoal(Job job) {
		this.job = job;
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