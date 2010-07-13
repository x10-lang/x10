/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_WORK_DIRECTORY;
import static org.eclipse.imp.x10dt.ui.cpp.debug.utils.PDTUtils.findMatch;
import static org.eclipse.imp.x10dt.ui.cpp.debug.utils.PDTUtils.indexOfSkipBraces;
import static org.eclipse.imp.x10dt.ui.cpp.debug.utils.PDTUtils.lastIndexOfSkipBraces;
import static org.eclipse.imp.x10dt.ui.cpp.debug.utils.X10Utils.FMGL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugCore;
import org.eclipse.imp.x10dt.ui.cpp.debug.IDebuggerTranslator;
import org.eclipse.imp.x10dt.ui.cpp.debug.utils.PDTUtils;
import org.eclipse.imp.x10dt.ui.cpp.launch.builder.CpEntryAsStringFunc;
import org.eclipse.imp.x10dt.ui.cpp.launch.builder.IPathToFileFunc;
import org.eclipse.imp.x10dt.ui.cpp.launch.builder.RuntimeFilter;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections.ListUtils;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import polyglot.ast.ClassDecl;
import polyglot.ast.LocalClassDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10cpp.debug.ClosureVariableMap;
import polyglot.ext.x10cpp.debug.LineNumberMap;
import polyglot.ext.x10cpp.types.X10CPPTypeSystem_c;
import polyglot.ext.x10cpp.visit.Emitter;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Scheduler;
import polyglot.main.Options;
import polyglot.types.FieldInstance;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.NodeVisitor;

import com.ibm.debug.internal.epdc.IEPDCConstants;
import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.EngineRequestException;
import com.ibm.debug.internal.pdt.model.ExprNodeBase;
import com.ibm.debug.internal.pdt.model.ExpressionBase;
import com.ibm.debug.internal.pdt.model.GlobalVariable;
import com.ibm.debug.internal.pdt.model.LocalFilter;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.StackFrame;
import com.ibm.debug.internal.pdt.model.ViewFile;

@SuppressWarnings("restriction")
public final class X10DebuggerTranslator implements IDebuggerTranslator {

  // --- Interface methods implementation

  public Location getCppLocation(final DebuggeeProcess process, final String x10File, final int x10LineNumber) {
    final String x10FileLoc;
    if (!x10File.startsWith("file:/")) {
      x10FileLoc = "file:/" + x10File;
    } else {
      x10FileLoc = x10File;
    }
    final LineNumberMap x10LineToCppLineMap = getX10ToCppLineMap(process, x10FileLoc, x10LineNumber);
    final String cppFile = x10LineToCppLineMap.getSourceFile(x10LineNumber);
    final int cppLineNumber = x10LineToCppLineMap.getSourceLine(x10LineNumber);
    if (cppFile == null || cppLineNumber == -1) {
      return null;
    }
    final ViewFile viewFile = PDTUtils.searchViewFile((PICLDebugTarget) process.getDebugTarget(), process, cppFile);
    return new Location(viewFile, cppLineNumber);
  }

