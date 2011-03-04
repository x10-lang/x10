// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 


package polyglot.ext.x10;


import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lpg.runtime.LexStream;

import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.dom.DomParser;
import polyglot.ext.x10.plugin.CompilerPlugin;
import polyglot.ext.x10.plugin.LoadJobPlugins;
import polyglot.ext.x10.plugin.LoadPlugins;
import polyglot.ext.x10.plugin.RegisterPlugins;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.visit.AnnotationChecker;
import polyglot.ext.x10.visit.AssignPropertyChecker;
import polyglot.ext.x10.visit.CastRewriter;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.PropagateAnnotationsVisitor;
import polyglot.ext.x10.visit.PropagateDependentAnnotationsVisitor;
import polyglot.ext.x10.visit.TypeElaborator;
import polyglot.ext.x10.visit.TypePropagator;
import polyglot.ext.x10.visit.X10Boxer;
import polyglot.ext.x10.visit.X10Caster;
import polyglot.ext.x10.visit.X10ImplicitDeclarationExpander;
import polyglot.ext.x10.visit.X10MLVerifier;
import polyglot.ext.x10.visit.X10Translator;
import polyglot.frontend.AbstractPass;
import polyglot.frontend.Compiler;
import polyglot.frontend.EmptyPass;
import polyglot.frontend.FileSource;
import polyglot.frontend.JLScheduler;
import polyglot.frontend.Job;
import polyglot.frontend.OutputPass;
import polyglot.frontend.Parser;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.SchedulerException;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.ClassTypeGoal;
import polyglot.frontend.goals.CodeGenerated;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.MembersAdded;
import polyglot.frontend.goals.SignaturesResolved;
import polyglot.frontend.goals.SupertypesResolved;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.frontend.passes.AddMembersPass;
import polyglot.frontend.passes.DisambiguateSignaturesPass;
import polyglot.frontend.passes.ResolveSuperTypesPass;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.types.ParsedClassType;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.visit.ConstantChecker;
import polyglot.visit.TypeChecker;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

/**
 * Extension information for x10 extension.
 */
public class ExtensionInfo extends polyglot.frontend.ParserlessJLExtensionInfo {
    static final boolean DEBUG_ = false;
    static {
        // force Topics to load
        Topics t = new Topics();
    }
    
    protected Map plugins;

	public static final String XML_FILE_EXTENSION = "x10ml";
	public static final String XML_FILE_DOT_EXTENSION = "." + XML_FILE_EXTENSION;
  
    public static String clock = "clock";

    static {
        Report.topics.add(clock);
    }

    public polyglot.main.Version version() {
    	return new Version();
    }
    public String[] fileExtensions() {
    	return new String[] { "x10", XML_FILE_EXTENSION };
    }
//    public String defaultFileExtension() {
//        return "x10";
//    }

    public String compilerName() {
        return "x10c";
    }

    //
    // Replace the Flex/Cup parser with a JikesPG parser
    //
    //    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) { 
    //        Lexer lexer = new Lexer_c(reader, source.name(), eq);
    //        Grm grm = new Grm(lexer, ts, nf, eq);
    //        return new CupParser(grm, source, eq);
    //    }
    //
    
    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        if (source.path().endsWith(XML_FILE_DOT_EXTENSION)) {
        	return new DomParser(reader, (X10TypeSystem) ts, (X10NodeFactory) nf, source, eq);
        }

    	try {
            //
            // X10Lexer may be invoked using one of two constructors.
            // One constructor takes as argument a string representing
            // a (fully-qualified) filename; the other constructor takes
            // as arguments a (file) Reader and a string representing the
            // name of the file in question. Invoking the first
            // constructor is more efficient because a buffered File is created
            // from that string and read with one (read) operation. However,
            // we depend on Polyglot to provide us with a fully qualified
            // name for the file. In Version 1.3.0, source.name() yielded a
            // fully-qualied name. In 1.3.2, source.path() yields a fully-
            // qualified name. If this assumption still holds then the 
            // first constructor will work.
            // The advantage of using the Reader constructor is that it
            // will always work, though not as efficiently.
            //
            // X10Lexer x10_lexer = new X10Lexer(reader, source.name());
            //
            X10Lexer x10_lexer = new X10Lexer(source.path());
            X10Parser x10_parser = new X10Parser((LexStream) x10_lexer.getLexStream(), ts, nf, source, eq); // Create the parser
            x10_lexer.lexer(x10_parser.getIPrsStream());
            return x10_parser; // Parse the token stream to produce an AST
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not parse " + source.path());
    }

