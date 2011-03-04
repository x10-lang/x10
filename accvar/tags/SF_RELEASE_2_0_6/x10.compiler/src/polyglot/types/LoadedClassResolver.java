/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.io.File;
import java.io.InvalidClassException;
import java.util.*;

import polyglot.frontend.*;
import polyglot.main.Report;
import polyglot.main.Version;
import polyglot.types.reflect.*;
import polyglot.util.*;

/**
 * Loads class information from class files, or serialized class infomation
 * from within class files.  It does not load from source files.
 */
public class LoadedClassResolver implements TopLevelResolver
{
  protected final static int NOT_COMPATIBLE = -1;
  protected final static int MINOR_NOT_COMPATIBLE = 1;
  protected final static int COMPATIBLE = 0;

  protected TypeSystem ts;
  protected TypeEncoder te;
  protected ClassFileLoader loader;
  protected ClassPathResourceLoader pathloader;
  protected Version version;
  protected Set<QName> nocache;
  protected boolean allowRawClasses;

  protected final static Collection<String> report_topics = CollectionUtil.list(
    Report.types, Report.resolver, Report.loader);

  /**
   * Create a loaded class resolver.
   * @param ts The type system
   * @param classpath The class path
   * @param loader The class file loader to use.
   * @param version The version of classes to load.
   * @param allowRawClasses allow class files without encoded type information 
   */
  public LoadedClassResolver(TypeSystem ts, String classpath,
                             ClassFileLoader loader, Version version,
                             boolean allowRawClasses)
  {
    this.ts = ts;
    this.te = new TypeEncoder(ts);
    this.loader = loader;
    this.pathloader = new ClassPathResourceLoader(classpath);
    this.version = version;
    this.nocache = new HashSet<QName>();
    this.allowRawClasses = allowRawClasses;
  }

  public boolean allowRawClasses() {
    return allowRawClasses;
  }

  public boolean packageExists(QName name) {
    return pathloader.dirExists(name.toString().replace('.', File.separatorChar));
  }

  /**
   * Load a class file for class <code>name</code>.
   */
  protected ClassFile loadFile(QName name) {
    if (nocache.contains(name)) {
        return null;
    }
    
    try {
	String classFileName = name.toString().replace('.', File.separatorChar) + ".class";
	Resource r = pathloader.loadResource(classFileName);
        ClassFile clazz = loader.loadClass(r);

        if (clazz == null) {
            if (Report.should_report(report_topics, 4)) {
                Report.report(4, "Class " + name + " not found in classpath "
                        + pathloader.classpath());
            }
        }
        else {
            if (Report.should_report(report_topics, 4)) {
                Report.report(4, "Class " + name + " found in classpath "
                        + pathloader.classpath());
            }
            return clazz;
        }
    }
    catch (ClassFormatError e) {
        if (Report.should_report(report_topics, 4))
            Report.report(4, "Class " + name + " format error");
    }

    nocache.add(name);

    return null;
  }

  /**
   * Find a type by name.
   */
  public Named find(QName name) throws SemanticException {
    if (Report.should_report(report_topics, 3))
      Report.report(3, "LoadedCR.find(" + name + ")");
 
    Named result = null;
    
    // First try the class file.
    ClassFile clazz = loadFile(name);
    if (clazz == null) {
        throw new NoClassException(name.toString());
    }

    // Check for encoded type information.
    if (clazz.encodedClassType(version.name()) != null) {
      if (Report.should_report(report_topics, 4))
	Report.report(4, "Using encoded class type for " + name);
      result = getEncodedType(clazz, name);
    }
    
    if (allowRawClasses) {
      if (Report.should_report(report_topics, 4))
	Report.report(4, "Using raw class file for " + name);
      result = ts.classFileLazyClassInitializer(clazz).type().asType();
    }
    
    // Verify that the type we loaded has the right name.  This prevents,
    // for example, requesting a type through its mangled (class file) name.
    if (result != null) {
        if (name.equals(result.fullName())) {
            return result;
        }
        if (result instanceof ClassType && name.equals(ts.getTransformedClassName(((ClassType) result).def()))) {
            return result;
        }
    }

    // We have a raw class, but are not allowed to use it, and
    // cannot find appropriate encoded info. 
    throw new SemanticException("Unable to find a suitable definition of \""
        + name +"\". A class file was found,"
        + " but it did not contain appropriate information for this" 
        + " language extension. If the source for this file is written"
        + " in the language extension, try recompiling the source code.");
    
  }

