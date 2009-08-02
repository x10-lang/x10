/*
 * Created on Oct 21, 2005
 */
package com.ibm.wala.cast.x10.tests;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import org.junit.Test;

import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.test.IRTests;
import com.ibm.wala.cast.loader.AstMethod;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.cast.x10.client.X10SourceAnalysisEngine;
import com.ibm.wala.cast.x10.ssa.AsyncInvokeInstruction;
import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.JarFileModule;
import com.ibm.wala.classLoader.SourceFileModule;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.LocalPointerKey;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cfg.BasicBlockInContext;
import com.ibm.wala.ipa.cfg.ExceptionPrunedCFG;
import com.ibm.wala.ipa.cfg.ExplodedInterproceduralCFG;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.analysis.IExplodedBasicBlock;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.collections.Filter;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.collections.Pair;
import com.ibm.wala.util.intset.OrdinalSet;

class ClockInst {

	public String functionName;
	public Set viableClocks;
	public Set successors;

	ClockInst(String func, Set clocks, Set succs) {
		functionName = func;
		viableClocks = clocks;
		successors = succs;
	}
}

class InstHash {

	/*Instruction hash*/
	private HashMap instHash = new HashMap();

	public Set keySet() {
		return instHash.keySet();
	}

	public ClockInst get(String key) {
		return (ClockInst) instHash.get(key);
	}

	/* Insert a new instruction */
	public void insert(String instNum, String function, Set viableclocks,
			Set successors) {
		instHash
		.put(instNum, new ClockInst(function, viableclocks, successors));
	}
}

class ClockHash {

	private HashMap clockHash = new HashMap();

	public Set getKeys() {
		return clockHash.keySet();
	}

	/* Insert a new Clock */
	public void insertNewClock(InstanceKey clockId, String uniqueName) {
		clockHash.put(clockId, uniqueName);
	}

	public String get(InstanceKey ik) {
		return (String) clockHash.get(ik);
	}

	public boolean contains(String ClockId) {
		return clockHash.containsKey(ClockId);
	}

}

public class X10StaAnIRTests extends IRTests {
	protected static List<String> x10SystemModules;
	protected static String x10LibPath = "." + File.separator + "lib";