  public String[] getStructDescriptor(String type) {
    if (type.startsWith("class ") && type.endsWith("*"))
      type = "x10aux::ref<" + type.substring("class ".length(), type.length() - 1) + ">";
    if (type.endsWith(" "))
      type = type.trim();
    if (type.endsWith("&"))
      type = type.substring(0, type.length() - 1);
    type = stripPrefixes(type, new String[] { "class ref<", "x10aux__ref<", "class x10aux::ref<", "x10aux::ref<" });
    if (type == null)
      return null;
    if (type.endsWith(" "))
      type = type.trim();
    // So now we have the contents of the ref
    String x10Type = getX10TypeFromCppType("x10aux__ref<" + type + " >");
    // Arrays are special
    if (type.startsWith("Rail<") || type.startsWith("ValRail<") || type.startsWith("x10__lang__Rail<")
        || type.startsWith("x10__lang__ValRail<") || type.startsWith("x10::lang::Rail<")
        || type.startsWith("x10::lang::ValRail<")) {
      int paramStart = type.indexOf("<");
      assert (type.endsWith(">"));
      String cppElementType = type.substring(paramStart + 1, type.length() - 1);
      // String x10ElementType = getX10TypeFromCppType(cppElementType, null);
      return new String[] { x10Type, cppElementType, null };
    }
    // So are strings
    if (type.equals("String") || type.equals("x10__lang__String") || type.equals("x10::lang::String")) {
      return new String[] { x10Type, null, null };
    }
    // Must be a class
    X10ParsedClassType t = (X10ParsedClassType) loadType(x10Type);
    if (t == null)
      return null;
    // Filter out static fields
    List<FieldInstance> fields = new ArrayList<FieldInstance>();
    for (FieldInstance f : t.fields()) {
      if (!f.flags().isStatic())
        fields.add(f);
    }
    int outer = t.isInnerClass() ? 2 : 0;
    // TODO: allow showing static fields as well
    String[] desc = new String[2 + outer + 2 * fields.size()];
    desc[0] = x10Type;
    desc[1] = Integer.toString(t.interfaces().size());
    int i = 2;
    if (t.isInnerClass()) {
      desc[i++] = InnerClassRemover.OUTER_FIELD_NAME.toString();
      desc[i++] = Emitter.translateType(t.outer(), true);
    }
    for (FieldInstance f : fields) {
      desc[i++] = f.name().toString();
      desc[i++] = Emitter.translateType(f.type(), true);
    }
    return desc;
  }

  public String getX10File(final DebuggeeProcess process, final Location cppLocation) {
    final String cppFile = getCppFile(process, cppLocation);
    final int cppLineNumber = getCppLine(process, cppLocation);
    final LineNumberMap cppLineToX10LineMap = getCppToX10LineMap(process, cppFile);
    final String x10File = cppLineToX10LineMap.getSourceFile(cppLineNumber);
    return (x10File != null && x10File.startsWith("file:/")) ? x10File.substring("file:/".length()) : x10File;
  }

  public String getX10Function(final DebuggeeProcess process, String cppFunction, final Location cppLocation) {
    final String cppFile = getCppFile(process, cppLocation);
    final LineNumberMap cppLineToX10LineMap = getCppToX10LineMap(process, cppFile);
    String x10Function = cppLineToX10LineMap.getMappedMethod(cppFunction);
    if (x10Function == null) { // now try alternate forms of primitives
      if (inClosure(process, cppFunction)) {
        return "closure in "+getX10File(process, cppLocation)+" (possibly an async)";
      }
      cppFunction = cppFunction.replaceAll("\\b(int|short|double|float)\\b", "x10_$1");
      x10Function = cppLineToX10LineMap.getMappedMethod(cppFunction);
      if (x10Function == null) { // now try adding spaces before closing type arg braces for Rail and ValRail
        int b = 0;
        while ((b = cppFunction.indexOf("Rail<", b)) != -1) {
          b += "Rail<".length() - 1;
          int m = PDTUtils.findMatch(cppFunction, b);
          assert (m != -1);
          if (cppFunction.charAt(m - 1) != ' ')
            cppFunction = cppFunction.substring(0, m) + " " + cppFunction.substring(m);
        }
        x10Function = cppLineToX10LineMap.getMappedMethod(cppFunction);
        if (x10Function == null) { // now try adding spaces before all closing type arg braces
        	cppFunction = cppFunction.replaceAll("(?<!\\s)>", " >");
        	x10Function = cppLineToX10LineMap.getMappedMethod(cppFunction);
        }
      }
    }
    if (x10Function != null)
      x10Function = x10Function.replace("::", ".");
    return x10Function;
  }

  public int getX10Line(final DebuggeeProcess process, final Location cppLocation) {
    final String cppFile = getCppFile(process, cppLocation);
    final int cppLineNumber = getCppLine(process, cppLocation);
    final LineNumberMap cppLineToX10LineMap = getCppToX10LineMap(process, cppFile);
    int line = cppLineToX10LineMap.getSourceLine(cppLineNumber);
	return line;
  }

  // --- Internal services

