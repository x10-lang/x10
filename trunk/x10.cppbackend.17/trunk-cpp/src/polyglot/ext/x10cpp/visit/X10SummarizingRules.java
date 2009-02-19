/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created: August 2007
 */
package polyglot.ext.x10cpp.visit;

import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.For;
import polyglot.ast.Loop;
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Throw;
import polyglot.ast.TypeNode;
import polyglot.ast.While;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10cpp.types.X10CPPContext_c;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.Type;
import polyglot.visit.ContextVisitor;
import polyglot.visit.Translator;


/**
 *
 * @author Pradeep Varma
 */

/**
This comprises the Inter-Procedural Analysis Framework using Class Hierarchy Analysis to map calls to methods.  The framework does one pass of the program to create a set of summaries of user-specified constructs and all methods. Each call is
associated with a set of candidate methods under closed world assumptions and class hierarchy analysis based matching. Calls associated with singleton-method sets are of particular interest since the control flow of such calls is
fully understood. In particular, in the context of the initial implementation for FT, all calls to X10-implemented methods generate singleton sets. <p>

Each summary stores within it a set of calls found while traversing its corresponding construct. Non-local properties for the summary (e.g. is a function mutually recursive) can optionally be
established by traversing the summaries graph reachable from the given summary (edges of the graph are obtained recursively from the summary call points). The set of summaries 
generated after the first pass can be iterated upon variously (e.g. methodwise, constructwise) for various fixpoint computations adding non-local properties to the summaries. 
Non-local properties can also be added to the summaries by further passes over the program AST and dealing with the call points as found in the program context. <p> 

The IPA framework is designed to take user-specified analyzer plug-ins/rules, which are manually plugged in for now. The user specifies a set of features to be searched for in the AST (such as asyncs, loops etc.)
by defining a class that implements the Feature interface.  Summarizers that create summaries of another (overlapping or non-overlapping) set of constructs are also specified (as
implementations of interface Summarizer). <p>

The IPA framework creates summaries using the user-specified features and summarizers as follows: <p>

<ol>
<li> Instantiate summarizers and features.

<li> In the visitor pattern for the pass over AST, check when visiting a node whether: 
<ol> <li> any summarizer is applicable.  If so, then execute the corresponding summaryStart at the 
       beginning of visiting the node.  This creates a summary object for the summarizer.  At the end of the node visit, summaryEnd is executed that completes the population of the local summary
       properties.
<li> it is a feature node : run the features test method.  If it succeeds, store the node in the appropriate summary objects for it. The node may qualify as multiple features
       and then would be stored multiple times.
</ol>

<li> Do a global fixpoint calculation over summaries, computing the global properties of each summary.  Each summarizer provides a method for its global properties.
</ol>

Although different summarizers may be interested in different sets of features, all features that are found are stored in all summaries in the context chain.  So the summarizers have to be written
to handle supersets of the features they're interested in (due to this interference of other summarizer interests). <p>

Since a call can map to a set of more than one methods, a summarizer can try to discern useful properties regardless of this imprecision by combining the contributions of the methods in a may or must
manner (e.g. the call is SPMDizable if all methods are SPMDizable, an async is not inlineable if any of the methods for a call within it contains a loop).

 */

public class X10SummarizingRules {
	
	static int debugLevel = 0;
	public static boolean debug() { return debugLevel > 0;} 
	
	/** This is the bag of features to be searched for, summarizers which will summarize, the summaries made, 
	    the mapping from calls to function summaries, and interface functions with PrettyPrinter which uses the collection of information. 
	*/
	public static class Collection {
		/** features to be searched for */
		public List <Feature> features;                         
		/** summarizers that will summarize */
		public List <Summarizer> summarizers;					
		/** summaries made upon visiting AST */
		public List <Summary> summaries;   						
		public Node topClass;
		/** mapping from calls to function summaries */
		public Relations callToSummaries ;  					
		public Summary getSummary(Node n) {

			for (Summary s : summaries) if (s.summaryOf == n) return s;
			return null;
		}
		
		private List <ProcedureInstance> stack = new LinkedList<ProcedureInstance> ();
		
		/** Interface with PrettyPrinter.  This adds a call to call stack unless it starts a recursion.  In this case, force == true adds the call still
		returns true if successful;  */
        public boolean addCall(Node call, boolean force) {
        	boolean found = false;
        	ProcedureInstance mi = null;
        	
        	if (call instanceof Call) mi = (ProcedureInstance) ((Call) call).methodInstance();
        	if (call instanceof ConstructorCall) mi = (ProcedureInstance) ((ConstructorCall) call).constructorInstance();
        	

        	for (ProcedureInstance i : stack)
        		if (i == mi) {found = true; break;}
        	
        	// summarize result
        	if (found) {
        		List <Summary> s = callToSummaries.summariesFor(call);
        		if (! (s == null || s.size() != 1 || s.get(0).library)) {
        			s.get(0).addGlobalProperty("recursive", "true");
        		}
        	}
        	
        	if (found && ! force) return false;
        	stack.add(mi);
        	return true;
        }
        
        /** Interface with PrettyPrinter.  Returns true if successful; */
        public boolean removeCall(Node call) {
        	ProcedureInstance mi = null;
        	
        	if (call instanceof Call) mi = (ProcedureInstance) ((Call) call).methodInstance();
        	if (call instanceof ConstructorCall) mi = (ProcedureInstance) ((ConstructorCall) call).constructorInstance();
        	
        	if (stack.get(stack.size() - 1) != mi) return false;
        	stack.remove(stack.size() - 1);
        	return true;
        }
        
        /** Interface with PrettyPrinter. Returns true if the target is in a library. */
        public boolean isLibrary(Call call) {
        	List <Summary> s = callToSummaries.summariesFor(call);
        	return (s != null && s.size() == 1 && s.get(0).library);
        }
        
        /** Interface with PrettyPrinter. Does not inspect stack, but queries summary if call has previouly been found to be recursive. */
        public boolean isCallRecursiveCached(Node call) {
        	List <Summary> s = callToSummaries.summariesFor(call);
        	if (s == null || s.size() != 1 || s.get(0).library) return false;
        	
        	String property = s.get(0).lookupGlobalProperty("recursive");
        	if (property == null) return false;
        	return property.equals("true");
        	
        }
        
        /** Interface with PrettyPrinter. Returns null except when target is non virtual whose AST is available (non library). */
        public MethodDecl_c getDecl(Call call) {
        	List <Summary> l = callToSummaries.summariesFor(call);
        	if (l == null) return null;
        	List <Summary> s = new LinkedList <Summary> ();
        	for (Summary sum : l) {
        		if (sum.library) return null;
        		if (! (((MethodDecl_c) sum.summaryOf).flags().flags().isAbstract())) 
        			s.add(sum);
        	}
        	if (s.size() != 1 ) return null;  // No need to check for nonVirtual, given CHA handles virtual functions.
        	return (MethodDecl_c) s.get(0).summaryOf;
        	}
        
