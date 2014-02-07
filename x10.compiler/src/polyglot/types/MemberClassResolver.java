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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.types;

import java.util.*;

import polyglot.main.Reporter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import x10.util.CollectionFactory;

/**
 * Loads member classes using a TopLevelResolver that can only handle
 * top-level classes.
 */
public class MemberClassResolver implements TopLevelResolver
{
    protected TypeSystem ts;
    protected TopLevelResolver inner;
    protected boolean allowRawClasses;
    protected Set<QName> nocache;

  protected final static Collection<String> report_topics =
      CollectionUtil.list(Reporter.types, Reporter.resolver, Reporter.loader, "mcr");

  /**
   * Create a member class resolver.
   * @param ts The type system
   * @param inner The resolver for top-level classes
   */
  public MemberClassResolver(TypeSystem ts, TopLevelResolver inner, boolean allowRawClasses) {
    this.ts = ts;
    this.inner = inner;
    this.allowRawClasses = allowRawClasses;
    this.nocache = CollectionFactory.newHashSet();
  }

  public Package findPackage(QName name) throws SemanticException {
    return inner.findPackage(name);
  }

  public boolean packageExists(QName name) {
    return inner.packageExists(name);
  }

  /**
   * Find a type by name.
   */
  public List<Type> find(QName name) throws SemanticException {
    Reporter reporter = ts.extensionInfo().getOptions().reporter;
    if (reporter.should_report(report_topics, 3))
      reporter.report(3, "MemberCR.find(" + name + ")");


    if (nocache.contains(name)) {
        throw new NoClassException(name.toString());
    }

    List<Type> n = ts.systemResolver().check(name);

    if (n != null) {
        return n;
    }

    SemanticException error = null;

    // First, just try the long name.
    try {
        if (reporter.should_report(report_topics, 2))
            reporter.report(2, "MCR: loading " + name + " from " + inner);
        return inner.find(name);
    }
    catch (SemanticException e) {
        if (reporter.should_report(report_topics, 2))
            reporter.report(2, "MCR: " + e.getMessage());
        if (name.qualifier() == null) {
            throw e;
        }
        error = e;
    }

    boolean install = true;

    // Now try the prefix of the name and look for a member class
    // within it named with the suffix.
    QName prefix = name.qualifier();
    Name suffix = name.name();

    // We should have thrown an exception above if prefix is null.
    assert prefix != null;

    // Try the full name of the prefix first, then the raw class name,
    // so that encoded type information and source files are preferred
    // to the raw class file.
    try {
        if (reporter.should_report(report_topics, 2))
            reporter.report(2, "MCR: loading prefix " + prefix);

        n = find(prefix);

        List<Type> result = new ArrayList<Type>();
        for (Type q : n) {
            result.addAll(findMember(q, suffix));
        }
        return result;
    }
    catch (SemanticException e) {
    }

    if (install) {
        nocache.add(name);
    }

    throw error;
  }

  /**
   * Find a single type by name.
   */
  public Type findOne(QName name) throws SemanticException {
    List<Type> res = find(name);
    if (res == null || res.size() != 1)
      throw new InternalCompilerError("Unexpected result when looking up "+name+": "+res);
    return res.get(0);
  }

  protected List<Type> findMember(Type container, Name name) throws SemanticException {
      if (container instanceof ClassType) {
          ClassType ct = (ClassType) container;

          Reporter reporter = ts.extensionInfo().getOptions().reporter;
          if (reporter.should_report(report_topics, 2))
              reporter.report(2, "MCR: found prefix " + ct);

          // Uncomment if we should search superclasses
          // return ct.resolver().find(name);
          Type n = ct.memberTypeMatching(ts.MemberTypeMatcher(ct, name, ts.emptyContext()));

          if (n != null) {
              if (reporter.should_report(report_topics, 2))
                  reporter.report(2, "MCR: found member of " + ct + ": " + n);
              return CollectionUtil.<Type>list(n);
          }
      }

      throw new NoClassException(container.fullName() + "." + name);
  }
}