  public void init(final IProject project) {
    this.fCompiler = Globals.Compiler();
    if (this.fCompiler == null) {
      ExtensionInfo extInfo = new polyglot.ext.x10cpp.ExtensionInfo() {
        public Scheduler createScheduler() {
          return new X10CPPScheduler(this) {
            public List<Goal> goals(final Job job) {
              ArrayList<Goal> goals = new ArrayList<Goal>();
              goals.add(Parsed(job));
              goals.add(TypesInitialized(job));
              goals.add(ImportTableInitialized(job));
              goals.add(CastRewritten(job));
              goals.add(PreTypeCheck(job));
              goals.add(TypeChecked(job));
              goals.add(End(job));
              return goals;
            }
          };
        }
      };
      buildOptions(JavaCore.create(project), extInfo.getOptions());
      this.fCompiler = new Compiler(extInfo, new ErrorQueue() {
        public boolean hasErrors() {
          return false;
        }

        public void flush() {
        }

        public int errorCount() {
          return 0;
        }

        public void enqueue(ErrorInfo e) {
          System.out.println(e.getErrorString());
        }

        public void enqueue(int type, String message, Position position) {
          System.out.println(message);
        }

        public void enqueue(int type, String message) {
          System.out.println(message);
        }
      });
      Globals.initialize(this.fCompiler);
    }
    this.fTypeSystem = (X10CPPTypeSystem_c) this.fCompiler.sourceExtension().typeSystem();
  }

  // --- Private code

  private void buildOptions(final IJavaProject project, final Options options) {
    try {
      // Sets the class path
      final Set<String> cps = JavaProjectUtils.getFilteredCpEntries(project, new CpEntryAsStringFunc(),
                                                                    new AlwaysTrueFilter<IPath>());
      final StringBuilder cpBuilder = new StringBuilder();
      int i = -1;
      for (final String cpEntry : cps) {
        if (++i > 0) {
          cpBuilder.append(File.pathSeparatorChar);
        }
        cpBuilder.append(cpEntry);
      }
      // Sets the source path.
      final Set<IPath> srcPaths = JavaProjectUtils.getFilteredCpEntries(project, new IdentityFunctor<IPath>(),
                                                                        new RuntimeFilter());
      // We can now set all the Polyglot options for our extension.
      options.assertions = true;
      options.classpath = cpBuilder.toString();
      options.output_classpath = options.classpath;
      options.serialize_type_info = false;
      options.post_compiler = null;
      options.source_path = ListUtils.transform(srcPaths, new IPathToFileFunc());
      options.compile_command_line_only = true;
    } catch (JavaModelException except) {
      DebugCore.log(except.getStatus());
    }
  }

  private String[] extractTypeArguments(final String typeArgsList) {
    final ArrayList<String> res = new ArrayList<String>();
    final StringTokenizer st = new StringTokenizer(typeArgsList, "<>, ", true);
    StringBuilder sb = new StringBuilder();
    int d = 0;
    while (st.hasMoreTokens()) {
      String t = st.nextToken();
      if (t.equals(",") && d == 0) {
        res.add(getX10TypeFromCppType(sb.toString()));
        sb = new StringBuilder();
      } else {
        if (!t.equals(" "))
          sb.append(t);
        if (t.equals("<")) {
          d++;
        } else if (t.equals(">")) {
          d--;
        }
      }
    }
    assert (d == 0);
    res.add(getX10TypeFromCppType(sb.toString()));
    return res.toArray(new String[res.size()]);
  }

  private GlobalVariable[] findLineNumberMaps(final DebuggeeProcess process, String cppFile) {
    GlobalVariable[] res = new GlobalVariable[1];
    // TODO: find the appropriate part and extract the variable directly
    System.err.println("Looking for mapping info for '" + cppFile + "'");
    if (cppFile.indexOf('/') == -1)
      cppFile = "/" + cppFile;
    String name = "LNMAP_" + cppFile.replace('/', '_').replace('.', '_');
    GlobalVariable[] globals = process.getDebugEngine().getGlobalVariables();
    for (GlobalVariable v : globals) {
      if (v.getName().equals(name)) {
        System.err.println("\tFound mapping info for '" + cppFile + "'");
        res[0] = v;
        break;
      }
    }
    return res;
  }

