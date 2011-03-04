/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types.reflect;

import polyglot.main.Reporter;
import polyglot.util.FileUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Resource;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.jar.*;

/**
 * We implement our own class loader.  All this pain is so
 * we can define the classpath on the command line.
 */
public class ClassFileLoader
{
    /** The extension info */
    protected ExtensionInfo extensionInfo;
    protected Reporter reporter;

    public ClassFileLoader(ExtensionInfo ext) {
        this.extensionInfo = ext;
        this.reporter = ext.getOptions().reporter;
    }


    /**
     * Try to find the class <code>name</code> in the directory or jar or zip
     * file <code>dir</code>.
     * If the class does not exist in the specified file/directory, then
     * <code>null</code> is returned.
     */
    public ClassFile loadClass(Resource r) {
	if (r == null)
	    return null;
	
	ClassFile c;
	try {
	    InputStream in = r.getInputStream();
	    c = loadFromStream(r.file(), in, r.name());
	    in.close();
	}
	catch (IOException e) {
	    return null;
	}
	return c;
    }

    /**
     * Load a class from an input stream.
     */
    protected ClassFile loadFromStream(File source, InputStream in, String name) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buf = new byte[4096];
        int n = 0;

        do {
            n = in.read(buf);
            if (n >= 0) out.write(buf, 0, n);
        } while (n >= 0);

        byte[] bytecode = out.toByteArray();

        try {
            if (reporter.should_report(Reporter.loader, 3))
                reporter.report(3, "defining class " + name);
            return createClassFile(source, bytecode);
        }
        catch (ClassFormatError e) {
            throw new IOException(e.getMessage());
        }
    }

    protected ClassFile createClassFile(File source, byte[] bytecode) {
        return new ClassFile(source, bytecode, extensionInfo);
    }

}
