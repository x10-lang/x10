/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * Scheduler.java
 * 
 * Author: nystrom
 * Creation date: Feb 6, 2005
 */
package polyglot.frontend;

import java.util.*;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.main.Version;
import polyglot.types.*;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.visit.*;

/**
 * Comment for <code>Scheduler</code>
 *
 * @author nystrom
 */
public class JLScheduler extends Scheduler {

    /**
     * @param extInfo
     */
    public JLScheduler(ExtensionInfo extInfo) {
        super(extInfo);
    }

    public List<Goal> goals(Job job) {
        List<Goal> goals = new ArrayList<Goal>();

        goals.add(Parsed(job));
        goals.add(TypesInitialized(job));
        goals.add(ImportTableInitialized(job));
        
        goals.add(PreTypeCheck(job));
        goals.add(TypesInitializedForCommandLineBarrier());
        goals.add(TypeChecked(job));
        goals.add(ReassembleAST(job));
        
        goals.add(ConformanceChecked(job));
        goals.add(ReachabilityChecked(job));
        goals.add(ExceptionsChecked(job));
        goals.add(ExitPathsChecked(job));
        goals.add(InitializationsChecked(job));
        goals.add(ConstructorCallsChecked(job));
        goals.add(ForwardReferencesChecked(job));
        goals.add(Serialized(job));
        goals.add(CodeGenerated(job));
        goals.add(End(job));
        
        return goals;
    }

    public Goal Parsed(Job job) {
    	return new ParserGoal(extInfo.compiler(), job).intern(this);
    }    

    public Goal ImportTableInitialized(Job job) {
        TypeSystem ts = job.extensionInfo().typeSystem();
        NodeFactory nf = job.extensionInfo().nodeFactory();
        Goal g = new VisitorGoal("ImportTableInitialized", job, new InitImportsVisitor(job, ts, nf));
        Goal g2 = g.intern(this);
//        if (g == g2) {
//            g.addPrereq(TypesInitializedForCommandLine());
//        }
        return g2;
    }

    public Goal TypesInitialized(Job job) {
        // For this one, the goal is stored in the job.  This is called a lot from the system resolver and interning is expensive.
        return job.TypesInitialized(this);
    }

    public Goal TypesInitializedForCommandLineBarrier() {
        TypeSystem ts = extInfo.typeSystem();
        NodeFactory nf = extInfo.nodeFactory();
        return new BarrierGoal("TypesInitializedForCommandLineBarrier", commandLineJobs()) {
            public Goal prereqForJob(Job job) {
                return PreTypeCheck(job);
            }
        }.intern(this);
    }

    public Goal PreTypeCheck(Job job) {
    	TypeSystem ts = job.extensionInfo().typeSystem();
    	NodeFactory nf = job.extensionInfo().nodeFactory();
        return new VisitorGoal("PreTypeCheck", job, new TypeCheckPreparer(job, ts, nf, job.nodeMemo())).intern(this);
    }
    
    public Goal ReassembleAST(final Job job) {
    	final Map<Node, Node> memo = job.nodeMemo();
    	return new VisitorGoal("ReassembleAST", job, new NodeVisitor() {
    		public Node leave(Node old, Node n, NodeVisitor v) {
    			Node m = memo.get(old);
    			
    			if (old == job.ast()) {
    				memo.clear();
    			}

    			if (m != null) {
    				return m;
    			}

    			return n;
    		}
    	}).intern(this);
    }
    
    public Goal TypeChecked(Job job) {
    	TypeSystem ts = job.extensionInfo().typeSystem();
    	NodeFactory nf = job.extensionInfo().nodeFactory();
    	return new VisitorGoal("TypeChecked", job, new TypeChecker(job, ts, nf, job.nodeMemo())).intern(this);
    }
    
    public Goal ConformanceChecked(Job job) {
	TypeSystem ts = job.extensionInfo().typeSystem();
	NodeFactory nf = job.extensionInfo().nodeFactory();
	return new VisitorGoal("ConformanceChecked", job, new ConformanceChecker(job, ts, nf)).intern(this);
    }
    
    public Goal ReachabilityChecked(Job job) {
        TypeSystem ts = job.extensionInfo().typeSystem();
        NodeFactory nf = job.extensionInfo().nodeFactory();
        return new VisitorGoal("ReachChecked", job, new ReachChecker(job, ts, nf)).intern(this);
    }

    public Goal ExceptionsChecked(Job job) {
        TypeSystem ts = job.extensionInfo().typeSystem();
        NodeFactory nf = job.extensionInfo().nodeFactory();
        return new VisitorGoal("ExceptionsChecked", job, new ExceptionChecker(job, ts, nf)).intern(this);
    }