  private GlobalVariable[] findX10LineNumberMaps(final DebuggeeProcess p, String x10File, String className) {
    GlobalVariable[] res = new GlobalVariable[3];
    int i = 0;
    // TODO: find the appropriate part and extract the variable directly
    System.err.println("Looking for mapping info for '" + x10File + "'");
    x10File = x10File.replace('\\', '/'); // normalize directory separators
    String nameNoExt = x10File.substring(0, x10File.lastIndexOf('/') + 1) + className;
    GlobalVariable[] globals = p.getDebugEngine().getGlobalVariables();
    for (GlobalVariable v : globals) {
      String name = v.getName();
      if (!name.startsWith("LNMAP_"))
        continue;
      String ext = name.substring(name.lastIndexOf('_'));
      // assert (ext.equals("_cc") || ext.equals("_h") || ext.equals("_inc"));
      String fName = name.substring("LNMAP_".length(), name.length() - ext.length());
      if (fName.startsWith("_"))
        fName = fName.substring(1);
      int len = fName.length();
      // Try to match the whole file name
      String baseName = nameNoExt.substring(nameNoExt.length() - len).replace('/', '_');
      if (!fName.equals(baseName))
        continue;
      System.err.println("\tFound mapping info for '" + x10File + "'");
      res[i++] = v;
    }
    return res;
  }

  private String getClassName(String x10File, int x10Line) {
    if (x10File.startsWith("file:/"))
      x10File = x10File.substring("file:/".length());
    String[] classes = this.fX10ClassMap.get(x10File);
    if (classes == null) {
      FileSource source = null;
      try {
        source = this.fCompiler.sourceExtension().sourceLoader().fileSource(x10File, true);
      } catch (IOException e) {
      }
      Scheduler scheduler = this.fCompiler.sourceExtension().scheduler();
      ArrayList<Job> jobs = new ArrayList<Job>();
      Job job = scheduler.addJob(source);
      jobs.add(job);
      Globals.initialize(this.fCompiler);
      scheduler.setCommandLineJobs(jobs);
      scheduler.addDependenciesForJob(job, true);
      scheduler.runToCompletion(scheduler.TypeChecked(job));
      Node ast = job.ast();
      assert (ast instanceof SourceFile);
      final ArrayList<String> classnames = new ArrayList<String>();
      // FIXME: deal with local classes
      ast.visit(new NodeVisitor() {
        private ArrayList<String> path = new ArrayList<String>();

        public NodeVisitor enter(Node n) {
          if (n instanceof ClassDecl)
            this.path.add(((ClassDecl) n).name().toString());
          else if (n instanceof LocalClassDecl)
            this.path.add(((LocalClassDecl) n).position().toString());
          return this;
        }

        public Node leave(Node old, Node n, NodeVisitor v) {
          if (n instanceof ClassDecl || n instanceof LocalClassDecl) {
            int start = n.position().line();
            int end = n.position().endLine();
            String p = this.path.toString();
            p = p.substring(1, p.length() - 1).replace(", ", "__");
            classnames.add(p + ":" + start + ":" + end);
            this.path.remove(this.path.size() - 1);
          }
          return n;
        }
      });
      classes = classnames.toArray(new String[classnames.size()]);
      this.fX10ClassMap.put(x10File, classes);
    }
    for (int i = 0; i < classes.length; i++) {
      String cInfo = classes[i];
      int l = cInfo.indexOf(':');
      int m = cInfo.lastIndexOf(':');
      assert (l != -1 && m != -1);
      int s = Integer.parseInt(cInfo.substring(l + 1, m));
      int e = Integer.parseInt(cInfo.substring(m + 1));
      if (s <= x10Line && e >= x10Line)
        return cInfo.substring(0, l);
    }
    return null;
  }

  private String getCppFile(final DebuggeeProcess process, final Location cppLocation) {
    String cppFile = cppLocation.getViewFile().getName();
    try {
      String baseDir = process.getDebugTarget().getLaunch().getLaunchConfiguration().getAttribute(ATTR_WORK_DIRECTORY,
                                                                                                  (String) null);
      if (cppFile.startsWith(baseDir + "/"))
        cppFile = cppFile.substring(baseDir.length() + 1);
    } catch (CoreException e) {
    }
    if (cppFile.startsWith("gen/"))
      cppFile = cppFile.substring("gen/".length());
    return cppFile;
  }

  private int getCppLine(final DebuggeeProcess process, final Location cppLocation) {
    return cppLocation.getLineNumber();
  }

