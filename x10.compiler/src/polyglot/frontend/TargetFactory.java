/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.frontend;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import polyglot.main.Reporter;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.UnicodeWriter;

/** A <code>TargetFactory</code> is responsible for opening output files. */
public class TargetFactory
{
    protected File outputDirectory;
    protected String outputExtension;
    protected boolean outputStdout;
    protected Reporter reporter;

    public TargetFactory(File outDir, String outExt, boolean so, Reporter reporter) {
	outputDirectory = outDir;
	outputExtension = outExt;
	outputStdout = so;
	this.reporter = reporter;
    }

    /** Open a writer to the output file for the class in the given package. */
    public Writer outputWriter(QName packageName, Name className,
	    Source source) throws IOException 
    {
	return outputWriter(outputFile(packageName, className, source));
    }

    public CodeWriter outputCodeWriter(File f, int width) throws IOException {
    	Writer w = outputWriter(f);
        return Compiler.createCodeWriter(w, width);
    }

    /** Open a writer to the output file. */
    public Writer outputWriter(File outputFile) throws IOException {
	if (reporter.should_report(Reporter.frontend, 2))
	    reporter.report(2, "Opening " + outputFile + " for output.");

	if (outputStdout) {
	    return new UnicodeWriter(new PrintWriter(System.out));
	}

	if (! outputFile.getParentFile().exists()) {
	    File parent = outputFile.getParentFile();
	    parent.mkdirs(); // ignore return; new FileWriter will check
	}

	return new UnicodeWriter(new FileWriter(outputFile));
    }

    /** Return a file object for the output of the source file in the given package. */
    public File outputFile(QName packageName, Source source) {
	String name;
	name = new File(source.name()).getName();
	name = name.substring(0, name.lastIndexOf('.'));
	return outputFile(packageName, Name.make(name), source);
    }

    /** Return a file object for the output of the class in the given package. */
    public File outputFile(QName packageName, Name className, Source source)
    {
	if (outputDirectory == null) {
	      throw new InternalCompilerError("Output directory not set.");
	}

	String pkgString;
	
	if (packageName == null)
	    pkgString = "";
	else
	    pkgString = packageName.toString();

	File outputFile = new File(outputDirectory,
				   pkgString.replace('.', File.separatorChar)
				   + File.separatorChar
				   + className
				   + "." + outputExtension);

        if (source != null && outputFile.getPath().equals(source.path())) {
	    throw new InternalCompilerError("The output file is the same as the source file");
	}
	
	return outputFile;
    }
}