    protected NodeFactory createNodeFactory() {
        return new X10NodeFactory_c(this);
    }

    protected TypeSystem createTypeSystem() {
        return new X10TypeSystem_c();
    }

    public void initCompiler(Compiler compiler) {
	super.initCompiler(compiler);
	QueryEngine.init(this);
    }

    // =================================
    // X10-specific goals and scheduling
    // =================================
    protected Scheduler createScheduler() {
        return new X10Scheduler(this);
    }
    
    protected static class EmptyGoal extends AbstractGoal {
    	Goal dep;
		protected EmptyGoal(Job job, Goal dep) {
			super(job);
			this.dep = dep;
		}
		protected EmptyGoal(Job job) {
			super(job);
		}
		public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
			return new EmptyPass(this);
		}
		@Override
		public Collection prerequisiteGoals(Scheduler scheduler) {
			Collection l = super.prerequisiteGoals(scheduler);
			if (dep == null)
				return l;
			List l2 = new ArrayList(l.size()+1);
			l2.add(dep);
			l2.addAll(l);
			return l2;
		}
    }
    public static class X10Scheduler extends JLScheduler {
       protected X10Scheduler(ExtensionInfo extInfo) {
		   super(extInfo);
	   }
	   public Goal LoadPlugins() {
		   return LoadPlugins.create(this);
	   }
	   public Goal LoadJobPlugins(final Job job) {
		   return LoadJobPlugins.create(this, job);
	   }
	   public Goal RegisterPlugins(final Job job) {
		   return RegisterPlugins.create(this, job);
	   }
	   public Goal CodeGenerated(final Job job) {
		   return X10CodeGenerated.create(this, job);
	   }
	   public Goal MembersAdded(ParsedClassType ct) {
		   return X10MembersAdded.create(this, ct);
	   }
	   public Goal SupertypesResolved(ParsedClassType ct) {
		   return X10SupertypesResolved.create(this, ct);
	   }
	   public Goal SignaturesResolved(ParsedClassType ct) {
		   return X10SignaturesResolved.create(this, ct);
	   }
	   public Goal PropagateDeclarationAnnotations(Job job) {
		   return PropagateDeclarationAnnotations.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal PropagateDependentAnnotations(Job job) {
		   return PropagateDependentAnnotations.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal PropagateAnnotations(Job job) {
		   return PropagateAnnotations.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal AnnotationsChecked(Job job) {
		   return AnnotationsChecked.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal X10Boxed(final Job job) {
		   return X10Boxed.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal X10Casted(final Job job) {
		   return X10Casted.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal CastRewritten(final Job job) {
		   return CastRewritten.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal X10Expanded(final Job job) {
		   return X10Expanded.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   /*public Goal X10ExprFlattened(final Job job) {
	    return X10ExprFlattened.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	    }*/
	   public Goal TypeElaborated(final Job job) {
		   return TypeElaborated.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal TypePropagated(final Job job) {
		   return TypePropagated.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal TypesInitialized(Job job) {
		   if (job.source().path().endsWith(XML_FILE_DOT_EXTENSION)) {
//			   return new EmptyGoal(job, Parsed(job));
			   return Parsed(job);
		   }
		   return super.TypesInitialized(job);
	   }
	   public Goal TypeChecked(final Job job) {
		   if (job.source().path().endsWith(XML_FILE_DOT_EXTENSION))
			   return X10MLTypeChecked.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
		   else
			   return X10TypeChecked.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal ConstantsChecked(final Job job) {
		   if (job.source().path().endsWith(XML_FILE_DOT_EXTENSION)) {
//			   return new EmptyGoal(job, TypeChecked(job));
			   return TypeChecked(job);
		   }
		   return X10ConstantsChecked.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal PropertyAssignmentsChecked(final Job job) {
		   return PropertyAssignmentsChecked.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	   }
	   public Goal TypeObjectAnnotationsPropagated(X10ParsedClassType ct) {
		   return TypeObjectAnnotationsPropagated.create(this, ct);
	   }
   }
   
   static class TypeObjectAnnotationsPropagated extends ClassTypeGoal {
	    public static Goal create(Scheduler scheduler, X10ParsedClassType ct) {
	        return scheduler.internGoal(new TypeObjectAnnotationsPropagated(ct));
	    }

	    protected TypeObjectAnnotationsPropagated(X10ParsedClassType ct) {
	        super(ct);
	    }

	    public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
	    	return new AbstractPass(this) {
	    		public boolean run() {
	    	        TypeObjectAnnotationsPropagated goal = (TypeObjectAnnotationsPropagated) this.goal;
					X10ParsedClassType ct = (X10ParsedClassType) goal.type();
					assert ct.isRootType();
					if (! ct.annotationsSet()) {
						ct.setAnnotations(Collections.EMPTY_LIST);
					}
					if (! ct.classAnnotationsSet()) {
						if (ct.job() == null) {
							ct.setClassAnnotations(Collections.EMPTY_LIST);
						}
						else {
							throw new SchedulerException();
						}
					}
	    	        return true;
	    		}
	    	};
	    }
	    
	    public Collection prerequisiteGoals(Scheduler scheduler) {
	        List l = new ArrayList();
	        if (ct != null && ct.job() != null) {
	        	l.add(((X10Scheduler) scheduler).PropagateDeclarationAnnotations(ct.job()));
	        }
	        l.addAll(super.prerequisiteGoals(scheduler));
	        return l;
	    }

//	    public Collection corequisiteGoals(Scheduler scheduler) {
//	        List l = new ArrayList();
//	        l.addAll(super.corequisiteGoals(scheduler));
//	        return l;
//	    }
	    
	    public boolean equals(Object o) {
	        return o instanceof TypeObjectAnnotationsPropagated && super.equals(o);
	    }
	}
   
   static class X10SignaturesResolved extends SignaturesResolved {
	   public static Goal create(Scheduler scheduler, ParsedClassType ct) {
		   return scheduler.internGoal(new X10SignaturesResolved(ct));
	   }
	   protected X10SignaturesResolved(ParsedClassType ct) {
		   super(ct);
	   }
	   public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
		   if (job() != null && job().source().path().endsWith(XML_FILE_DOT_EXTENSION))
			   return new DisambiguateSignaturesPass(extInfo.scheduler(), this);
		   return super.createPass(extInfo);
	   }
	   public Collection prerequisiteGoals(Scheduler scheduler) {
		   X10Scheduler x10Sched= (X10Scheduler) scheduler;
		   List<Goal> l = new ArrayList<Goal>();
		   l.addAll(super.prerequisiteGoals(scheduler));
		   return l;
	   }
	   public Collection corequisiteGoals(Scheduler scheduler) {
		   X10Scheduler x10Sched= (X10Scheduler) scheduler;
		   List<Goal> l = new ArrayList<Goal>();
		   if (job != null && ! job.source().path().endsWith(XML_FILE_DOT_EXTENSION))
			   l.add(x10Sched.TypeElaborated(job));
		   l.addAll(super.corequisiteGoals(scheduler));
		   return l;
	   }
   }
   static class X10MembersAdded extends MembersAdded {
	   public static Goal create(Scheduler scheduler, ParsedClassType ct) {
		   return scheduler.internGoal(new X10MembersAdded(ct));
	   }
	   protected X10MembersAdded(ParsedClassType ct) {
		   super(ct);
	   }
	   @Override
	   public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
		   if (job() != null && job().source().path().endsWith(XML_FILE_DOT_EXTENSION))
			   return new AddMembersPass(extInfo.scheduler(), this);
		   else
			   return super.createPass(extInfo);
	   }
   }
   static class X10SupertypesResolved extends SupertypesResolved {
	   public static Goal create(Scheduler scheduler, ParsedClassType ct) {
		   return scheduler.internGoal(new X10SupertypesResolved(ct));
	   }
	   protected X10SupertypesResolved(ParsedClassType ct) {
		   super(ct);
	   }
	   @Override
	   public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
		   if (job() != null && job().source().path().endsWith(XML_FILE_DOT_EXTENSION))
			   return new ResolveSuperTypesPass(extInfo.scheduler(), this);
		   return super.createPass(extInfo);
	   }
   }
    protected static class X10CodeGenerated extends CodeGenerated {
    	public static Goal create(Scheduler scheduler, Job job) {
    		return scheduler.internGoal(new X10CodeGenerated(job));
    	}
    	protected X10CodeGenerated(Job job) {
    		super(job);
    	}
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched= (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.X10Boxed(job));
    		l.add(x10Sched.X10Casted(job));    		
    	//	l.add(x10Sched.X10ExprFlattened(job));
    		l.add(x10Sched.TypeElaborated(job));
    		l.add(x10Sched.X10Expanded(job));
    		l.add(x10Sched.PropertyAssignmentsChecked(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
            TypeSystem ts = extInfo.typeSystem();
            NodeFactory nf = extInfo.nodeFactory();
            return new OutputPass(this, new X10Translator(job(), ts, nf,
                                                      extInfo.targetFactory()));
        }
    }
    static class PropagateDeclarationAnnotations extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new PropagateDeclarationAnnotations(job, ts, nf));
    	}
    	private PropagateDeclarationAnnotations(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new PropagateAnnotationsVisitor(job, ts, nf));
    	}
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.Disambiguated(job));
    		l.add(x10Sched.TypePropagated(job));
    		l.add(scheduler.TypeChecked(job));
    		l.add(scheduler.ConstantsChecked(job));
    		l.add(x10Sched.AnnotationsChecked(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
    		return super.createPass(extInfo);
    	}
    }
    static class PropagateDependentAnnotations extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new PropagateDependentAnnotations(job, ts, nf));
    	}
    	private PropagateDependentAnnotations(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new PropagateDependentAnnotationsVisitor(job, ts, nf));
    	}
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.PropagateDeclarationAnnotations(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
    		return super.createPass(extInfo);
    	}
    }
    static class PropagateAnnotations extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new PropagateAnnotations(job, ts, nf));
    	}
    	private PropagateAnnotations(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new PropagateAnnotationsVisitor(job, ts, nf));
    	}
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		
//    		l.add(x10Sched.PropagateDependentAnnotations(job));
    		
    		l.add(x10Sched.Disambiguated(job));
    		l.add(x10Sched.TypePropagated(job));
    		l.add(scheduler.TypeChecked(job));
    		l.add(scheduler.ConstantsChecked(job));
    		
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
    		return super.createPass(extInfo);
    	}
    }

    static class X10Boxed extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new X10Boxed(job, ts, nf));
    	}
    	private X10Boxed(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new X10Boxer(job, ts, nf));
    	}
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(scheduler.TypeChecked(job));
    		l.add(scheduler.ConstantsChecked(job));
    		l.add(x10Sched.RegisterPlugins(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}

    	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
    		return super.createPass(extInfo);
    	}
    }

    static class X10Casted extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new X10Casted(job, ts, nf));
    	}
    	private X10Casted(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new X10Caster(job, ts, nf));
    	}
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(scheduler.TypeChecked(job));
    		l.add(scheduler.ConstantsChecked(job));
    		l.add(((X10Scheduler)scheduler).X10Boxed(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
//  		System.out.println("Creating pass for X10Boxed goal...");
    		return super.createPass(extInfo);
    	}
    }

    static class X10ExprFlattened extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new X10ExprFlattened(job, ts, nf));
    	}
    	private X10ExprFlattened(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new ExprFlattener(job, ts, nf));
    	}
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(scheduler.TypeChecked(job));
    		l.add(scheduler.ConstantsChecked(job));
    		l.add(x10Sched.X10Boxed(job));
    		l.add(x10Sched.X10Casted(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
    		return super.createPass(extInfo);
    	}
    }
    static class X10MLTypeChecked extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new X10MLTypeChecked(job, ts, nf));
    	}
    	protected X10MLTypeChecked(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new X10MLVerifier(job, ts, nf));
    	}
    	
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.Parsed(job));
    		l.add(x10Sched.ImportTableInitialized(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	
    	public Collection corequisiteGoals(Scheduler scheduler) {
    		List<Goal> l = new ArrayList<Goal>();
    		l.addAll(super.corequisiteGoals(scheduler));
    		return l;
    	}
    }
    static class X10TypeChecked extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new X10TypeChecked(job, ts, nf));
    	}
    	protected X10TypeChecked(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new TypeChecker(job, ts, nf));
    	}
    	
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.Disambiguated(job));
    		l.add(x10Sched.TypePropagated(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	
    	public Collection corequisiteGoals(Scheduler scheduler) {
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(scheduler.ConstantsChecked(job));
    		l.addAll(super.corequisiteGoals(scheduler));
    		return l;
    	}
    	
    }
    static class X10ConstantsChecked extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new X10ConstantsChecked(job, ts, nf));
    	}
    	 protected X10ConstantsChecked(Job job, TypeSystem ts, NodeFactory nf) {
    	        super(job, new ConstantChecker(job, ts, nf));
    	    }

    	    public Collection prerequisiteGoals(Scheduler scheduler) {
    	    	X10Scheduler x10Sched = (X10Scheduler) scheduler;
    	        List<Goal> l = new ArrayList<Goal>();
    	        l.add(x10Sched.Disambiguated(job));
    	        l.add(x10Sched.TypePropagated(job));
    	        l.addAll(super.prerequisiteGoals(scheduler));
    	        return l;
    	    }

    	    public Collection corequisiteGoals(Scheduler scheduler) {
    	        List<Goal> l = new ArrayList<Goal>();
    	        l.add(scheduler.TypeChecked(job));
    	        l.addAll(super.corequisiteGoals(scheduler));
    	        return l;
    	    }
    	
    }
    public static class CastRewritten extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new CastRewritten(job, ts, nf));
    	}
    	private CastRewritten(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new CastRewriter(job, ts, nf));
    	}
    	
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(scheduler.Disambiguated(job));
    		l.add(scheduler.SupertypesDisambiguated(job));
    		l.add(scheduler.SignaturesDisambiguated(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	 
    }
    static class TypeElaborated extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new TypeElaborated(job, ts, nf));
    	}
    	private TypeElaborated(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new TypeElaborator(job, ts, nf));
    	}
    	
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(scheduler.Disambiguated(job));
    		l.add(scheduler.SupertypesDisambiguated(job));
    		l.add(scheduler.SignaturesDisambiguated(job));
    		l.add(x10Sched.CastRewritten(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	 
    }
    static class TypePropagated extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new TypePropagated(job, ts, nf));
    	}
    	private TypePropagated(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new TypePropagator(job, ts, nf));
    	}
    	
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.TypeElaborated(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	 
    }
    static class AnnotationsChecked extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new AnnotationsChecked(job, ts, nf));
    	}
    	private AnnotationsChecked(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new AnnotationChecker(job, ts, nf));
    	}
    	
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.ReachabilityChecked(job));
    		l.add(x10Sched.ExitPathsChecked(job));
    		l.add(x10Sched.InitializationsChecked(job));
    		l.add(x10Sched.TypeChecked(job));
    		l.add(x10Sched.ConstantsChecked(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	
    }
    static class PropertyAssignmentsChecked extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new PropertyAssignmentsChecked(job, ts, nf));
    	}
    	private PropertyAssignmentsChecked(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new AssignPropertyChecker(job, ts, nf));
    	}
    	
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(x10Sched.ReachabilityChecked(job));
    		l.add(x10Sched.ExitPathsChecked(job));
    		l.add(x10Sched.InitializationsChecked(job));
    		l.add(x10Sched.TypeChecked(job));
    		l.add(x10Sched.ConstantsChecked(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	
    }

    static class X10Expanded extends VisitorGoal {
    	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
    		return scheduler.internGoal(new X10Expanded(job, ts, nf));
    	}
    	private X10Expanded(Job job, TypeSystem ts, NodeFactory nf) {
    		super(job, new X10ImplicitDeclarationExpander(job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory()));
    	}
    	public Collection prerequisiteGoals(Scheduler scheduler) {
    		X10Scheduler x10Sched = (X10Scheduler) scheduler;
    		List<Goal> l = new ArrayList<Goal>();
    		l.add(scheduler.TypeChecked(job));
    		l.add(scheduler.ConstantsChecked(job));
			l.add(x10Sched.PropagateAnnotations(job));
    		l.addAll(super.prerequisiteGoals(scheduler));
    		return l;
    	}
    	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
    		return super.createPass(extInfo);
    	}
    }
    
    protected Options createOptions() {
    	return new X10CompilerOptions(this);
    }
    
    public Map plugins() {
    	if (plugins == null) {
    		return Collections.EMPTY_MAP;
    	}
    	return plugins;
    }
    
	public void addPlugin(String pluginName, CompilerPlugin plugin) {
		if (plugins == null) {
			plugins = new HashMap();
		}
		plugins.put(pluginName, plugin);
	}
	
	public CompilerPlugin getPlugin(String pluginName) {
		if (plugins == null) {
			return null;
		}
		
		return (CompilerPlugin) plugins.get(pluginName);
	}
}