  private LineNumberMap getCppToX10LineMap(final DebuggeeProcess process, final String cppFile) {
    LineNumberMap map = this.fCppToX10Map.get(cppFile);
    if (map == null) {
      readLineNumberMaps(process, findLineNumberMaps(process, cppFile));
      map = this.fCppToX10Map.get(cppFile);
      if (map == null)
        this.fCppToX10Map.put(cppFile, map = new LineNumberMap(cppFile));
    }
    assert (map != null);
    return map;
  }

  private LineNumberMap getX10ToCppLineMap(final DebuggeeProcess process, final String x10File, final int x10Line) {
    String className = getClassName(x10File, x10Line);
    LineNumberMap map = this.fX10ToCppMap.get(x10File + "|" + className);
    if (map == null) {
      readLineNumberMaps(process, findX10LineNumberMaps(process, x10File, className));
      map = this.fX10ToCppMap.get(x10File);
      if (map == null)
        this.fX10ToCppMap.put(x10File, map = new LineNumberMap(x10File));
      this.fX10ToCppMap.put(x10File + "|" + className, map);
    }
    assert (map != null);
    return map;
  }

  private String getX10TypeFromCppType(String type) {
    if (type.endsWith(" "))
      type = type.trim();
    if (type.startsWith("x10aux__ref<") || type.startsWith("x10aux::ref")) {
      assert (type.endsWith(">"));
      type = type.substring("x10aux__ref<".length(), type.length() - 1);
      if (type.endsWith(" "))
        type = type.trim();
      int paramStart = type.indexOf("<");
      String[] typeArgs = paramStart == -1 ? null
                                          : extractTypeArguments(type.substring(paramStart + 1, type.lastIndexOf('>')));
      String cppType = paramStart == -1 ? type : type.substring(0, paramStart);
      String args = "";
      if (typeArgs != null) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < typeArgs.length; i++) {
          if (i > 0)
            sb.append(",");
          sb.append(typeArgs[i]);
        }
        sb.append("]");
        args = sb.toString();
      }
      return cppType.replace("__", ".").replace("::", ".") + args;
    } else if (type.equals("int") || type.equals("x10_int")) {
      return "x10.lang.Int";
    } else if (type.equals("float") || type.equals("x10_float")) {
      return "x10.lang.Float";
    } else if (type.equals("double") || type.equals("x10_double")) {
      return "x10.lang.Double";
    } else if (type.equals("bool") || type.equals("x10_boolean")) {
    	return "x10.lang.Boolean";
    } else if (type.equals("x10_long")) {
      return "x10.lang.Long";
    } else if (type.equals("x10_short")) {
    	return "x10.lang.Short";
    } else if (type.equals("x10_byte")) {
    	return "x10.lang.Byte";
    } else if (type.equals("x10_char")) {
    	return "x10.lang.Char";
    } else {
      return "UNKNOWN";
    }
  }

  private Type loadType(String x10Type) {
    Globals.initialize(this.fCompiler);
    try {
      String[] args = null;
      if (x10Type.contains("[")) {
        // parse out the type arguments
        if (x10Type.endsWith("]")) { // the last type is parametric - keep the type arguments
          int typeArgsEnd = x10Type.length()-1;
          int typeArgsStart = findMatch(x10Type, typeArgsEnd);
          String allArgs = x10Type.substring(typeArgsStart+1, typeArgsEnd);
          x10Type = x10Type.substring(0, typeArgsStart);
          ArrayList<String> a = new ArrayList<String>();
          for (int c = indexOfSkipBraces(allArgs, ',', 0); c != -1; c = indexOfSkipBraces(allArgs, ',', c)) {
            a.add(allArgs.substring(0, c));
            allArgs = allArgs.substring(c+1).trim();
          }
          a.add(allArgs);
          args = a.toArray(new String[a.size()]);
        }
        // TODO: parse out all other braces from x10Type
      }
      Type loadedType = this.fTypeSystem.typeForName(QName.make(x10Type));
      if (args != null) {
        ArrayList<Type> params = new ArrayList<Type>();
        for (int i = 0; i < args.length; i++) {
		  params.add(loadType(args[i]));
        }
        loadedType = ((X10ParsedClassType)loadedType).typeArguments(params);
      }
      return loadedType;
    } catch (SemanticException except) {
      DebugCore.log(IStatus.ERROR, "Unable to load type " + x10Type, except);
    }
    return null;
  }

  private void readLineNumberMaps(DebuggeeProcess p, GlobalVariable[] vars) {
    assert (vars != null);
    for (GlobalVariable v : vars) {
      if (v == null)
        continue;

      System.out.println("\tGOT MAP: " + v.getName() + " " + v.getExpression());
      System.err.println("Reading mapping info from " + v.getName());
      String val = null;
      try {
        DebuggeeThread t = p.getStoppingThread();
        // The code below doesn't create the right kind of monitor.
        // ExpressionBase b = t.evaluateExpression(t.getLocation(t.getViewInformation()), v.getExpression(), 1, 1000000);
        ExpressionBase b = p.monitorExpression(t.getLocation(t.getViewInformation()).getEStdView(), t.getId(),
                                               v.getExpression(), IEPDCConstants.MonEnable, IEPDCConstants.MonTypeProgram,
                                               null, null, null, null);
        // TODO
        // Address addr = p.convertToAddress(v.getExpression(), t.getLocation(t.getViewInformation()), t);
        if (b != null) {
          ExprNodeBase n = b.getRootNode();
          if (n != null)
            val = n.getValueString();
          b.remove();
        }
      } catch (EngineRequestException except) {
        DebugCore.log(IStatus.ERROR, "Monitor expression failed by engine", except);
      }
      System.out.println("\tValue = '" + val + "'");
      if (val == null)
        continue;
      if (val.endsWith("..."))
        val = val.substring(0, val.lastIndexOf(',') + 1) + "}\""; // FIXME: damage control
      assert (val.startsWith("\"") && val.endsWith("\""));
      LineNumberMap c2xFileMap = LineNumberMap.importMap(val.substring(1, val.length() - 1));
      HashMap<String, LineNumberMap> c2xMap = new HashMap<String, LineNumberMap>();
      c2xMap.put(c2xFileMap.file(), c2xFileMap);
      LineNumberMap.mergeMap(this.fCppToX10Map, c2xMap);
      HashMap<String, LineNumberMap> x2cMap = c2xFileMap.invert();
      LineNumberMap.mergeMap(this.fX10ToCppMap, x2cMap);
    }
  }
  
  public static boolean inClosure(DebuggeeProcess p, String function) {
    return function.matches(".*__closure__\\d+::apply\\(\\)");
  }
  
  public static final String SAVED_THIS = "saved_this";
  public String[] getClosureVars(DebuggeeProcess process, StackFrame frame, Location location, String function) {
    assert (function.matches(".*__closure__\\d+::apply\\(\\)"));
    String closure = function.substring(0, function.indexOf("::apply()"));
    final ClosureVariableMap map = getClosureInfoMap(process, frame, location, closure);
    String[] variables = map.getVariables();
    for (int i = 0; i < variables.length; i++) {
	  if (variables[i].equals(SAVED_THIS))
	    variables[i] = "this";
	}
	return variables;
  }
  
  public String getClosureVariableType(DebuggeeProcess process, StackFrame frame, Location location, String function, String name) {
    assert (function.matches("__closure__\\d\\+::apply()$"));
    String closure = function.substring(0, function.indexOf("::apply()"));
    final ClosureVariableMap map = getClosureInfoMap(process, frame, location, closure);
    return map.get(name);
  }
	  
  private static final ClosureVariableMap EMPTY_CLOSURE_MAP = new ClosureVariableMap();
  private ClosureVariableMap getClosureInfoMap(final DebuggeeProcess process, final StackFrame frame, final Location location, final String closure) {
    ClosureVariableMap map = this.fClosureInfoMap.get(closure);
    if (map == null) {
      readClosureInfo(process, frame, location, closure);
      map = this.fClosureInfoMap.get(closure);
      if (map == null)
        this.fClosureInfoMap.put(closure, map = EMPTY_CLOSURE_MAP);
    }
    assert (map != null);
    return map;
  }

  public static final String VARIABLE_NOT_FOUND = "Variable was not found.";
  public static final String NOT_IN_SCOPE = "Not within current scope";
  private void readClosureInfo(final DebuggeeProcess p, final StackFrame frame, final Location location, final String closure) {
    System.err.println("Reading closure mapping info for " + closure);
    String val = null;
    try {
      String SHOW_STATICS = "Show static variables from the file scope";
      String name = closure+"::"+ClosureVariableMap.VARIABLE_NAME;
      LocalFilter[] localFilters = p.getDebugEngine().getLocalFilters();
      boolean[] savedFilterState = new boolean[localFilters.length]; 
      for (int i = 0; i < localFilters.length; i++) {
        savedFilterState[i] = localFilters[i].isEnabled();
        if (localFilters[i].getFilterLabel().equals(SHOW_STATICS))
          localFilters[i].enable();
        else
          localFilters[i].disable();
      }
      p.getStoppingThread().stopMonitoringLocalVariables();
      ExprNodeBase[] locals = p.getStoppingThread().getLocals(frame);
      for (int i = 0; i < locals.length; i++) {
        if (locals[i].getExpression().getExpressionString().equals(name)) {
          val = locals[i].getValueString();
          break;
        }
      }
      for (int i = 0; i < localFilters.length; i++) {
        if (savedFilterState[i])
          localFilters[i].enable();
        else
          localFilters[i].disable();
      }
      p.getStoppingThread().getLocals(frame); // resume monitoring
//      GlobalVariable var = null;
//      GlobalVariable[] globals = p.getDebugEngine().getGlobalVariables();
//      for (GlobalVariable v : globals) {
//        if (v.getName().equals(name)) {
//          System.err.println("\tFound mapping info for closure '" + closure + "'");
//          var = v;
//          break;
//        }
//      }
//      if (var == null)
//        return;
//      DebuggeeThread t = p.getStoppingThread();
//      // The code below doesn't create the right kind of monitor.
//      // ExpressionBase b = t.evaluateExpression(t.getLocation(t.getViewInformation()), v.getExpression(), 1, 1000000);
//      ExpressionBase b = p.monitorExpression(location.getEStdView(), t.getId(),
//                                             var.getExpression(), IEPDCConstants.MonEnable, IEPDCConstants.MonTypeProgram,
//                                             null, null, null, null);
//      // FIXME: using name as the expression should also work (!)
      // TODO
      // Address addr = p.convertToAddress(v.getExpression(), t.getLocation(t.getViewInformation()), t);
//      if (b != null) {
//        ExprNodeBase n = b.getRootNode();
//        if (n != null)
//          val = n.getValueString();
//        b.remove();
//      }
    } catch (EngineRequestException except) {
      DebugCore.log(IStatus.ERROR, "Monitor expression failed by engine", except);
    }
    System.out.println("\tValue = '" + val + "'");
    if (val == null)
      return;
    if (val.equals(VARIABLE_NOT_FOUND))
      val = "\"" + EMPTY_CLOSURE_MAP.exportMap() + "\"";
    if (val.endsWith("..."))
      val = val.substring(0, val.lastIndexOf(',') + 1) + "}\""; // FIXME: damage control
    assert (val.startsWith("\"") && val.endsWith("\""));
    ClosureVariableMap map = ClosureVariableMap.importMap(val.substring(1, val.length() - 1));
    this.fClosureInfoMap.put(closure, map);
  }

  private String stripPrefixes(String type, String[] prefixes) {
    for (int i = 0; i < prefixes.length; i++) {
      if (type.startsWith(prefixes[i]))
        return stripPrefix(type, prefixes[i]);
    }
    return null;
  }

  private String stripPrefix(String type, String prefix) {
    assert (prefix.endsWith("<"));
    assert (type.startsWith(prefix));
    assert (type.endsWith(">"));
    return type.substring(prefix.length(), type.length() - 1);
  }

  // --- Fields

  private X10CPPTypeSystem_c fTypeSystem;
  private final HashMap<String, LineNumberMap> fX10ToCppMap = LineNumberMap.initMap();
  private final HashMap<String, LineNumberMap> fCppToX10Map = LineNumberMap.initMap();
  private final HashMap<String, ClosureVariableMap> fClosureInfoMap = new HashMap<String, ClosureVariableMap>();
  private final HashMap<String, String[]> fX10ClassMap = new HashMap<String, String[]>();
  private Compiler fCompiler;
}
