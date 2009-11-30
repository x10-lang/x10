package x10cpp.visit;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Node;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.visit.Translator;
import x10.Configuration;
import x10.util.StreamWrapper;

/**
 * Extracted from X10PrettyPrinterProcessor. Helper class for instantiating and processing
 * templates.
 *
 * @author vj
 *
 */
public class XCDProcessor {
	
	private final StreamWrapper sw;
	private final Translator tr;
	public XCDProcessor(StreamWrapper sw, Translator tr) {
		this.sw = sw;
		this.tr = tr;
	}
	
	static HashMap<String,String> translationCache_ = new HashMap<String,String>();

	/**
	 * Pretty-print a given object.
	 *
	 * @param o object to print
	 */
	void prettyPrint(Object o) {
		if (o instanceof Expander) {
			((Expander) o).expand();
		} else if (o instanceof Node) {
			((Node) o).del().translate(sw, tr);
		} else if (o instanceof Type) {
			throw new InternalCompilerError("Should not attempt to pretty-print a type");
		} else if (o != null) {
			sw.write(o.toString());
		}
	}
	static String translate(String id) {
		String cached = translationCache_.get(id);
		if (cached != null)
			return cached;
		try {
			String rname = Configuration.COMPILER_FRAGMENT_DATA_DIRECTORY + id + ".xcd"; // xcd = x10 compiler data/definition
			InputStream is = XCDProcessor.class.getClassLoader().getResourceAsStream(rname);
			if (is == null)
				throw new IOException("Cannot find resource '"+rname+"'");
			byte[] b = new byte[is.available()];
			for (int off = 0; off < b.length; ) {
				int read = is.read(b, off, b.length - off);
				off += read;
			}
			String trans = new String(b, "UTF-8");
			// Skip initial lines that start with "// SYNOPSIS: "
			// (spaces matter!)
			while (trans.indexOf("// SYNOPSIS: ") == 0)
				trans = trans.substring(trans.indexOf('\n')+1);
			// Remove one trailing newline (if any)
			if (trans.lastIndexOf('\n') == trans.length()-1)
				trans = trans.substring(0, trans.length()-1);
			boolean newline = trans.lastIndexOf('\n') == trans.length()-1;
			trans = "/* template:"+id+" { */" + trans + "/* } */";
			// If the template ends in a newline, add it after the footer
			if (newline)
				trans = trans + "\n";
			translationCache_.put(id, trans);
			is.close();
			return trans;
		} catch (IOException io) {
			throw new Error("No translation for " + id + " found!", io);
		}
	}

	// TODO: [IP] Move all of the template functionality into the base compiler

	/**
	 * An abstract class for sub-template expansion.
	 */
	public abstract class Expander { public abstract void expand(); }

	/**
	 * Expand a given template with given parameters.
	 *
	 * @param id xcd filename for the template
	 * @param components arguments to the template.
	 */
	private void dump(String id, Object[] components) {
		String regex = translate(id);
		int len = regex.length();
		int pos = 0;
		int start = 0;
		while (pos < len) {
			if (regex.charAt(pos) == '\n') {
				sw.write(regex.substring(start, pos));
				sw.newline(0);
				start = pos+1;
			}
			else if (regex.charAt(pos) == '#') {
				sw.write(regex.substring(start, pos));
				pos++;
				if (regex.charAt(pos) == '#') {
					sw.write("#");
					start = pos+1;
				} else if (Character.isDigit(regex.charAt(pos))) {
					int end = pos+1;
					while (Character.isDigit(regex.charAt(end)))
						end++;
					Integer idx = new Integer(regex.substring(pos, end));
					start = end;
					if (idx.intValue() >= components.length)
						throw new InternalCompilerError("Template '"+id+"' uses #"+idx);
					prettyPrint(components[idx.intValue()]);
				} else
					throw new InternalCompilerError("Template '"+id+"' has a lone '#'");
			}
			pos++;
		}
		sw.write(regex.substring(start));
	}