        /** Interface with PrettyPrinter. Returns null except when target is fixed and whose AST is available (non library). */
        public ConstructorDecl getDecl(ConstructorCall call) {
        	List <Summary> s = callToSummaries.summariesFor(call);
        	if (s == null || s.size() != 1 || s.get(0).library) return null;
        	return (ConstructorDecl) s.get(0).summaryOf;
        	}
        
        public void printCallMatches() {
        	
        	System.out.println("\n \n \n Set of calls and decl matches made for the program \n");

        	for (Summary s: summaries) {
        		List<Node> calls = s.lookupFeature("CallFeature");
        		if (calls == null) continue;

        		for (Node c : calls) {
        			Call call = (Call) c;
        			MethodDecl decl = getDecl(call);
        			if (debugLevel > 1) System.out.println("Call_c discriminator: " + call.methodInstance().container() + call.methodInstance().signature() );
        			if (decl == null) {if (debugLevel > 1)  System.out.println("No MethodDecl_c available");}
        			else System.out.println("MethodDecl_c discriminator: " + decl.methodDef().container() + "." +
        					decl.methodDef().signature() + " flags: " + decl.flags().flags());
        		}
        	}
        }
		
		public Collection (List <Feature> f, List <Summarizer> sz, List <Summary> s, Node n) {
			features = f;
			summarizers = sz;
			summaries = s;
			topClass = n;
		}
	}
	
	/** The feature base class.  Extensions of this define syntactic features to be searched for in an AST. */
	public static abstract class Feature {
		public abstract boolean test(Node n);
		public void store (String featureName, Node feature, X10CPPContext_c c) {
			if (c == null) return;  // returns when runs out of outer contexts.
			if (debug()) {
				System.out.println("Storing Featurename: " + featureName + "node: " + feature);
				System.out.println("Context ID: " + c.hashCode());
				System.out.println("context_c: " + c);
			}
			
			if (c.summaries != null) {

				for (Summary s : c.summaries) {
					if (debug()) System.out.println("Summary of" + s.summaryOf);
					s.addFeature(featureName, feature);
				}
			}
			store(featureName, feature, c.outerContext());	
		}
	}
	
	/** The summarizer base class.  Extensions of this define handler objects used to create summaries (of features found) for individual syntactic entities. */
	public static abstract class Summarizer {
		public abstract void adder(List <Summary> summaries, Summary s);
		public abstract boolean applicable(Node n);
		public Summary summaryStart(Node n, X10CPPContext_c c, X10CPPContext_c outermost) { 
			// sets up context with a summary for storage of features;
			if (debug()) System.out.println("summaryBegin for " + n.getClass().getSimpleName());
			if (c.summaries == null) c.summaries = new LinkedList <Summary> ();
			Summary s = new Summary(this, n, outermost.harvest);
			adder(c.summaries, s);
			outermost.harvest.summaries.add(s);
			
			return s;
		}
		
		public void summaryEnd(Summary s) {
			computeLocal(s);
		}
		
		/** this method is guaranteed to be executed sometime soon after summary s's node traversal has completed.  Soon is a best efforts guarantee. */
		public abstract void computeLocal(Summary s);
		
		/** this method is guaranteed to be executed after all summaries and their computeLocal properties have been computed. */
		public abstract void computeGlobal(Summary s);          // computes global properties of the summary
	} 
	
	/** Summary structure for each summarization done by a summarizer. */
	public static class Summary {
		/** An Association List class */
		public static class Association <T>{
			public String key; 
			public T data; 
			Association (String k, T d) {
				key = k;
				data = d;
			}
			public String toString() {
				return key + "=" + data;
			}
		}
		
		/**		 the summarizer that created this summary. */
		public Summarizer summarizer;	
		/**		 the entity being summarized */
		public Node summaryOf;			
		/**		 the collection the summary belongs to. */
		public Collection harvest;      
		/**		 whether this summary is one of a library function */
		public boolean library;	
		/**		 the type signature of the library function, if applicable. */
		public String libraryFuncDiscriminator;			
		//public LinkedList <Node> methodCalls = new LinkedList <Node>();
		//public LinkedList <Node> constructorCalls = new LinkedList <Node>();
		private List <Association <List <Node>>> features = new LinkedList <Association <List <Node>>> ();
		private List <Association <String>> localProperties = new LinkedList <Association <String>> ();
		private List<Association <String>> globalProperties = new LinkedList <Association <String>> ();
		
		public void printSummary() {
			if (! library ) {
			System.out.println("printSummary: Summary is of: " + summaryOf.getClass().getSimpleName() + " full summary object: "+ this);
			if (summaryOf instanceof MethodDecl) System.out.println("summary of method: " 
					+ ((MethodDecl) summaryOf).methodDef().signature());
			/* System.out.println("containing calls: ");
			ListIterator <Node> lm = methodCalls.listIterator(0);
			while (lm.hasNext()) {
				Call_c m = (Call_c) lm.next();
				System.out.println("        method call: " + m.getClass().getSimpleName() + m.target());
			}
			ListIterator <Node> lc = constructorCalls.listIterator(0);
			while (lc.hasNext()) {
				ConstructorCall c = (ConstructorCall) lc.next();
				System.out.println("        constructor call: " + c.getClass().getSimpleName());
			} */

			for (Association <List <Node>> f : features) {
				System.out.println(" Feature Name: " + f.key);

				for (Node d: f.data) {
					System.out.println("     NodeName is: " + d.getClass().getSimpleName() + " Node is: " + d);
				}
			}

			for (Association<String> l : localProperties) {
				System.out.println(" Local Property Name: " + l.key + " property: " + l.data);
			}

			for (Association <String> g : globalProperties) {
				System.out.println(" Global Property Name: " + g.key + " property: " + g.data);
			}
			}
			else {
				System.out.println ("printSummary: Library function summary: " + libraryFuncDiscriminator);

				for (Association <String> l : localProperties) {
					System.out.println(" Local Property Name: " + l.key + " property: " + l.data);
				}

				for (Association<String> g : globalProperties){
					System.out.println(" Global Property Name: " + g.key + " property: " + g.data);
				}
				
			}
		}
		
		public void addFeature(String featureName, Node feature) { 
			if (library) {if (debug())System.err.println("Attempting to add feature to a library summary"); return ;}
			List <Node> f = lookupFeature(featureName);
			if (f == null) {
				List <Node> a = new LinkedList <Node>();
				a.add(feature);
				features.add(new Association <List <Node>>(featureName, a));
			}
			else 
				if (! f.contains(feature)) f.add(feature);
				else if (debug()) System.out.println("Warning: feature: " + feature + "being added repeatedly to Summary: " + this);
			
		}
		
