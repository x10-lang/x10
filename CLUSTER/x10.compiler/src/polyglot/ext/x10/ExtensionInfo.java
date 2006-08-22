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
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.ext.jl.JLScheduler;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.query.QueryEngine;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.visit.X10Boxer;
import polyglot.ext.x10.visit.X10Qualifier;
import polyglot.ext.x10.visit.X10ImplicitDeclarationExpander;
import polyglot.frontend.Compiler;
import polyglot.frontend.CyclicDependencyException;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Pass;
import polyglot.frontend.Scheduler;
import polyglot.frontend.VisitorPass;
import polyglot.frontend.goals.CodeGenerated;
import polyglot.frontend.goals.ConstantsChecked;
import polyglot.frontend.goals.Goal;
import polyglot.frontend.goals.VisitorGoal;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.visit.NodeVisitor;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

/**
 * Extension information for x10 extension.
 */
public class ExtensionInfo extends polyglot.ext.jl.ExtensionInfo {
    static final boolean DEBUG_ = false;
    static {
        // force Topics to load
        Topics t = new Topics();
    }
    
    public static String clock = "clock";

    static {
        Report.topics.add(clock);
    }

    public polyglot.main.Version version() {
    	return new Version();
    }
    public String defaultFileExtension() {
        return "x10";
    }

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
            X10Parser x10_parser = new X10Parser(x10_lexer, ts, nf, source, eq); // Create the parser
            x10_lexer.lexer(x10_parser);
            return x10_parser; // Parse the token stream to produce an AST
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Could not parse " + source.path());
    }

    protected NodeFactory createNodeFactory() {
        X10NodeFactory_c nf = new X10NodeFactory_c();
        X10NodeFactory_c.setNodeFactory(nf);
        return nf;
    }

    protected TypeSystem createTypeSystem() {
        X10TypeSystem ts = new X10TypeSystem_c();
        X10TypeSystem_c.setTypeSystem(ts);
        return ts;
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

    static class X10Scheduler extends JLScheduler {
	X10Scheduler(ExtensionInfo extInfo) {
	    super(extInfo);
	}
	public Goal CodeGenerated(final Job job) {
//	    System.out.println("creating CodeGenerated Goal for " + job.source().name());
	    return X10CodeGenerated.create(this, job);
	}
	public Goal X10Boxed(final Job job) {
	    return X10Boxed.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	}
	public Goal X10Qualified(final Job job) {
	    return X10Qualified.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	}
	public Goal X10Expanded(final Job job) {
	    return X10Expanded.create(this, job, extInfo.typeSystem(), extInfo.nodeFactory());
	}
    }

    static class X10CodeGenerated extends CodeGenerated {
	public static Goal create(Scheduler scheduler, Job job) {
	    return scheduler.internGoal(new X10CodeGenerated(job));
	}
	private X10CodeGenerated(Job job) {
	    super(job);
	}
	public Collection prerequisiteGoals(Scheduler scheduler) {
	    X10Scheduler x10Sched= (X10Scheduler) scheduler;
	    List l = new ArrayList();
	    l.add(x10Sched.X10Boxed(job));
	    l.add(x10Sched.X10Qualified(job));
	    l.add(x10Sched.X10Expanded(job));
	    l.addAll(super.prerequisiteGoals(scheduler));
	    return l;
	}
//	public void setState(int state) {
//	    super.setState(state);
//	    if (state == Goal.REACHED)
//		System.out.println("Reached CodeGenerated goal.");
//	}
    }

    static class X10Boxed extends VisitorGoal {
	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
	    return scheduler.internGoal(new X10Boxed(job, ts, nf));
	}
	private X10Boxed(Job job, TypeSystem ts, NodeFactory nf) {
	    super(job, new X10Boxer(job, ts, nf));
	}
	public Collection prerequisiteGoals(Scheduler scheduler) {
	    List l = new ArrayList();
	    l.add(scheduler.TypeChecked(job));
	    l.addAll(super.prerequisiteGoals(scheduler));
	    return l;
	}
	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
//	    System.out.println("Creating pass for X10Boxed goal...");
	    return super.createPass(extInfo);
	}
    }

    static class X10Qualified extends VisitorGoal {
	public static Goal create(Scheduler scheduler, Job job, TypeSystem ts, NodeFactory nf) {
	    return scheduler.internGoal(new X10Qualified(job, ts, nf));
	}
	private X10Qualified(Job job, TypeSystem ts, NodeFactory nf) {
	    super(job, new X10Qualifier(job, ts, nf));
	}
	public Collection prerequisiteGoals(Scheduler scheduler) {
	    List l = new ArrayList();
	    l.add(scheduler.TypeChecked(job));
	    l.addAll(super.prerequisiteGoals(scheduler));
	    return l;
	}
	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
//	    System.out.println("Creating pass for X10Qualified goal...");
	    return super.createPass(extInfo);
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
	    List l = new ArrayList();
	    l.add(scheduler.TypeChecked(job));
	    l.addAll(super.prerequisiteGoals(scheduler));
	    return l;
	}
	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
//	    System.out.println("Creating pass for X10Expanded goal...");
	    return super.createPass(extInfo);
	}
    }

    protected Options createOptions() {
        return new X10CompilerOptions(this);
    }
}
