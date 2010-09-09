package x10.refactoring.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lpg.runtime.Monitor;
import polyglot.ast.Node;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.AllBarrierGoal;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.frontend.Source;
import polyglot.frontend.SourceGoal_c;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

public class TestExtensionInfo extends ExtensionInfo {
    private X10Lexer fLexer;
    private X10Parser fParser;
    private final Monitor fMonitor;

    private final Set<Source> fInterestingSources = new HashSet<Source>();
    private final Map<Source,Node> fInterestingASTs = new HashMap<Source,Node>();

    public TestExtensionInfo(Monitor monitor) {
        fLexer = new X10Lexer();
        fLexer.reset(new char[0], "");
        fParser = new X10Parser(fLexer.getILexStream());
        fMonitor = monitor;
    }

    public void setInterestingSources(Collection<Source> sources) {
        fInterestingSources.clear();
        fInterestingASTs.clear();
        fInterestingSources.addAll(sources);
    }

    public Node getASTFor(Source src) { return fInterestingASTs.get(src); }

    @Override
    protected Scheduler createScheduler() {
        return new X10Scheduler(this) {
            @Override
            public List<Goal> goals(Job job) {
                List<Goal> goals = new ArrayList<Goal>();
                // This is essentially the list of goals specified by the base class,
                // up through and including type-checking.
                goals.add(Parsed(job));
                goals.add(TypesInitialized(job));
                goals.add(ImportTableInitialized(job));
                goals.add(CastRewritten(job));
                goals.add(PropagateAnnotations(job));
                goals.add(PreTypeCheck(job));
                goals.add(TypeChecked(job));

                if (TestExtensionInfo.this.fInterestingSources.contains(job.source())) {
                    goals.add(RetrieveAST(job));
                }

                goals.add(End(job));
                return goals;
            }
            /**
             * This goal simply retrieves the AST for the given job and squirrels it
             * away into the map fInterestingASTs.
             */
            Goal RetrieveAST(Job job) {
                return new SourceGoal_c("AST retriever", job) {
                    @Override
                    public boolean runTask() {
                        TestExtensionInfo.this.fInterestingASTs.put(job().source(), job().ast());
                        return true;
                    }
                }.intern(scheduler);
            }
            @Override
            protected Goal EndAll() { // a dummy goal that replaces PostCompiled
                return new AllBarrierGoal(this) {
                    @Override
                    public Goal prereqForJob(Job job) {
                        if (scheduler.shouldCompile(job)) {
                            return scheduler.End(job);
                        }
                        else {
                            return new SourceGoal_c("DummyEnd", job) {
                                public boolean runTask() { return true; }
                            }.intern(scheduler);
                        }
                    }
                };
            }
        };
    }
}