		public List <Node> lookupFeature(String featureName) { 
//			if (featureName.equals("Call_c")) return methodCalls;
//			if (featureName.equals("New_c")) return methodCalls;
			/*
			ListIterator <Association <LinkedList <Node>>> l = features.listIterator(0);
			while (l.hasNext()) {
				Association <LinkedList <Node>> pair = l.next();
				*/
			for (Association<List<Node>> pair : features) {
				if (pair.key.equals(featureName)) return pair.data;
			}
			return null;
		}
		
		void addLocalProperty(String propertyName, String property) { 
			String p = lookupLocalProperty(propertyName);
			if (p == null) {
				localProperties.add(new Association <String>(propertyName, property));
			}
			else System.out.println("Warning: Local Property: " + property + " being added repeatedly to Summary: " + this);
		}
		public String lookupLocalProperty(String propertyName) { 

			for (Association <String> pair : localProperties) {
				if (pair.key.equals(propertyName)) return pair.data;
			}
			return null;
			
		}
		
		void addGlobalProperty(String propertyName, String property) { 
			String p = lookupGlobalProperty(propertyName);
			if (p == null) {
				globalProperties.add(new Association <String>(propertyName, property));
			}
			else System.out.println("Warning: Global Property: " + property + "being added repeatedly to Summary: " + this);
		}
		public String lookupGlobalProperty(String propertyName) { 

			for (Association <String> pair : globalProperties) {
				if (pair.key.equals(propertyName)) return pair.data;
			}
			return null;
			
		}
		
		public static Summary findSummary(Node n, List <Summary> summaries) {
			/*
			ListIterator <Summary> si = summaries.listIterator(0);
			while (si.hasNext()) {
				Summary s = si.next();
				*/
			for (Summary s : summaries) {
				if (s.summaryOf == n) return s;
			}
			return null;
		}
		
		/**  for summaries created by a summarizer */
		Summary (Summarizer sz, Node n, Collection c) {
			summarizer = sz;
			summaryOf = n;
			harvest = c;
			library = false;
		}
		
		/** used for library function summaries -- no summarizer involved. */
		Summary (Collection c, String funcDiscriminator) {
			libraryFuncDiscriminator = funcDiscriminator;
			library = true;
			harvest = c;
		}
	}
	
	/** Relations represents the mapping from calls to functions and its computation using Class Hierarchy Analysis.  */ 
	public static class Relations {
		/** A Relation object maps a method or constructor call to a set of methods or constructors */
		private static class Relation {
			Node call; 
			List <Summary> summaries; 
			Relation (Node c, List <Summary> s) {
				call = c;
				summaries = s;
			}
			public String toString() {
				return "{" + call + "}->" + summaries;
			}
		}
		
		private static class Tree {
			// Represents class hierarchy information up to a TypeNode parent
			// Note that interfaces are not modeled since only MethodDecls with bodies are interesting.
			// TODO -- indexing the class hierarchy via an interface target is not supported.  
			//         Proper fix: Fix this by decomposing the interface target into multiple class targets, each of which leverages CHA.
			//         Present shortcut: interface target calls are assumed to be library calls which is in effect returning bottom for the calls.


			ClassDecl self;  
		    Object parent;  // initially TypeNode, converted to ClassDecl_c where possible and then to parent's Tree
			List <Tree> children = new LinkedList <Tree>();
			Tree (ClassDecl s, Term p) {
				self = s;
				parent = p;
			}
			public String toString() {
				return "<" + self + ", " + parent + ">" ;
			}

			/** Identifies a non-virtual matching member from this tree's self ClassDecl, if any, and adds its summary to matchingSummaries. */
			public void nonVirtualMatch(List <Summary> matchingSummaries, String basicCallDiscriminator, Collection c) {
				List ms = self.body().members();

				for (Object member : ms) {
					if ((member instanceof MethodDecl) 
							&& (((MethodDecl) member).flags().flags().isStatic() ||
							    ((MethodDecl) member).flags().flags().isFinal()  ||
							    ((MethodDecl) member).flags().flags().isPrivate())) {
						MethodDecl mem = (MethodDecl) member;
						MethodDef mi = mem.methodDef();
						String memberDiscriminator =  mi.name() + Relations.constructTypeSignature(mi);
						if (debug()) System.out.println("nonVirtualMatch: Comparing basicCallDiscriminator: " + basicCallDiscriminator + " with " + memberDiscriminator);
						if (basicCallDiscriminator.equals(memberDiscriminator)) {
							matchingSummaries.add(Summary.findSummary(mem, c.summaries));
							return;
						}
						
					}
				}
				
			}
			
			/** Identifies a virtual matching member from this tree's self ClassDecl or the parent tree's ClassDecl (recursively), if any, and adds its summary to matchingSummaries. */
			public void parentMatch(List <Summary> matchingSummaries, String basicCallDiscriminator, Collection c, String callDiscriminator) {
				if (debug()) System.out.println("Entering parent with name: " + self.classDef().fullName());
				List ms = self.body().members();

				for (Object member : ms) {
					if ((member instanceof MethodDecl) 
							&& (! ((MethodDecl) member).flags().flags().isStatic())
							&& (! ((MethodDecl) member).flags().flags().isPrivate())){
						MethodDecl mem = (MethodDecl) member;
						MethodDef mi = mem.methodDef();
						String memberDiscriminator =  mi.name() + Relations.constructTypeSignature(mi);
						if (debug()) System.out.println("parentMatch: Comparing basicCallDiscriminator: " + basicCallDiscriminator + " with " + memberDiscriminator);
						if (basicCallDiscriminator.equals(memberDiscriminator)) {
							matchingSummaries.add(Summary.findSummary(mem, c.summaries));
							return;
						}
						
					}
				}
				if (parent instanceof Tree) ((Tree) parent).parentMatch (matchingSummaries, basicCallDiscriminator, c, callDiscriminator);
				else {  // create a library function summary
					Summary l = new Summary(c, callDiscriminator);
					matchingSummaries.add(l);
				}
			}
			
