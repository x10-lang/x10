/*******************************************************************************
 * Copyright (c) 2002,2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10.sncode;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import x10.sncode.Container.Mapper;

public class SnFile implements SnConstants {
    /** The constant pool */
    private ConstantPool cp;

    /** Sn file major version. */
    int majorVersion;

    /** Sn file minor version. */
    int minorVersion;

    /** The contents. */
    Tree.Branch tree;

    /**
     * Create a blank Sn editor with no members.
     */
    public SnFile() {
        cp = new ConstantPool();
        majorVersion = MAJOR;
        minorVersion = MINOR;
        tree = new Tree.Branch("Sn");
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return majorVersion;
    }

    /**
     * Set the class file format major version. You probably don't want to use
     * this unless you really know what you are doing.
     */
    public void setMajorVersion(int major) {
        if (major < 0 || major > 0xFFFF) {
            throw new IllegalArgumentException("Major version out of range: " + major);
        }
        majorVersion = major;
    }

    /**
     * Set the class file format minor version. You probably don't want to use
     * this unless you really know what you are doing.
     */
    public void setMinorVersion(int minor) {
        if (minor < 0 || minor > 0xFFFF) {
            throw new IllegalArgumentException("Major version out of range: " + minor);
        }
        minorVersion = minor;
    }
    
    public void addClass(ClassEditor e) {
        Tree t = e.makeTree();
		tree.add(t);
    }

    public List<ClassEditor> classes() throws InvalidClassFileException {
        return Container.mapList(tree.findAll("Class"), new Mapper<Tree, ClassEditor, InvalidClassFileException>() {
            @Override
            ClassEditor map(Tree s) throws InvalidClassFileException {
            	ClassEditor e = new ClassEditor();
                e.readFrom(SnFile.this, s);
                return e;
            }
        });
    }

    public void readFrom(ByteBuffer p) throws InvalidClassFileException {
        p.seek(0);
        
        int magic = p.getInt();
        if (magic != SnConstants.MAGIC)
            throw new InvalidClassFileException(p.offset() - 4, "bad magic number 0x" + Integer.toHexString(magic));

        int major = p.getUShort();
        int minor = p.getUShort();

        if (major != MAJOR && minor != MINOR)
            throw new InvalidClassFileException(p.offset() - 4, "bad version; got " + major + "." + minor + ", expected " + MAJOR + "." + MINOR);

        int count = p.getCount();
        cp = new ConstantPool();
        
        ByteBuffer p2 = new ByteBuffer(p.getBytes());
        p2.seek(p.offset());
        ConstantPoolParser cpp = new ConstantPoolParser(p2, count);
		p.seek(p2.offset());
        cp.setRawCP(cpp);

        Tree t = Tree.readFrom(this, p);
        if (t instanceof Tree.Branch)
            tree = (Tree.Branch) t;
        else
            throw new InvalidClassFileException(p.offset(), "bad tree");
    }

    public void writeInto(ByteBuffer w) {
        w.seek(0);
        w.addInt(SnConstants.MAGIC);
        w.addUShort(majorVersion);
        w.addUShort(minorVersion);
        
        Tree tree = this.tree;
        if (tree == null)
            tree = new Tree.Branch("Sn");

        // Output the tree into a separate buffer. This will update the CP.
        ByteBuffer tw = new ByteBuffer();
        tree.writeInto(this, tw);

        // Now, write the CP.
        int beforeCP = w.offset();
        cp.copyInto(w);

        // The constant pool can grow during emission, so seek back to store the
        // size.
        w.setInt(beforeCP, cp.cpItems.size());

        // And finally write the tree.
        w.addBytes(tw.getBytes());
    }

    /**
     * After you've added everything you need to the class, call this method to
     * generate the actual class file data. This can only be called once.
     */
    public byte[] makeBytes() {
        ByteBuffer w = new ByteBuffer(1024);
        writeInto(w);
        return w.getBytes();
    }
    
    static <T> List<T> toList(Object l) {
        if (l instanceof List)
            return (List<T>) l;
        if (l instanceof Object[]) {
            return (List<T>) Arrays.asList((Object[]) l);
        }
        else
            throw new IllegalArgumentException();
    }

    static <T> ArrayList<T> nonnull(List<T> l) {
        if (l == null)
            return new ArrayList<T>(0);
        else if (l instanceof ArrayList)
            return (ArrayList<T>) l;
        else
            return new ArrayList<T>(l);
    }

	public Tree.Branch tree() {
		return tree;
	}

	public void setCp(ConstantPool cp) {
		this.cp = cp;
	}

	public ConstantPool getConstantPool() {
		return cp;
	}

	public void dump(PrintStream out) {
		out.println("major: " + majorVersion);
		out.println("minor: " + minorVersion);
		out.println("constant pool:");
		cp.dump(out);
		out.println("tree:");
		tree.dump(out);
		out.println();
	}
}