	static {
		x10SystemModules = new ArrayList<String>();
		x10SystemModules.add(x10LibPath + File.separator + "x10-runtime.jar");

		try {
			// Force X10 version of CAstPrinter to be used...
			Class.forName("com.ibm.wala.cast.x10.translator.X10CAstPrinter");
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

	public X10StaAnIRTests() {
		super("clock optimization tests", null);
    	setTestSrcPath("." + File.separator + "testSrc");
	}

	protected ClassLoaderReference getSourceLoader() {
		return X10SourceLoaderImpl.X10SourceLoader;
	}

	protected JavaSourceAnalysisEngine getAnalysisEngine(
			final String[] mainClassDescriptors) {
		JavaSourceAnalysisEngine engine = new X10SourceAnalysisEngine() {
			protected Iterable<Entrypoint> makeDefaultEntrypoints(
					AnalysisScope scope, IClassHierarchy cha) {
				return Util.makeMainEntrypoints(
						X10SourceLoaderImpl.X10SourceLoader, cha,
						mainClassDescriptors);
			}
		};
		engine.setExclusionsFile(CallGraphTestUtil.REGRESSION_EXCLUSIONS);
		return engine;
	}

	protected String singleJavaInputForTest() {
		return getName().substring(4) + ".x10";
	}

	protected String singleJavaPkgInputForTest(String pkgName) {
		return pkgName + File.separator + getName().substring(4) + ".x10";
	}

	@Override
	protected void populateScope(JavaSourceAnalysisEngine engine,
			Collection<String> sources, List<String> libs) throws IOException {
		super.populateScope(engine, Collections.EMPTY_SET, libs);

		for (String modPath : x10SystemModules) {
			((X10SourceAnalysisEngine) engine)
			.addX10SystemModule(new JarFileModule(new JarFile(modPath)));
		}
		for (String modPath : sources) {
			((X10SourceAnalysisEngine) engine)
			.addX10SourceModule(new SourceFileModule(new File(modPath),
					modPath));
		}
	}
	
	/* Remove wierd characters to make it NuSMV compatible */
	String preprocess(String methodClass, BasicBlockInContext bb)
	{
		if (methodClass.contains("activity")) /* Give a short name */
		methodClass = "activity" + bb.getMethod().hashCode();
		methodClass = methodClass.replace("$", "");
		methodClass = methodClass.replace("<", "");
		methodClass = methodClass.replace(">", "");
		methodClass = methodClass.replace(" ", "");
		methodClass = methodClass.replace("(", "");
		methodClass = methodClass.replace(")", "");
		methodClass = methodClass.replace("/", "");
		return methodClass;
	}
	
	

	/* Am I a clock instruction? */
	private boolean isClockInstruction(SSAInstruction inst) {
		if (inst == null) return false;
		if ((inst.toString().contains("clock()")
				|| inst.toString().contains("doNext()")
				|| inst.toString().contains("drop()")
				|| inst.toString().contains("registered()") || inst.toString()
				.contains("resume()"))
				|| inst.toString().contains("async"))
		{ //System.out.println(inst);
			return true;
		}
		else
			return false;
	}

	/* Get the first clock instruction in a basic block */
	private Integer getFirstClockInstOfBB(BasicBlockInContext bb,
			SSAInstruction[] instructions) {
		int start = bb.getFirstInstructionIndex();
		int end = bb.getLastInstructionIndex();
		for (int j = start; j > 0 && j <= end; j++) {
			if (instructions[j] != null) {
				if (isClockInstruction(instructions[j]))
					return new Integer(j);
			}
		}
		return null;
	}

	/* Async have to be handled in a different manner. They are to be
	 * viewed as an extra path in the program, and not like function
	 * calls
	 */
	private void handleAsyncCase(BasicBlockInContext bb,
			ExplodedInterproceduralCFG cfg, Set successors, Set trace, PointerAnalysis pa) {
		Iterator i = cfg.getSuccNodes(bb);

		while (i.hasNext()) {
			BasicBlockInContext bbNext = (BasicBlockInContext) i.next();
			if (!trace.contains(bbNext)) {
				if (!(bbNext.isEntryBlock()))// hack to async body edges
					findSuccessorClockInst(null, bbNext, cfg, successors, trace, pa);
			}
		}
	}

	/* Find the first successor instruction of a given instruction */
	private void findSuccessorClockInst(Integer instNum,
			BasicBlockInContext bb, ExplodedInterproceduralCFG cfg,
			Set successors, Set trace, PointerAnalysis pa
			) {
		
		SSAInstruction[] instructions = bb.getNode().getIR().getInstructions();
		String methodClass = bb.getMethod().getName() + ""
		+ bb.getMethod().getDeclaringClass().getName();

		if (instNum == null) {
			Integer next = null;
			trace.add(bb);
			next = getFirstClockInstOfBB(bb, instructions);

			if (next != null) // In the same block
			{
				/*Short cutting*/
				if (instructions[next].toString().contains("async")) {
					handleAsyncCase(bb, cfg, successors, trace, pa);

				}
				
				/*Preprocessing to make it NuSMV compatible */
				methodClass = preprocess(methodClass, bb);
				
					
				Position p = ((AstMethod)(bb.getNode().getMethod())).getSourcePosition(next);
				String location = "_" + p.getFirstLine() + "_" + p.getFirstCol();
				successors.add(methodClass + location);
				return;
			}
		}

		if (bb.isExitBlock() && methodClass.contains("activity"))
			return; // end async with terminate

		Iterator i = cfg.getSuccNodes(bb);

		boolean skipCrossInvoke = false;
		
		Collection normalSuccessors = null;
		SSAInstruction last = bb.getLastInstruction();
		if (last instanceof SSAAbstractInvokeInstruction) {
			
			if (cfg.getSuccNodeCount(bb) > 1) {
				skipCrossInvoke = true;
			
			}
									
			normalSuccessors = cfg.getCFG(bb.getNode())
			.getNormalSuccessors(
					(IExplodedBasicBlock) bb.getDelegate());
		
			/* Code to check if any of the instructions use clock in the called method  */
			/*SSAInstruction [] insts = bb.getNode().getIR().getInstructions();
			for (int j = 0; j < insts.length; j++)
				if(this.isClockInstruction(insts[j])) 
					{
						functionCallTakesInClock = true;
						break;
					}*/
			
			
			/* Checks if the function takes clock as parameter*/
			/*for (int count = 0; count < last.getNumberOfUses(); count++) {
				//clockId = methodClass + inst.getUse(count);
				
				OrdinalSet<InstanceKey> os = pa
				.getPointsToSet(new LocalPointerKey(bb
						.getNode(), last.getUse(count)));
				Iterator<InstanceKey> it = os.iterator();
				while (it.hasNext()) {
					InstanceKey ik = it.next();
					if (ik.getConcreteType().toString()
							.contains("clock"))
						functionCallTakesInClock = true;
						break;
				}

			}*/
		}

		while (i.hasNext()) {
			BasicBlockInContext bbNext = (BasicBlockInContext) i.next();
			

			/* If we have already traversed it */
			if (trace.contains(bbNext)) 
				continue;
	
			/*For normal calls*/
			/*If a block has a call then we have only the body of the invoked function as successors*/
			/*inline functions - Reject other edges*/
			
		
	
			
			if (skipCrossInvoke && bb.getNode() == bbNext.getNode()
						&& normalSuccessors.contains(bbNext.getDelegate())){
						
					continue;
			}
			
		
			
			/* Get rid of junk calls */
			if(bbNext.getMethod().isNative() || bbNext.getMethod().isClinit())
					continue;
				
			findSuccessorClockInst(null, bbNext, cfg, successors, trace, pa);
							
		}
	}
	
	/* Run NuSMV to check for a particular specification in the NuSMV file */
	/*private boolean checkSpec(int specNum)
	{
			Boolean spec = null;
			int attempts = 0;
			do {
				try{
					Process p = Runtime.getRuntime().exec("NuSMV -n " + specNum + " /tmp/output.nusmv");
					p.waitFor();
					BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
					DataInputStream dis = new DataInputStream(bis);
		
					while(dis.available() != 0)
					{
						String line = dis.readLine();
						if (line.contains("TL Counterexample"))
						{ 
							spec = false;
							break;
						}
						else if (line.contains("is true"))
						{
							spec = true;
							break;
						}
					}
					dis.close();
					bis.close();
					if (spec == null) throw new Exception (); // Parse error case 
					System.out.println("--" + spec);
					} catch (Exception e){
						System.out.println("--");
						attempts ++;
						spec = null;
				}
			} while(spec == null && attempts <= 3); // keep retrying by rerunning NUSMV
			return spec;
		}*/
	
	
	
	
	
/* Build the NuSMV file out of the instruction hash */
	private void buildNuSMV(InstHash iHash, ClockHash cHash, NuSMVFile n) {

		X10StaAnPrinter p = new X10StaAnPrinter();
		Iterator<InstanceKey> cIt = cHash.getKeys().iterator();
		while (cIt.hasNext()) {
			InstanceKey ik = cIt.next();
			Automaton a = new Automaton();
			a.setName(cHash.get(ik));
			Set instNums = iHash.keySet();
			Iterator instIt = instNums.iterator();
			while (instIt.hasNext()) {
				String instNum = (String) instIt.next();
				ClockInst cInst = (ClockInst) iHash.get(instNum);
				String variable = "";
				CaseStmt c = new CaseStmt();
				if (cInst.functionName == "async") {
					variable = cInst.functionName + "Clocked_" + instNum;
					a.addViableValue(variable);
					c.addClause("", a.name, "=", variable);
					variable = cInst.functionName + "Unclocked_" + instNum;
					a.addViableValue(variable);
					c.addClause("|", a.name, "=", variable);

				} else if (cInst.viableClocks.contains(ik)) {
					variable = cInst.functionName + "_" + instNum;
					a.addViableValue(variable);
					c.addClause("", a.name, "=", variable);
					if (cInst.functionName == "create")
						a.setInitValue(cInst.functionName + "_" + instNum);

					if (cInst.viableClocks.size() > 1) {
						variable = "nop_" + instNum;
						a.addViableValue(variable);
						c.addClause("|", a.name, "=", variable);
					}
				} else {
					variable = "nop_" + instNum;
					a.addViableValue(variable);
					c.addClause("", a.name, "=", variable);
				}
				/*Get all successors*/

				Iterator succIt = cInst.successors.iterator();
				if (cInst.successors.size() == 0) {
					a.addViableValue("terminate");
					c.addResult("terminate");
				} else
					while (succIt.hasNext()) {
						String succInstNum = (String) succIt.next();
						ClockInst succInst = iHash.get(succInstNum);
						String result = "";
						if (succInst.functionName == "async") {
							Iterator<Set> it = succInst.viableClocks.iterator();
							boolean definitelyClocked = false;
							while (it.hasNext()) {
								Set individualViableClocks = it.next();
								if (individualViableClocks.contains(ik)) {
									result = succInst.functionName + "Clocked_"
									+ succInstNum;
									c.addResult(result);
									if (individualViableClocks.size() == 1) {
										definitelyClocked = true;
										break;
									}
								}

							}
							if (definitelyClocked == false) {
								result = succInst.functionName + "Unclocked_"
								+ succInstNum;
								c.addResult(result);

							}

						} else {
							if (succInst.viableClocks.contains(ik)) {

								result = succInst.functionName + "_"
								+ succInstNum;
								c.addResult(result);
								if (succInst.viableClocks.size() > 1) {
									result = "nop_" + succInstNum;
									c.addResult(result);
								}
							} else {
								result = "nop_" + succInstNum;
								c.addResult(result);
							}
						}
					}
				a.addCaseStmt(c);
			}
			n.addAutomaton(a);
		}

	}

	/* Play with the IR to build the instruction hash */
	private void dumpIR(CallGraph cg, boolean assertReachable,
			PointerAnalysis pa, long startTime) throws IOException {
		Set<IMethod> unreachable = HashSetFactory.make();
		IClassHierarchy cha = cg.getClassHierarchy();
		IClassLoader sourceLoader = cha.getLoader(getSourceLoader());
		ClockHash cHash = new ClockHash();
		InstHash iHash = new InstHash();
		NuSMVFile n = new NuSMVFile();
		
		super.dumpIR(cg, assertReachable);
		
		ExplodedInterproceduralCFG ipCFG = new ExplodedInterproceduralCFG(cg, new Filter<CGNode>() {
			public boolean accepts(CGNode o) {
				try {
					ControlFlowGraph cfg = o.getIR().getControlFlowGraph();
					return cfg.getNormalPredecessors(cfg.exit()).size() > 0;
				} catch (NullPointerException e) {
					return false;
				}
			}			
		}) {
			@Override
			public ControlFlowGraph<SSAInstruction, IExplodedBasicBlock> getCFG(CGNode n)
			throws IllegalArgumentException {
				ControlFlowGraph<SSAInstruction, IExplodedBasicBlock> x = super.getCFG(n);

				/*	if (n.getIR() != null) {
						System.err.println( n );
						System.err.println( n.getIR() );
				}*/
				if (x == null || x.getMethod().isSynthetic()) {
					return x;
				} else {
					//System.out.println(n.getMethod().getName());
					return ExceptionPrunedCFG.make(x);
				}
			}
		};

		//Trace.println(ipCFG);
	
		//System.out.println(ipCFG.getNumberOfNodes());
		
		System.out.println("-- Abstraction");
		for (int i = 0; i < ipCFG.getMaxNumber(); i++) {

			//System.out.println("i =" + i + ipCFG.getNode(i));

			BasicBlockInContext bb = ipCFG.getNode(i);
		
			int start = bb.getFirstInstructionIndex();
			int end = bb.getLastInstructionIndex();

			SSAInstruction[] instructions = bb.getNode().getIR()
			.getInstructions();

			for (int j = start; j >= 0 && j <= end; j++) {
				if (instructions[j] != null) {
				
					Set successors = new HashSet();
					if (isClockInstruction(instructions[j])) {
						//System.out.println( " " + ((AstMethod)(bb.getNode().getMethod())).getSourcePosition(j));
						String functionName = "";
						String clockId = "";
						SSAInstruction inst = instructions[j];
						Position p = ((AstMethod)(bb.getNode().getMethod())).getSourcePosition(j);
						String location = "_" + p.getFirstLine() + "_" + p.getFirstCol();
						findSuccessorClockInst(new Integer(j + 1), bb, ipCFG,
								successors, new HashSet(), pa);
						//Set viableclocks = new HashSet ();
						Set paViableClocks = new HashSet();
						String methodClass = bb.getMethod().getName() + ""
						+ bb.getMethod().getDeclaringClass().getName();
						
						
						if (inst.toString().contains("clock()")) {
					
							clockId = "CLOCK" + location;
												
							//System.out.println(clockId);
							functionName = "create";
							//uClocks.insert
							// viableclocks = cHash.getUniqueReferences(clockId);
							OrdinalSet<InstanceKey> os = pa
							.getPointsToSet(new LocalPointerKey(bb
									.getNode(), inst.getDef()));
							Iterator<InstanceKey> it = os.iterator();
							while (it.hasNext()) {
								InstanceKey ik = it.next();
								if (ik.getConcreteType().toString().contains(
								"clock")) {
									paViableClocks.add(ik);
									cHash.insertNewClock(ik, clockId);
								}
							}
						} else if (!inst.toString().contains("async")) {
							int use = inst.getNumberOfUses();
							
							
							for (int count = 0; count < inst.getNumberOfUses(); count++) {
								//clockId = methodClass + inst.getUse(count);

								OrdinalSet<InstanceKey> os = pa
								.getPointsToSet(new LocalPointerKey(bb
										.getNode(), inst.getUse(count)));
								Iterator<InstanceKey> it = os.iterator();
								while (it.hasNext()) {
									InstanceKey ik = it.next();
									if (ik.getConcreteType().toString()
											.contains("clock"))
										paViableClocks.add(ik);
								}

							}

							//clockId = "" + inst.getUse(0);
							// Trace.println("VC: " + viableclocks + " PAVC: " + paViableClocks );
							if (inst.toString().contains("doNext()"))
								functionName = "next";
							else if (inst.toString().contains("drop()"))
								functionName = "drop";
							else if (inst.toString().contains("registered()"))
								functionName = "registered";
							else if (inst.toString().contains("resume()"))
								functionName = "resume";

						} else if (inst.toString().contains("async")) {
							//System.out.println("async found");
							
							int clocks[] = ((AsyncInvokeInstruction) inst)
							.getClocks();
							//System.out.println(clocks.length);
							//System.out.println(((AsyncInvokeInstruction)inst).);
							for (int count = 0; count < clocks.length; count++) {
								//System.out.println(clocks[count]);
								OrdinalSet<InstanceKey> os = pa
								.getPointsToSet(new LocalPointerKey(bb
										.getNode(), clocks[count]));
								Iterator<InstanceKey> it = os.iterator();
								Set individualViableClocks = new HashSet();
								while (it.hasNext()) {
									InstanceKey ik = it.next();
									individualViableClocks.add(ik);
								}
								//System.out.println("clcock = " + clocks[count] + " individualViableClocks = " + individualViableClocks);
								paViableClocks.add(individualViableClocks);

							}
							functionName = "async";

						}

						/* Preprocessing for NuSMV compatibliltity */
						methodClass = preprocess(methodClass, bb);
					
						//System.out.println(methodClass);
						iHash.insert(methodClass + location, functionName,
								paViableClocks, successors);
						System.out.println("--" + methodClass + j + " "
								+ functionName + " " + paViableClocks + " "
								+ successors);
					}
				}
			}
		}
		buildNuSMV(iHash, cHash, n);
		n.buildNuSMVFile();
		long absTime = System.currentTimeMillis() - startTime;
		n.runNuSMVFile(cHash);
		long nusmvTime = System.currentTimeMillis() - startTime - absTime;
		System.out.println ("-- Time: Abstraction = " + absTime + " NuSMV = " +
				nusmvTime + " Total = " + (absTime + nusmvTime));
	}

	public Pair runTest(Collection<String> sources, List<String> libs,
		String[] mainClassDescriptors, boolean assertReachable) throws IOException, IllegalArgumentException, CancelException {
		
		long startTime = System.currentTimeMillis();
		
		JavaSourceAnalysisEngine engine = getAnalysisEngine(mainClassDescriptors);

		populateScope(engine, sources, libs);

		CallGraph callGraph = engine.buildDefaultCallGraph();

		dumpIR(callGraph, assertReachable, engine.getPointerAnalysis(), startTime);
		
		

		return Pair.make(callGraph, engine.getPointerAnalysis());
	}

	@Test public void testClock() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	
	@Test public void testClockPipeline()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
		
	@Test public void testClockKernel()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	@Test public void testClockEdmiston() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	

	@Test public void testClockAllReductionBarrier()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	
	@Test public void testClockLUOverlap()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	@Test public void testClockJGFMolDynBenchSizeA()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	
	@Test public void testClockSieve()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	@Test public void testClockPascalTriangle()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}

	
	@Test public void testClockFFTDist()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	
	@Test public void testClockQueensList()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	@Test public void testClockLinearSearch()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
		
	@Test public void testClockTest1() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	@Test public void testClockTest2() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	@Test public void testClockTest3()  throws Exception{
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	@Test public void testClockTest4() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);

	}
	
	@Test public void testClockTest5() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest6() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}

	@Test public void testClockTest7_MustFailTimeout() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest8_MustFailRun() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest9() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest10() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest11() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest12() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest13() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest14() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest15() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
	
	@Test public void testClockTest16() throws Exception {
		Pair p = runTest(singleTestSrc(), rtJar, simpleTestEntryPoint(), false);
	}
}