  protected boolean recursive = false;

  /**
   * Extract an encoded type from a class file.
   */
  protected ClassType getEncodedType(ClassFile clazz, QName name)
    throws SemanticException
  {
    // At this point we've decided to go with the Class. So if something
    // goes wrong here, we have only one choice, to throw an exception.

    // Check to see if it has serialized info. If so then check the
    // version.
    int comp = checkCompilerVersion(clazz.compilerVersion(version.name()));

    if (comp == NOT_COMPATIBLE) {
        throw new SemanticException("Unable to find a suitable definition of "
                                    + clazz.name()
                                    + ". Try recompiling or obtaining "
                                    + " a newer version of the class file.");
    }

    // Alright, go with it!
    TypeObject dt;

    try {
        if (Report.should_report(Report.serialize, 1))
            Report.report(1, "Decoding " + name + " in " + clazz);

        dt = te.decode(clazz.encodedClassType(version.name()), name);

        if (dt == null) {
            if (Report.should_report(Report.serialize, 1))
                Report.report(1, "* Decoding " + name + " failed");

            // Deserialization failed because one or more types could not
            // be resolved.  Abort this pass.  Dependencies have already
            // been set up so that this goal will be reattempted after
            // the types are resolved.
            throw new SchedulerException("Could not decode " + name);
        }
    }
    catch (InternalCompilerError e) {
        throw e;
    }
    catch (InvalidClassException e) {
        throw new BadSerializationException(clazz.name());
    }

    if (dt instanceof ClassType) {
        ClassType ct = (ClassType) dt;
        // Install the decoded type into the *new* system resolver.
        // It will be installed into the old resolver below by putAll.
        ts.systemResolver().addNamed(name, ct);

        if (Report.should_report(Report.serialize, 1))
            Report.report(1, "* Decoding " + name + " succeeded");

        if (Report.should_report("typedump", 1)) {
            new ObjectDumper(new SimpleCodeWriter(System.out, 72)).dump(dt);
        }

        if (Report.should_report(Report.serialize, 2)) {
            for (MethodInstance mi : ct.methods()) {
                Report.report(2, "* " + mi);
            }
            for (Iterator<FieldInstance> i = ct.fields().iterator(); i.hasNext(); ) {
                FieldInstance fi = (FieldInstance) i.next();
                Report.report(2, "* " + fi);
            }
            for (Iterator<ConstructorInstance> i = ct.constructors().iterator(); i.hasNext(); ) {
                ConstructorInstance ci = (ConstructorInstance) i.next();
                Report.report(2, "* " + ci);
            }
        }

        if (Report.should_report(report_topics, 2))
            Report.report(2, "Returning serialized ClassType for " +
                          clazz.name() + ".");

        return ct;
    }
    else {
        throw new SemanticException("Class " + name + " not found in " + clazz.name() + ".");
    }
  }
  
  /**
   * Compare the encoded type's version against the loader's version.
   */
  protected int checkCompilerVersion(String clazzVersion) {
    if (clazzVersion == null) {
      return NOT_COMPATIBLE;
    }

    StringTokenizer st = new StringTokenizer(clazzVersion, ".");

    try {
      int v;
      v = Integer.parseInt(st.nextToken());
      Version version = this.version;

      if (v != version.major()) {
	// Incompatible.
	return NOT_COMPATIBLE;
      }

      v = Integer.parseInt(st.nextToken());

      if (v != version.minor()) {
	// Not the best option, but will work if its the only one.
	return MINOR_NOT_COMPATIBLE;
      }
    }
    catch (NumberFormatException e) {
      return NOT_COMPATIBLE;
    }

    // Everything is way cool.
    return COMPATIBLE;
  }
}