			/** Identifies all virtual matching members, if any, from this tree's self ClassDecl or the children trees' ClassDecls (recursively), and adds their summaries to matchingSummaries. */
			public void childrenMatch(List <Summary> matchingSummaries, String basicCallDiscriminator, Collection c) {
				if (debug()) System.out.println("Entering Child with name: " + self.classDef().fullName());
				List ms = self.body().members();

				for (Object member : ms) {
					if ((member instanceof MethodDecl) 
							&& (! ((MethodDecl) member).flags().flags().isStatic())) {
						MethodDecl mem = (MethodDecl) member;
						MethodDef mi = mem.methodDef();
						String memberDiscriminator =  mi.name() + Relations.constructTypeSignature(mi);
						if (debug()) System.out.println("childrenMatch: Comparing basicCallDiscriminator: " + basicCallDiscriminator + " with " + memberDiscriminator);
						if (basicCallDiscriminator.equals(memberDiscriminator)) {
							if (debug()) System.out.println("Adding child method.  Matching summaries length is: " + matchingSummaries.size());
							matchingSummaries.add(Summary.findSummary(mem, c.summaries));
							if (debug()) System.out.println("Added child method.  Matching summaries length is: " + matchingSummaries.size());
							break;
						}
						
					}
				}

				for (Tree child : children) {
					child.childrenMatch (matchingSummaries, basicCallDiscriminator, c);
				}
			}
		}
		
		/**		 the mapping from calls to summaries */
		final List <Relation> cToS; 
		/**		 the class Hierarchy */
		final List <Tree> classHierarchy;		
		
		private ClassDecl searchClassDecl(List <Summary> summaries, String name ) {

			for (Summary s : summaries) {
				if ((s.summaryOf instanceof ClassDecl) && 
						(! ((ClassDecl) s.summaryOf).flags().flags().isInterface()) &&
						name.equals(((ClassDecl) s.summaryOf).classDef().fullName())) 
					return (ClassDecl) s.summaryOf;
			}
			return null;
		}
		
		// Indexes into the classHierarcy using t, and searches  for matching virtual/non-virtual methods in the hierarchy using basicCallDiscriminator
		private List <Summary> matchingMethods (Tree t, String basicCallDiscriminator, Collection c, String callDiscriminator) {
			List <Summary> matchingSummaries = new LinkedList <Summary> ();
			
			// try out matches with non-virtual (static, final, private) methods.
			t.nonVirtualMatch(matchingSummaries, basicCallDiscriminator, c);
			if (matchingSummaries.size() == 1) return matchingSummaries;
			
			// try out matches with virtual functions.
			t.parentMatch(matchingSummaries, basicCallDiscriminator, c, callDiscriminator);
			
			if (debug()) System.out.println("Number of children to be traversed:" + t.children.size());

			for (Tree child : t.children) {
				child.childrenMatch (matchingSummaries, basicCallDiscriminator, c);
			}
			return matchingSummaries;
		}
		
		
		
