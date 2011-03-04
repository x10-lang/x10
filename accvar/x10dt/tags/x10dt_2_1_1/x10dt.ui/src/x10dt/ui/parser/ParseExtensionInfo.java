package x10dt.ui.parser;

import java.util.ArrayList;
import java.util.List;

import lpg.runtime.IMessageHandler;
import lpg.runtime.Monitor;

import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;

public class ParseExtensionInfo extends ExtensionInfo {
	
	public ParseExtensionInfo(Monitor monitor, IMessageHandler handler){
		super(monitor, handler);
	}

	@Override
    protected Scheduler createScheduler() {
        return new X10Scheduler(this) {
            @Override
            public List<Goal> goals(Job job) {
                if (fInterestingSources.contains(job.source())) {
                	fInterestingJobs.put(job.source(), job);
                }
                List<Goal> goals = new ArrayList<Goal>();
                goals.add(Parsed(job));
                return goals;
            }
 
        };
    }
}