    public Goal ExitPathsChecked(Job job) {
        TypeSystem ts = job.extensionInfo().typeSystem();
        NodeFactory nf = job.extensionInfo().nodeFactory();
        return new VisitorGoal("ExitChecked", job, new ExitChecker(job, ts, nf)).intern(this);
    }

    public Goal InitializationsChecked(Job job) {
        TypeSystem ts = job.extensionInfo().typeSystem();
        NodeFactory nf = job.extensionInfo().nodeFactory();
        return new VisitorGoal("InitializationsChecked", job, new InitChecker(job, ts, nf)).intern(this);
    }

    public Goal ConstructorCallsChecked(Job job) {
        TypeSystem ts = job.extensionInfo().typeSystem();
        NodeFactory nf = job.extensionInfo().nodeFactory();
        return new VisitorGoal("ContructorCallsChecked", job, new ConstructorCallChecker(job, ts, nf)).intern(this);
    }

    public Goal ForwardReferencesChecked(Job job) {
        TypeSystem ts = job.extensionInfo().typeSystem();
        NodeFactory nf = job.extensionInfo().nodeFactory();
        return new VisitorGoal("ForwardRefsChecked", job, new FwdReferenceChecker(job, ts, nf)).intern(this);
    }

    public Goal Serialized(Job job) {
    	Compiler compiler = job.extensionInfo().compiler();
    	TypeSystem ts = job.extensionInfo().typeSystem();
    	NodeFactory nf = job.extensionInfo().nodeFactory();
    	if (compiler.serializeClassInfo()) {
    		return new VisitorGoal("Serialized", job,
    				new ClassSerializer(ts, nf, job.source().lastModified(), compiler.errorQueue(), extInfo.version())).intern(this);
    	}
    	else {
    		return new SourceGoal_c("Serialized", job) {
    			public boolean runTask() {
    				return true;
    			}
            }.intern(this);
        }
    }

    protected ClassSerializer createSerializer(TypeSystem ts, NodeFactory nf,
            Date lastModified, ErrorQueue eq, Version version) {
        return new ClassSerializer(ts, nf, lastModified, eq, version);
    }

    public Goal CodeGenerated(Job job) {
    	TypeSystem ts = extInfo.typeSystem();
    	NodeFactory nf = extInfo.nodeFactory();
    	return new OutputGoal(job, new Translator(job, ts, nf, extInfo.targetFactory())).intern(this);
    }

    @Override
    public Goal LookupGlobalType(LazyRef<Type> sym) {
        return new LookupGlobalType("LookupGlobalType", sym).intern(this);
    }

    @Override
    public Goal LookupGlobalTypeDef(LazyRef<ClassDef> sym, QName className) {
        return LookupGlobalTypeDefAndSetFlags(sym, className, null);
    }

    @Override
    public Goal LookupGlobalTypeDefAndSetFlags(LazyRef<ClassDef> sym,
	    QName className, Flags flags) {
        return new LookupGlobalTypeDefAndSetFlags(sym, className, flags).intern(this);
    }
    
    public Goal EnsureNoErrors(Job job) {
           return new SourceGoal_c("EnsureNoErrors", job) {
               public boolean runTask() {
                   return !job.reportedErrors();
               }
           }.intern(this);
       }

    public static class LookupGlobalType extends TypeObjectGoal_c<Type> {
        public LookupGlobalType(String name, Ref<Type> v) {
            super(name, v);
            ref = Types.lazyRef(null);
			Goal g = Globals.Scheduler().LookupGlobalTypeDef(ref, null);
			ref.setResolver(g);
        }
        
        LazyRef<ClassDef> ref;
        
        public boolean runTask() {
        	ClassDef def = ref.get();
        	v.update(def.asType());
        	return true;
        }
    }

    protected static class LookupGlobalTypeDefAndSetFlags extends TypeObjectGoal_c<ClassDef> {
        protected QName className;
        protected Flags flags;

        private LookupGlobalTypeDefAndSetFlags(Ref<ClassDef> v, QName className, Flags flags) {
            super(v);
            this.className = className;
            this.flags = flags;
        }

        public String toString() {
            if (flags == null)
                return name + "(" + className + ")";
            else 
                return name + "(" + className + ", " + flags + ")";
        }

        public boolean runTask() {
        	LazyRef<ClassDef> ref = (LazyRef<ClassDef>) typeRef();
        	try {
        		TypeSystem ts = Globals.TS();
			Named n = ts.systemResolver().find(QName.make(className));
        		if (n instanceof ClassType) {
        			ClassType ct = (ClassType) n;
        			ClassDef def = ct.def();
        			if (flags != null) {
        				// The flags should be overwritten only for a member class.
        				assert def.isMember();
        				def.setFlags(flags);
        			}
        			ref.update(def);
        			return true;
        		}
        	}
        	catch (SemanticException e) {
        		Globals.Compiler().errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, e.getMessage(), e.position());
        	}
        	return false;
        }
    }
}