		/** This constructs the mapping from calls to matching method/constructor summaries using class hierarchy analysis. */
		public Relations(Collection c) {
			if (debug()) System.out.println("Constructing Relations");
			cToS = new LinkedList <Relation> ();
			classHierarchy = new LinkedList <Tree> ();
			List <Summary> librarySummaries = new LinkedList <Summary>();

			// populate classHierarchy list
			if (debug()) System.out.println("Populating Class Hierarchy List");
			for (Summary s : c.summaries) {
				if (s.summaryOf instanceof ClassDecl
						&& (! ((ClassDecl) s.summaryOf).flags().flags().isInterface())) {
					ClassDecl cl = (ClassDecl) s.summaryOf;
					if (debug()) {
						System.out.println("Adding to hierarchy, class: " + cl.classDef().fullName()); //cl.name());
						System.out.println("interface? " + cl.flags().flags().isInterface());
						System.out.println("    with super Class: " + cl.superClass().
								type().
								toClass().toString()); // cl.superClass().name());
						/*
						if (cl.superClass().ext().node() instanceof ClassDecl)
							System.out.println("    No ClassDecl");
						else 
							System.out.println("    with ClassDecl: " + ((ClassDecl) cl.superClass().node()).name());
							*/
							
					}
					TypeNode p = cl.superClass();
					ClassDecl pc = searchClassDecl(c.summaries, p.type().toClass().toString());  // p.name();
					if (pc == null) 
						classHierarchy.add(new Tree(cl, p));
					else 
						classHierarchy.add(new Tree(cl, pc));
				}
			}
			
		    // populate children fields and repopulate parent with Trees
			if (debug()) System.out.println("Populating children fields and re-populating parent with trees");

			for (Tree t : classHierarchy) {
				ClassDecl p = (t.parent instanceof ClassDecl) ? (ClassDecl) t.parent : null;
				if (p != null) {
					

					// locate tree for p as tp
					Tree tp = null; 
					for (Tree cht : classHierarchy) {
						if (cht.self == p) tp = cht;
					}
					
					// repopulate t's parent, populate tp's children
					t.parent = tp;
					tp.children.add(t);
					if (debug()) {
						System.out.println("t: " + t.self.classDef().fullName() + " t.parent: " 
								+ tp.self.classDef().fullName());
					}
				}
			}
			

			for (Summary s : c.summaries) {
				List <Node> mcl = s.lookupFeature("CallFeature");
				if (mcl != null) {

					for (Node mcn : mcl) {
						Call mc = (Call) mcn;
						MethodInstance mi = mc.methodInstance();
						String callClass = stripDepTypeClause(mc.target().type().toReference().toString());  //mc.target().type().toClass().fullName(); //  mi.container().toClass().name(); -- loses precision //mi.container().toString();
						String basicCallDiscriminator = mi.name() + constructTypeSignature(mi.def());
						String callDiscriminator = constructDiscriminator(mi.def());
						if (debug()) System.out.println("target: " + callClass +  
								" mi's class: " + (mc.target().type().isClass() ? mi.container().toClass().name() : "?") + 
								" basicCallDiscriminator: " + basicCallDiscriminator + " callDiscriminator: " + callDiscriminator);
						if (summariesFor(mc) == null) {
							List <Summary> matchingSummaries = null;
							Tree t = null;

							for (Tree cht : classHierarchy) {
								if (cht.self.classDef().toString().equals(callClass)) {  
									t = cht; 
									if (debug()) System.out.println("Found Class in Hierarchy as: " + callClass);
									break;
									}
							}
							if (t != null) {
								matchingSummaries = matchingMethods(t, basicCallDiscriminator, c, callDiscriminator);
								// search for a library summary in matching summaries that is not a duplicate of an existing library summary. Else, add any
								// library summary found to librarySummaries.
								Summary anyLibSum = null;

								for (Summary sum : matchingSummaries) {
									if (sum.library) anyLibSum = sum;
								}
								if (anyLibSum != null) { 
									Summary duplicate = null;

									for (Summary summary : librarySummaries) {
										if (summary.libraryFuncDiscriminator.equals(anyLibSum.libraryFuncDiscriminator)) {duplicate = summary; break;}
									}
									if (duplicate != null) {
										matchingSummaries.remove(anyLibSum);
										matchingSummaries.add (duplicate);
									}
									else librarySummaries.add(anyLibSum);
								}
							}
							else {	
								matchingSummaries = new LinkedList<Summary>();

								for (Summary summary : librarySummaries) {
									if (summary.libraryFuncDiscriminator.equals(callDiscriminator)) matchingSummaries.add(summary);
								}
								if (matchingSummaries.isEmpty()) {
									Summary libSummary = new Summary(c, callDiscriminator);
									librarySummaries.add(libSummary);
									matchingSummaries.add(libSummary);
								}
							}
							Relation r = new Relation (mc, matchingSummaries);
							cToS.add(r);
							
						}
					}
				}
			
			

			/*
			si = c.summaries.listIterator(0);
			while (si.hasNext()){
				Summary s = si.next();
				LinkedList <Node> mcl = s.lookupFeature("CallFeature");
				if (mcl != null) {
					ListIterator <Node> mci = mcl.listIterator(0);
					while (mci.hasNext()) {
						Call_c mc = (Call_c) mci.next();
						String callDiscriminator = constructDiscriminator(mc.methodInstance());
						
						if (summariesFor(mc) == null) {
							LinkedList <Summary> matchingSummaries = new LinkedList<Summary>();
							ListIterator <Summary> summaries = c.summaries.listIterator(0);
							while (summaries.hasNext()){
								Summary summary = summaries.next();
								if (summary.summaryOf instanceof MethodDecl_c) {
									MethodDecl_c m = (MethodDecl_c) summary.summaryOf;
									//if (debug()) System.out.println("class Name:" + summary.lookupLocalProperty("className"));
									String methodDeclDiscriminator = constructDiscriminator(m.methodInstance());
									if (debug()) System.out.println("Matching callDiscriminator = " + callDiscriminator + " with methodDeclDiscriminator = " + methodDeclDiscriminator);
									if (methodDeclDiscriminator.equals(callDiscriminator)) {
										if (debug()) System.out.println("Matched!!, nonVirtual: " + summary.lookupLocalProperty("nonVirtual") + " to " + mc);
										matchingSummaries.add(summary);
									}
								}
							}
							summaries = librarySummaries.listIterator(0);
							while (summaries.hasNext()){
								Summary summary = summaries.next();
								if (summary.libraryFuncDiscriminator.equals(callDiscriminator)) matchingSummaries.add(summary);
							}
							if (matchingSummaries.isEmpty()) {
								Summary libSummary = createLibrarySummary(callDiscriminator, c);
								librarySummaries.add(libSummary);
								matchingSummaries.add(libSummary);
							}
							Relation r = new Relation (mc, matchingSummaries);
							cToS.add(r);
							
						}
					}
				}
				*/
				List <Node> ccl = s.lookupFeature("ConstructorCallFeature");
				if (ccl != null) {

					for (Node ccn : ccl) {
						ConstructorCall cc = (ConstructorCall) ccn;
						String callDiscriminator = constructDiscriminator(cc.constructorInstance().def());
						if (summariesFor(cc) == null) {
							LinkedList <Summary> matchingSummaries = new LinkedList<Summary> ();

							for (Summary summary : c.summaries) {
								if (summary.summaryOf instanceof ConstructorDecl) {
									ConstructorDecl cd = (ConstructorDecl) summary.summaryOf;
									String constructorDeclDiscriminator = 
										constructDiscriminator(cd.constructorDef());
									if (debug()) System.out.println("Matching callDiscriminator = " + callDiscriminator + " with constructorDeclDiscriminator = " + constructorDeclDiscriminator);
									if (constructorDeclDiscriminator.equals(callDiscriminator)) {
										if (debug()) System.out.println("Matched!!" + cc);
										matchingSummaries.add(summary);
									}
								}
							}

							for (Summary summary : librarySummaries) {
								if (summary.libraryFuncDiscriminator.equals(callDiscriminator)) matchingSummaries.add(summary);
							}
							if (matchingSummaries.isEmpty()) {
								Summary libSummary = new Summary(c, callDiscriminator);
								librarySummaries.add(libSummary);
								matchingSummaries.add(libSummary);
							}
							Relation r = new Relation (cc, matchingSummaries);
							cToS.add(r);
						}
						
					}
				}
			}
			c.summaries.addAll(librarySummaries);
			if (debug()) {
				System.out.println("Constructed Relations: summaries total " + c.summaries.size());
				System.out.println("Dumping cToS:");

				for (Relation r : cToS) {
					System.out.println("Call : " + 
							((r.call instanceof Call) ? 
									//constructDiscriminator(((Call) r.call).methodInstance())
									stripDepTypeClause(((Call) r.call).target().type().toReference().toString()) + "." +
									((Call) r.call).methodInstance().name() + constructTypeSignature(
									((Call) r.call).methodInstance().def())
									:
										constructDiscriminator(((ConstructorCall) r.call).constructorInstance().def())));

					for (Summary s : r.summaries) {
						System.out.println("         maps to: " + (s.library ? s.libraryFuncDiscriminator : 
							(s.summaryOf instanceof MethodDecl ? 
									constructDiscriminator(((MethodDecl) s.summaryOf).methodDef()) :
										constructDiscriminator(((ConstructorDecl) s.summaryOf).constructorDef()))) 
										+ " of library: " + s.library);
					}
				}
			}
		}
		
		private String stripDepTypeClause (String typeWithClause) {
			int li = typeWithClause.indexOf("(");
			if (li == -1) li = typeWithClause.length();
			return typeWithClause.substring(0, li);
		}

		// Externalizes the type signature of a method
		private String constructDiscriminator(MethodDef mi) {
//			return mi.container().toClass().fullName() + "." + mi.name() + constructTypeSignature(mi);
			return stripDepTypeClause(mi.container().toString()) + "." 
			+ mi.name() + constructTypeSignature(mi);
		}
		// Externalizes the type signature of a constructor
		private String constructDiscriminator(ConstructorDef ci) {
			return ci.container().get().fullName() + ".<init>" + constructTypeSignature(ci);
		}
		/** Externalizes the arguments part of a call's type signature */
		public static String constructTypeSignature(ProcedureDef pi) {
			StringBuffer res = new StringBuffer("(");
			List formals = pi.formalTypes();
			for (Iterator fi = formals.iterator(); fi.hasNext();) {
				Type t = (Type) fi.next();
				res.append(t.translate(null));
				if (fi.hasNext())
					res.append(", ");
			}
			res.append(")");
			return res.toString();
		}

