package x10.sncode;

import java.io.InvalidClassException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Tree {
	String key;

	public Tree(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public boolean contains(String string) {
		Tree t = findTree(key);
		return t != null;
	}

	public Object find(String key) {
		Tree t = findTree(key);
		if (t != null)
			return t.getValue();
		return null;
	}

	public Tree findTree(String key) {
		for (Tree t : getChildren()) {
			if (key.equals(t.key))
				return t;
		}
		return null;
	}

	public List<Tree> findAll(String key) {
		List<Tree> ts = new ArrayList<Tree>();
		for (Tree t : getChildren()) {
			if (key.equals(t.key))
				ts.add(t);
		}
		return ts;
	}

	public abstract Object getValue();

	public abstract List<Tree> getChildren();

	public abstract void writeInto(SnFile sn, ByteBuffer buf);

	public void dump(PrintStream out) {
		dump(out, 0);
	}

	public abstract void dump(PrintStream out, int indent);

	public static Tree readFrom(SnFile sn, ByteBuffer buf)
			throws InvalidClassFileException {
		// for debugging
		int offset = buf.offset();

		int magic = buf.getInt();
		assert magic == 0xdefaced;

		int dataLen = buf.getLength();

		try {
			if (dataLen == -1) {
				int keyIndex = buf.getCPIndex();
				int valIndex = buf.getCPIndex();
				String key = sn.getConstantPool().getCPUtf8(keyIndex);
				Object value = sn.getConstantPool().getCPEntry(valIndex);
				return new Leaf(key, value);
			} else {
				int dataOffset = buf.offset();
				int endOffset = dataOffset + dataLen;

				int keyIndex = buf.getCPIndex();
				String key = sn.getConstantPool().getCPUtf8(keyIndex);

				List<Tree> children = new ArrayList<Tree>();
				while (buf.offset() < endOffset) {
					Tree t = readFrom(sn, buf);
					children.add(t);
				}

				if (endOffset != buf.offset())
					throw new InvalidClassFileException(offset, "bad attribute");

				return new Branch(key, children);
			}
		} catch (ClassCastException e) {
			throw new InvalidClassFileException(offset, e.getMessage());
		}
	}

	public static class Branch extends Tree {
		List<Tree> children;

		public Branch(String key) {
			super(key);
			children = new ArrayList<Tree>(0);
		}

		public Branch(String key, Tree... children) {
			this(key, Arrays.asList(children));
		}

		public Branch(String key, List<Tree> children) {
			super(key);
			this.children = new ArrayList<Tree>(children);
		}

		public void add(Tree child) {
			children.add(child);
		}

		public void writeInto(SnFile sn, ByteBuffer buf) {
			buf.addInt(0xdefaced);

			int offset = buf.offset();

			buf.addLength(0);

			buf.addCPIndex(sn.getConstantPool().addCPUtf8(key));

			for (Tree e : getChildren()) {
				e.writeInto(sn, buf);
			}

			buf.setLength(offset, buf.offset() - offset - 4);
		}

		public List<Tree> getChildren() {
			return children;
		}

		public Object getValue() {
			return null;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(key);
			sb.append(" ");
			for (Tree t : getChildren()) {
				sb.append(" ");
				sb.append(t);
			}
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void dump(PrintStream out, int indent) {
			indent(out, indent);
			out.print("(");
			out.print(key);
			for (int i = 0; i < children.size(); i++) {
				Tree t = children.get(i);
				out.println();
				t.dump(out, indent + 1);
			}
			out.print(")");
		}
	}

	public static class Leaf extends Tree {
		Object value;

		public Leaf(String key, Object value) {
			super(key);
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		public void writeInto(SnFile sn, ByteBuffer buf) {
			buf.addInt(0xdefaced);

			buf.addLength(-1);
			buf.addInt(sn.getConstantPool().addCPUtf8(key));
			buf.addInt(sn.getConstantPool().addCPEntry(value));
		}

		public List<Tree> getChildren() {
			return Collections.EMPTY_LIST;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(key);
			sb.append(": ");
			sb.append(value);
			return sb.toString();
		}

		@Override
		public void dump(PrintStream out, int indent) {
			indent(out, indent);
			out.print("(");
			out.print(key);
			out.print(": ");
			if (value instanceof Object[])
				out.print(Arrays.asList((Object[]) value));
			else
				out.print(value);
			out.print(")");
		}
	}

	public static void indent(PrintStream out, int indent) {
		for (int i = 0; i < indent; i++) {
			out.print(" ");
		}
	}
}