	/**
	 * Expand a given template with the given set of arguments.
	 * Equivalent to a Loop with an array of singleton lists.
	 * If the template has zero, one, two, or three arguments, the
	 * arguments can be passed in directly to the constructor.
	 */
	public class Template extends Expander {
		private final String id;
		//private final String template;
		private final Object[] args;
		public Template(String id) {
			this(id, new Object[] { });
		}
		public Template(String id, Object arg) {
			this(id, new Object[] { arg });
		}
		public Template(String id, Object arg1, Object arg2) {
			this(id, new Object[] { arg1, arg2 });
		}
		public Template(String id, Object arg1, Object arg2, Object arg3) {
			this(id, new Object[] { arg1, arg2, arg3 });
		}
		public Template(String id, Object[] args) {
			this.id = id;
			//this.template = translate(id);
			this.args = args;
		}
		public void expand() {
			dump(id, args);
		}
	}

	/**
	 * Join a given list of arguments with a given delimiter.
	 * Two or three arguments can also be specified separately.
	 * Do not join a circular list.
	 */
	public class Join extends Expander {
		// FIXME: Igor, where do we need this class? -[Krishna].
		private final String delimiter;
		private final List args;
		public Join(String delimiter, Object a, Object b) {
			this(delimiter, asList(a, b));
		}
		public Join(String delimiter, Object a, Object b, Object c) {
			this(delimiter, asList(a, b, c));
		}
		public Join(String delimiter, List args) {
			this.delimiter = delimiter;
			this.args = args;
		}
		public void expand() {
			sw.write("/* Join: { */");
			int N = args.size();
			// FIXME: [IP] use N in iteration, to handle circular lists
			for (Iterator i = args.iterator(); i.hasNext(); ) {
				prettyPrint(i.next());
				if (i.hasNext())
					prettyPrint(delimiter);
			}
			sw.write("/* } */");
		}
	}

	/**
	 * Expand a given template in a loop with the given set of arguments.
	 * For the loop body, pass in an array of Lists of identical length
	 * (each list representing all instances of a given argument),
	 * which will be translated into array-length repetitions of the
	 * loop body template.
	 * Use a CircularList to replicate a single object across iterations.
	 * If the template has only one argument, a single list can be used.
	 */
	public class Loop extends Expander {
		// FIXME: Igor, where do we need this class? -[Krishna].
		private final String id;
		//private final String template;
		private final List[] lists;
		private final int N;
		public Loop(String id, List arg) {
			this(id, new List[] { arg });
		}
		public Loop(String id, List arg1, List arg2) {
			this(id, new List[] { arg1, arg2 });
		}
		public Loop(String id, List arg1, List arg2, List arg3) {
			this(id, new List[] { arg1, arg2, arg3 });
		}
		public Loop(String id, List arg1, List arg2, List arg3, List arg4) {
			this(id, new List[] { arg1, arg2, arg3, arg4 });
		}
		public Loop(String id, List[] components) {
			this.id = id;
			//this.template = translate(id);
			this.lists = components;
			// Make sure we have at least one parameter
			assert(lists.length > 0);
			int n = -1;
			int i = 0;
			for (; i < lists.length && n == -1; i++)
				n = lists[i].size();
			// Make sure the lists are all of the same size or circular
			for (; i < lists.length; i++)
				assert(lists[i].size() == n || lists[i].size() == -1);
			this.N = n;
		}
		public void expand() {
			sw.write("/* Loop: { */");
			Object[] args = new Object[lists.length];
			Iterator[] iters = new Iterator[lists.length];
			// Parallel iterators over all argument lists
			for (int j = 0; j < lists.length; j++)
				iters[j] = lists[j].iterator();
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < args.length; j++)
					args[j] = iters[j].next();
				dump(id, args);
			}
			sw.write("/* } */");
		}
	}

	private static List asList(Object a, Object b) {
		List<Object> l = new ArrayList<Object>(2); l.add(a); l.add(b);
		return l;
	}
	private static List asList(Object a, Object b, Object c) {
		List l = new ArrayList(3); l.add(a); l.add(b); l.add(c);
		return l;
	}
}