		/*
		private void addLibrarySummaries(Collection c, LinkedList<Summary> libs) {
			
			if (debug()) System.out.println("Adding Library Summaries" );
			LinkedList <Summary> additions = new LinkedList<Summary>();
			ListIterator <Summary> si = c.summaries.listIterator(0);
			while (si.hasNext()){
				Summary s = si.next();
				if (debugLevel == 2) {System.out.println("====================================="); s.printSummary();}
				LinkedList <Node> mcl = s.lookupFeature("CallFeature");
				if (mcl != null) {
					ListIterator <Node> mci = mcl.listIterator(0);
					while (mci.hasNext()) {
						Call_c mc = (Call_c) mci.next();
						String callDiscriminator = mc.target().type().toClass().fullName() + mc.name() + mc.arguments().size();
						if (debug()) System.out.println("handling call: " + callDiscriminator + "summariesFor : " + summariesFor(mc)
								+ "topClass: " + ((ClassDecl_c) c.topClass).name());
						if (summariesFor(mc) == null && 
								(! mc.target().type().toClass().fullName().equals(((ClassDecl_c) c.topClass).name()))) {
							LinkedList <Summary> matchingSummaries = new LinkedList<Summary>();
							if (debug()) System.out.println("Seeking libraries input for method summaries of call: " + callDiscriminator);
							ListIterator <Summary> llibs = libs.listIterator(0);
							while (llibs.hasNext()){
								Summary summary = llibs.next();
								if (summary.libraryFuncDiscriminator.equals(callDiscriminator)) {
									matchingSummaries.add(summary);
									
								}
							}
							if (! matchingSummaries.isEmpty()) {
								Relation r = new Relation (mc, matchingSummaries);
								cToS.add(r);
								ListIterator <Summary> lms = matchingSummaries.listIterator(0);
								while (lms.hasNext()) additions.add(lms.next());  
							}
							else if (debug()) System.out.println("Unmatched call: " + callDiscriminator );
						}
					}
				}
				LinkedList <Node> ccl = s.lookupFeature("ConstructorCallFeature");
				if (ccl != null) {
					ListIterator <Node> cci = ccl.listIterator(0);
					while (cci.hasNext()) {
						ConstructorCall cc = (ConstructorCall) cci.next();
						String callSignature = cc.constructorInstance().signature();  
						String callName = callSignature.substring(0, callSignature.indexOf("("));
						String callDiscriminator = callName + cc.constructorInstance().formalTypes().size();
						if (debug()) System.out.println("handling call: " + callDiscriminator + "summariesFor : " + summariesFor(cc)
								+ "topClass: " + ((ClassDecl_c) c.topClass).name());
						if (summariesFor(cc) == null && (! callName.equals(((ClassDecl_c) c.topClass).name()))) {
							LinkedList <Summary> matchingSummaries = new LinkedList<Summary> ();
							if (debug()) System.out.println("Seeking libraries input for constructor summaries of ccall: " + callDiscriminator);
							
							ListIterator <Summary> llibs = libs.listIterator(0);
							while (llibs.hasNext()){
								Summary summary = llibs.next();
								if (summary.libraryFuncDiscriminator.equals(callDiscriminator)) {
									matchingSummaries.add(summary);
									
								}
							}
							
							if (! matchingSummaries.isEmpty()) {
								Relation r = new Relation (cc, matchingSummaries);
								cToS.add(r);
								ListIterator <Summary> lms = matchingSummaries.listIterator(0);
								while (lms.hasNext()) additions.add(lms.next());  
							}
							else if (debug()) System.out.println("Unmatched call: " + callDiscriminator );
						}
						
					}
				}
			}
			ListIterator <Summary> la = additions.listIterator(0);
			while (la.hasNext()) c.summaries.add(la.next());  // add collected library summaries to collection summaries.
			if (debug()) System.out.println("Added Library Summaries");
		}
		*/
		
		public List <Summary> summariesFor(Node call) {

			for (Relation r : cToS) {
				if (r.call == call) return r.summaries;
			}
			return null;
		}
		
	}
	
	/** Concrete feature class */
	public static class LoopFeature extends Feature {
		public boolean test(Node n) {
			return (n instanceof Loop) || (n instanceof While) || (n instanceof For);
		}
	}
	
	/** Concrete feature class	 */
	public static class ParallelFeature extends Feature {
		public boolean test(Node n) {
			return (n instanceof Async) || (n instanceof AtEach) || (n instanceof ForEach) || (n instanceof Future);
		}
	}
	
	/** Concrete feature class  */
	public static class FuncDeclFeature extends Feature {
		public boolean test(Node n) {
			return (n instanceof MethodDecl) || (n instanceof ConstructorDecl);
		}
	}
	
	/** Concrete feature class  */
	public static class ClassDeclFeature extends Feature {
		public boolean test(Node n) {
			return (n instanceof ClassDecl);
		}
	}
	
	/** Concrete feature class  */
	public static class FinishAteachFeature extends Feature {
		public boolean test(Node n) {
			return (n instanceof Finish) || (n instanceof AtEach);
		}
	}
	
	/** Concrete feature class  */
	public static class ExceptionFeature extends Feature {
		public boolean test(Node n) {
			return (n instanceof Throw) ;
		}
	}
	
	/** Concrete feature class */
	public static class BlockingFeature extends Feature {
		public boolean test(Node n) {
			return false; // blocking constructs
		}
	}
	
	/** Concrete feature class */
	public static class InlinableAsyncRelatedFeature extends Feature {
		// An inlineable async must look for all loops, recursion, blocking function calls/constructs, and asyncs and parallel features within itself.
		// An inlineable async should not have any of the above features within itself, except (if desired) inlinable asyncs themselves within itself.  An inlinable
		// async can be clocked. The above can be relaxed somewhat by proving that loops/recursion if any are bounded/terminating.
		LoopFeature l = new LoopFeature();
		ParallelFeature p = new ParallelFeature();
		ExceptionFeature e = new ExceptionFeature();
		BlockingFeature b = new BlockingFeature(); 
		public boolean test(Node n) {
			return l.test(n) || p.test(n) || e.test(n) || b.test(n);
		}
	}
	
	/** Concrete feature class */
	public static class CallFeature extends Feature {
		public boolean test(Node n) {
			if (debug()) {
				if (n instanceof Call) System.out.println("Call_c feature method name : " + ((Call) n).name() + " full class name " +
						((Call) n).target().type().toReference().toString());
				//if (n instanceof ConstructorCall) System.out.println("ConstructorCall feature name: " + ((ConstructorCall) n).procedureInstance().signature());
			}
			return n instanceof Call ; //|| n instanceof ConstructorCall;
		}
	}
	/** Concrete feature class */
	public static class ConstructorCallFeature extends Feature {
		public boolean test(Node n) {
			if (debug()) {
				//if (n instanceof Call_c) System.out.println("Call_c feature method name : " + ((Call_c) n).name() + " full class name " +
					//	((Call_c) n).target().type().toClass().fullName());
				if (n instanceof ConstructorCall) System.out.println("ConstructorCall feature name: " + ((ConstructorCall) n).procedureInstance().signature());
			}
			return n instanceof ConstructorCall ; // || n instanceof Call_c ;
		}
	}
	/** Concrete Summarizer class  */
	public static class AsyncSummarizer extends Summarizer {
		public boolean applicable(Node n) { return n instanceof Async;}
		
		public void adder(List <Summary> summaries, Summary s) {summaries.add(s);}
		
		public void computeLocal(Summary s) {
			
				List<Node> fs = s.lookupFeature("InlinableAsyncRelatedFeature");
				boolean localInlineable = fs == null || fs.size() == 1 && fs.contains(s.summaryOf);
			s.addLocalProperty("inlineable", localInlineable ? "true" : "false");
		}
		
		public void computeGlobal(Summary s) 
		{ 
			/*
			boolean localInlineable = ((Boolean) s.lookupLocalProperty("inlineable")).booleanValue();
			if (! localInlineable) {s.addGlobalProperty("inlineable", new Boolean(false)); return;}
			// LinkedList <Node> methods = s.lookupFeature("Call_c");
			// LinkedList <Node> constructors = s.lookupFeature("ConstructorCall");	     
			boolean inlineableCalls = true ; /* exTODO forall calls, all methods for each call are asyncWiseInlineable.  
			AsyncWiseInlineable can either be defined as a local property of each method (in MethodSummarizer) or it can be computed
			afresh each time for each method summary.  Any blocking function calls are not AsyncWiseInlineable */ 		
			/*
			if (! inlineableCalls) {s.addGlobalProperty("inlineable", new Boolean(false)); return;}
			boolean nonRecursive =  true; // exTODO search the summary graph for any call cycles
			s.addGlobalProperty("inlineable", new Boolean(nonRecursive));
			*/
		}
	}
	/** Concrete Summarizer class */
	public static class MethodSummarizer extends Summarizer {
		
		public void adder(List <Summary> summaries, Summary s) {summaries.clear(); summaries.add(s);}
		public boolean applicable(Node n) { 
			if (debug()) {
				if (n instanceof MethodDecl) {
					System.out.println(" MethodDecl name is: " + ((MethodDecl) n).name() + 
							((MethodDecl) n).flags().flags());
				}
			}
			
			return n instanceof MethodDecl;
			}
		
		private boolean isFinal(MethodDecl m) {
			return m.flags().flags().isFinal() || 
			// left clause for && below is unnecessary once anonymous classes are taken care of 
			// necessitated for crash-free passing of x10.common/examples/Constructs/Array/JavaArrayWithInitializer.x10
			(((ClassType) m.
					methodDef().
					container()).
					flags().flags() != null) 
					&& 
					((ClassType) m.
							methodDef().
							container()).
							flags().isFinal();
		}
		
		public void computeLocal(Summary s) {
			MethodDecl m = (MethodDecl) s.summaryOf; 
			boolean nonVirtual = isFinal(m) || m.flags().flags().isStatic();
			s.addLocalProperty("nonVirtual", nonVirtual ? "true" : "false");
		}
		
		
		public void computeGlobal(Summary s) 
		{ 
			boolean nonRecursive =  true; // exTODO search the summary graph for any call cycles
			// Boolean(nonRecursive) property below can be true, false, or null. Null represents bottom
			s.addGlobalProperty("nonRecursive", nonRecursive ? "true" : "false");
		}
	}
	/** Concrete Summarizer class  */
	public static class ClassSummarizer extends Summarizer {
		
		public void adder(List <Summary> summaries, Summary s) {summaries.clear(); summaries.add(s);}
		
		public boolean applicable(Node n) { 
			if (debug()) {
				if (n instanceof ClassDecl) {
					System.out.println(" ClassDecl_c name is: " + ((ClassDecl) n).name() + 
							((ClassDecl) n).flags().flags());
				}
			}
			
			return n instanceof ClassDecl;
			}
		
		public void computeLocal(Summary s) {
			/*
			ClassDecl_c c = (ClassDecl_c) s.summaryOf;
			
			LinkedList <Node> fs = s.lookupFeature("FuncDeclFeature");
			if (fs == null) return;
			ListIterator <Node> fsi = fs.listIterator(0);
			while (fsi.hasNext()) {
				Node n = fsi.next();
				if (n instanceof MethodDecl_c) {
					
					Summary ns = s.harvest.getSummary(n);
					if (c.flags().isFinal()) ns.addLocalProperty("nonVirtual", "true");
					ns.addLocalProperty("className", c.name());
				}
				
			}
			*/
			
			
		}
		
		
		public void computeGlobal(Summary s) 
		{ 
			
		}
	}
	/** Concrete Summarizer class */
	public static class ConstructorSummarizer extends Summarizer {
		
		public void adder(List <Summary> summaries, Summary s) {summaries.clear(); summaries.add(s);}
		
		public boolean applicable(Node n) { 
			if (debug()) {
				if (n instanceof ConstructorDecl) {
					System.out.println("ConstructorDecl signature is: " 
							+ ((ConstructorDecl) n).constructorDef().signature() + 
							" and name is: " + ((ConstructorDecl) n).name());
				}
			}
			
			return n instanceof ConstructorDecl;} 
		
		public void computeLocal(Summary s) {
			boolean nonVirtual = true; 
			s.addLocalProperty("nonVirtual", nonVirtual ? "true" : "false");
		}
		
		public void computeGlobal(Summary s) 
		{ 
			boolean nonRecursive =  true; // exTODO : search the summary graph for any call cycles
			// Boolean(nonRecursive) property below can be true, false, or null. Null represents bottom
			s.addGlobalProperty("nonRecursive", nonRecursive ? "true" : "false");
		}
	}
	
	/** The AST visitor class for summarizing the AST  */
	public static class X10SummarizingVisitor extends ContextVisitor {
		private final Translator tr;
		private X10CPPContext_c outermost;
		
		public X10SummarizingVisitor(Translator tr, X10CPPContext_c o) {
			super(tr.job(), tr.typeSystem(), tr.nodeFactory());
			this.tr = tr;
			outermost = o;
			this.begin();
		}
		
		public void handleIfSummarizer(Node n) {
			String nodeName = n.getClass().getSimpleName();
			if (debug()) {
				System.out.println("Checking for summarizer. NodeName is: " + nodeName);
			}

			for (Summarizer summarizer : outermost.harvest.summarizers) {
				if (summarizer.applicable(n)) {
					summarizer.summaryStart(n, (X10CPPContext_c) context(), outermost); // tr.context()
					return ;
				}
			}
		}
		
		public void handleIfFeature(Node n) {
			String nodeName = n.getClass().getSimpleName();
			if (debug()) {
				System.out.println("Checking for feature. NodeName is: " + nodeName);
			}

			for (Feature feature : outermost.harvest.features) {
				if (feature.test(n)) {
					X10CPPContext_c context = (X10CPPContext_c) context();
					String featureName = feature.getClass().getSimpleName();
					feature.store(featureName, n, context);   // tr.context()
				}
			}
		}
		
		public Node override(Node parent, Node child) {
			handleIfSummarizer(child);
			handleIfFeature(child);
			return null;
		}
	}
	
	
	/** This class creates the AST visitor and invokes it to make the summarizing pass over the AST.  */
	public static class X10SummarizingPass { //extends X10DelegatingVisitor {
		protected X10CPPContext_c outermost;
		private final Translator tr;
		private List <Summary> librarySummaries; 
		


		private void completeSummaries(Collection c) {
			if (debug()) System.out.println("completeSummaries");
			List <Summary> l = c.summaries;
			if (l == null) return;

			for (Summary s : l) {
				s.summarizer.summaryEnd(s);	
			}
			

			c.callToSummaries = new Relations (c);
			//c.callToSummaries.addLibrarySummaries(c, librarySummaries);
			

			for (Summary s : c.summaries) {
				if (! s.library) s.summarizer.computeGlobal(s);
				if (debug()) s.printSummary();
			}
		}
		
		
		/* For file-based specification of library summaries -- the file of following format is read in to create the summaries
		 * Input file Structure generating a summary is:
		 * LibfuncDiscriminator (virtual | nonVirtual) { (localPropertyName localProperty)* } { (globalPropertyName globalProperty)* }
		 */
		
		private List <Summary> loadlibrarySummaries(FileReader librarySummaries, Collection c) throws Exception {
			StreamTokenizer lib = new StreamTokenizer(librarySummaries);
			lib.slashSlashComments(true); // only C++ style comments are supported in library files.
			List <Summary> libs = new LinkedList<Summary> ();
			List <String> tokens = new LinkedList<String> ();
			while (true) {
				lib.nextToken();
				if (lib.ttype == StreamTokenizer.TT_EOF) break;
				else if (lib.ttype == StreamTokenizer.TT_WORD) tokens.add(lib.sval);
				else if (lib.ttype == StreamTokenizer.TT_NUMBER) tokens.add(new Double(lib.nval).toString());
				else {System.err.println("Parsing unknown token in loading libraries"); throw new Exception();}
				
				
			}
			ListIterator <String> itokens = tokens.listIterator(0);
			while (itokens.hasNext()) libs.add(getSummary(itokens, c));
			return libs;

		}
		
		/*
		 * Input file Structure generating a summary is:
		 * LibfuncDiscriminator (virtual | nonVirtual) { (localPropertyName localProperty)* } { (globalPropertyName globalProperty)* }
		 */
		private Summary getSummary (ListIterator <String> itokens, Collection c) throws Exception {
			String libFuncDiscriminator = itokens.next();
			String kind = itokens.next();
			if (kind.equals("virtual")) {
				System.err.println("Improper handling warning: Dealing with a vitual library function: " + libFuncDiscriminator); 
				throw new Exception ();
				};
			String lbrace = itokens.next();
			if (! kind.equals("{")) {
				System.err.println("Expected {, got: " + lbrace); 
				throw new Exception ();
				};
				
			Summary s = new Summary(c, libFuncDiscriminator);
				
			while (true) {
				String name = itokens.next();
				if (name.equals("}")) break;
				String property = itokens.next();
				s.addLocalProperty(name, property);
			}
			
			lbrace = itokens.next();
			if (! kind.equals("{")) {
				System.err.println("Expected {, got: " + lbrace); 
				throw new Exception ();
				};
				
			while (true) {
				String name = itokens.next();
				if (name.equals("}")) break;
				String property = itokens.next();
				s.addGlobalProperty(name, property);
			}
			
			return s;

		}

        /** This method instantiates the concrete features and summarizers and AST visitor.  The last is invoked to make the summarizing passs
		 populating the summaries, followed by completion works like creating the calls to summaries Relations.  */
		public void makeSummariesPass (Node n) {
			
			String nodeName = n.getClass().getSimpleName();
			if (debug()) {
				System.out.println("Starting Summaries Pass. NodeName is: " + nodeName);
			}
			
			List <Feature> features = new LinkedList <Feature> ();
			List <Summarizer> summarizers = new LinkedList <Summarizer> ();
			List <Summary> summaries = new LinkedList <Summary> ();
			features.add(new CallFeature());
			features.add(new InlinableAsyncRelatedFeature ());
			features.add(new ConstructorCallFeature());
			features.add(new FuncDeclFeature());
			features.add(new ClassDeclFeature());
			summarizers.add(new AsyncSummarizer());
			summarizers.add(new MethodSummarizer());
			summarizers.add(new ConstructorSummarizer());
			summarizers.add(new ClassSummarizer());
			outermost.harvest = new Collection(features, summarizers, summaries, n);
			/*  
			try {
				FileReader librarySummaries = new FileReader(librarySummaries());
				this.librarySummaries = loadlibrarySummaries(librarySummaries, outermost.harvest);
				librarySummaries.close();
				}
				catch (Exception e) {
					System.err.println("Exception in creating library Summaries:" + e);
				}
			*/
			n.visit(new X10SummarizingVisitor (tr, outermost));
			//assert (n instanceof ClassDecl_c);
			//n.visit(new ContextVisitor(tr.job(), tr.typeSystem(), tr.nodeFactory()).begin());
			completeSummaries(outermost.harvest);
			
			if (debug()) outermost.harvest.printCallMatches();
			
			if (debug()) {
				System.out.println("Concluded Summaries Pass. NodeName is: " + nodeName);
			}
			
		}


		private File librarySummaries() {
			File librarySummaries = new File(tr.job().extensionInfo().getOptions().output_directory, "librarySummaries.txt");
			return librarySummaries;
		}
		
		/*
		public void visitAppropriate(JL n) {
			if ((n instanceof ClassDecl_c) && (outermost.harvest == null)) {	// this happens only once for the outermost class
				makeSummariesPass((Node) n);
			}
			super.visitAppropriate(n);
		}
		*/
		
		
		public X10SummarizingPass (Translator tr){
			this.tr = tr;
			outermost = ((X10CPPContext_c) tr.context()).outermostContext();
		}
		
	}
	
	/* TODO List:  
	 * (a) Make Relations constructor inter-file.
	 * (b) break file into a separate package and files
	 * (c) make a separate plug-ins folder, from which use dynamic class loading to actively pick up the current set of plug-ins instead of linking 
	 *     them manually into code as presently done
	 * (d) Anonymous classes?
	 * (e) Am I handling constructor calls correctly -- check super/this direct calls as well as the other types
	 */
	
}

