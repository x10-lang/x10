package x10dt.core.tests.compiler;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Import;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.MethodDef;
import polyglot.types.ObjectType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.types.ClosureType_c;
import x10.types.MethodInstance;
import x10.types.X10ClassType;

/**
 * This class provides some base functionality for compilation
 * @author mvaziri
 *
 */
public class CompilerTestsBase {
	
    protected static String[] STATIC_CALLS = {"-STATIC_CALLS=true", "-CHECK_INVARIANTS"};
    protected static String[] NOT_STATIC_CALLS = {"-STATIC_CALLS=false", "-CHECK_INVARIANTS"};

    private static String OUTPUT_DIR = "output";
    
    private static String SEVERE_MODE = "severe";

    public boolean compile(File[] files, String[] options,
            final Collection<ErrorInfo> errors, String sourcepath) throws Exception {
        return compile(files, options, errors, sourcepath, new ArrayList<Job>());
    }



    /**
     *
     * @param files
     * @param options
     * @param sourcepath
     * @param jobs: non-null collection of jobs to return to caller (from which ASTs can be extracted and visited)
     * @return true if compilation succeeds without errors
     * @throws Exception
     */
    public boolean compile(File[] files, String[] options,
            final Collection<ErrorInfo> errors, String sourcepath, Collection<Job> jobs) throws Exception {

        Collection<String> paths = new ArrayList<String>();
        for (File f : files) {
            paths.add(f.getPath());
        }
        try {
            ExtensionInfo extInfo = new x10c.ExtensionInfo();
            Compiler compiler = getCompiler(extInfo, options, errors, sourcepath);
            Globals.initialize(compiler);
            compiler.compileFiles(paths);
            return outcome(paths, options, sourcepath, errors, jobs, extInfo);

        } catch (Throwable e) {
            throw new Exception(getTestId(paths, options), e);
        }
    }

    /**
     *
     * @param files
     * @param options
     * @param sourcepath
     * @param jobs: non-null collection of jobs to return to caller (from which ASTs can be extracted and visited)
     * @return true if compilation succeeds without errors
     * @throws Exception
     */
    public boolean compileStreams(Source[] files, String[] options,
            final Collection<ErrorInfo> errors, String sourcepath, Collection<Job> jobs) throws Exception {
        Collection<String> paths = new ArrayList<String>();
        Collection<Source> sources = new ArrayList<Source>();
        for (Source f : files) {
            paths.add(f.path());
            sources.add(f);
        }
        try {
            ExtensionInfo extInfo = new x10c.ExtensionInfo();
            Compiler compiler = getCompiler(extInfo, options, errors, sourcepath);
            Globals.initialize(compiler);
            compiler.compile(sources);
            return outcome(paths, options, sourcepath, errors, jobs, extInfo);

        } catch (Throwable e) {
            throw new Exception(getTestId(paths, options), e);
        }
    }




    private Compiler getCompiler(ExtensionInfo extInfo, String[] options, final Collection<ErrorInfo> errors, String sourcepath) {
        buildOptions(extInfo, options, sourcepath);
        return new Compiler(extInfo,
                new AbstractErrorQueue(10000, extInfo.compilerName()) {
                    protected void displayError(ErrorInfo error) {
                        errors.add(error);
                    }
                });
    }

    private boolean outcome(Collection<String> sources, String[] options, String sourcepath, Collection<ErrorInfo> errors, Collection<Job> jobs, ExtensionInfo extInfo) {
        jobs.addAll(extInfo.scheduler().commandLineJobs());

        for (String s : sources) {
            System.err.print(s + " - ");
        }
        for (String s : options) {
            System.err.print(s + " - ");
        }
        System.err.println(sourcepath);
        for (ErrorInfo e : errors) {
            System.err.println(e + ":" + e.getPosition());
        }
        for(ErrorInfo error: errors) {
            Assert.assertFalse("INVARIANT: "+getTestId(sources, options) + error.getMessage(), invariantViolation(error));
            Assert.assertFalse("INTERNAL ERROR: "+getTestId(sources, options) + error.getMessage(), internalError(error));
            if (notSevereMode())
            	Assert.assertFalse("WELL-FORMEDNESS: "+getTestId(sources, options) + error.getMessage(), notWellFormed(error));
        }
        if (notSevereMode())
        	Assert.assertFalse("DUPLICATE: "+getTestId(sources, options), duplicateErrors(errors));

        // --- Now we null out Globals and query the ASTs.
        Globals.initialize(null);
        for(Job job: jobs) {
            checkAST(sources, options, job);
        }
        return errors.isEmpty();
    }

    private void buildOptions(ExtensionInfo extinfo, String[] options, String sourcepath) {
        Options opts = extinfo.getOptions();
        List<String> optsList = new ArrayList<String>();
        String[] stdOptsArray = new String[] {
                "-noserial",
                "-d",
                OUTPUT_DIR,
                "-sourcepath",
                sourcepath,
                "-commandlineonly",
                "-c",
                };
        for (String s : stdOptsArray) {
            optsList.add(s);
        }
        for (String s: options) {
            optsList.add(0, s);
        }

        String[] optsArray = optsList.toArray(new String[optsList.size()]);
        try {
            opts.parseCommandLine(optsArray, new HashSet<String>());
        } catch (UsageError e) {

        }
    }

    protected String getTestId(Collection<String> sources, String[] options) {
        String testId = "";
        for (String f : sources) {
            testId += f + " - ";
        }
        for (String s : options) {
            testId += s + " - ";
        }
        return testId;
    }

    protected static Collection<File> getSources(File dir) {
        Collection<File> results = new ArrayList<File>();
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".x10")) {
                    results.add(f);
                } else if (f.isDirectory()) {
                    results.addAll(getSources(f));
                }
            }
        }
        return results;
    }

    protected static String getRuntimeJar() throws URISyntaxException{
      final URL url = CompilerTestsBase.class.getClassLoader().getResource("x10.jar");
      return toFile(url).getAbsolutePath();
    }

    protected static File toFile(final URL url) throws URISyntaxException {
      return new File(url.toURI());
    }

    private String getErrorString(ErrorInfo e) {
        return e.toString() + (e.getPosition()==null ? "" : (":" + e.getPosition()));
    }


    private boolean duplicateErrors(Collection<ErrorInfo> errors) {
        Map<String,Integer> count = new HashMap<String,Integer>(); //Map of message string to count
        for(ErrorInfo e1: errors) {
            if (e1.getErrorKind() == ErrorInfo.INTERNAL_ERROR) continue;
            String m1 = getErrorString(e1);
            for(ErrorInfo e2: errors) {
                String m2 = getErrorString(e2);
                if(m1.equals(m2)) {
                    if (count.get(m1) == null) {
                        count.put(m1, 1);
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean notWellFormed(ErrorInfo e) {
        // TODO: add other well-formedness conditions here.
        if (e.getMessage() == null)
            return true;
        if (e.getMessage().contains("{amb}"))
            return true;
        if (e.getMessage().contains("<unknown>"))
            return true;
        if (e.getMessage().contains("place_"))
            return true;
        if (e.getMessage().contains("self_"))
            return true;
        if (e.getMessage().contains("#"))
        	return true;
        return false;
    }


    private boolean internalError(ErrorInfo e) {
        if (e.getErrorKind() == ErrorInfo.INTERNAL_ERROR)
            return true;
        return false;
    }

    private boolean invariantViolation(ErrorInfo e) {
        if (e.getErrorKind() == ErrorInfo.INVARIANT_VIOLATION_KIND)
            return true;
        return false;
    }

    private void checkAST(Collection<String> sources, String[] options,  Job job) {
        try {
            job.ast().visit(new ContextVisitor(job,job.extensionInfo().typeSystem(),job.extensionInfo().typeSystem().extensionInfo().nodeFactory()) {

                    @Override
                    public NodeVisitor enterCall(Node n) throws SemanticException {
                    	Position position = n.position();
						if (position == null)
							Assert.assertFalse("AST: position is null for node:" + n, true);
						if (position.file() == null)
							Assert.assertFalse("AST: position.file() is null for node:" + n, true);
						if (!position.isCompilerGenerated() && !position.file().startsWith(File.separator)) 
							Assert.assertFalse("AST: position.file() returned a string that was not in the original sources:" + n, true);
				 		
//						if (position.isCompilerGenerated()){
//							if (position.endOffset() - position.offset() != 0) // --- Non-zero extent
//								Assert.assertFalse("AST: position extent is non-zero", true);
//						}
						
						if (n instanceof TypeNode) {

                            TypeNode typeNode = (TypeNode) n;
                            Type type = typeNode.type();
                            if (type == null)
                                Assert.assertFalse("AST: typeNode.type() == null for node: " + n, true);
                            Assert.assertFalse("AST: ill type: " + type, illType(type));
                            List<Type> types = new ArrayList<Type>();
                            extractAllClassTypes(type, types);
                            for(Type t: types) {
                                Assert.assertFalse("AST: ill type: " + t, illType(t));
                            }

                        } else if (n instanceof Field) {

                            Field field = (Field) n;
                            Receiver rcvr = field.target();
                            if (rcvr == null)
                                Assert.assertFalse("AST: field.target() == null for node: " + n, true);
                            if (rcvr.type() == null)
                                Assert.assertFalse("AST: field.target().type() == null for node: " + n, true);
                            Assert.assertFalse("AST: ill type: " +rcvr.type(), illType(rcvr.type()));
                            if (field.type() == null)
                                Assert.assertFalse("AST: field.type() == null for node: " + n, true);
                            Assert.assertFalse("AST: ill type: " +field.type(), illType(field.type()));


                        } else if (n instanceof Call) {

                            Call call = (Call) n;
                            MethodInstance mi = call.methodInstance();
                            if (mi.container() == null)
                                Assert.assertFalse("AST: call.methodInstance.container() == null for node: " + n, true);
                            if (mi.returnType() == null)
                                Assert.assertFalse("AST: call.methodInstance.returnType() == null for node: " + n, true);
                            Assert.assertFalse("AST: ill type: " +mi.returnType(), illType(mi.returnType()));
                            if (mi.signature() == null)
                                Assert.assertFalse("AST: call.methodInstance.signature() == null for node: " + n, true);
                            MethodDef def = mi.def();
                            if (def == null)
                            	Assert.assertFalse("AST: call.methodInstance.def() == null for null: " + n, true);
                            if (def.container() == null)
                                Assert.assertFalse("AST: call.methodInstance.container() == null for node: " + n, true);
                            if (def.returnType() == null)
                                Assert.assertFalse("AST: call.methodInstance.returnType() == null for node: " + n, true);
                            if (def.returnType().get() == null)
                                Assert.assertFalse("AST: call.methodInstance.returnType().get() == null for node: " + n, true);
                            Assert.assertFalse("AST: ill type: " +def.returnType().get(), illType(def.returnType().get()));
                            if (def.signature() == null)
                                Assert.assertFalse("AST: call.methodInstance.signature() == null for node: " + n, true);
                            
                            
                            
                        } else if (n instanceof New) {

                            New nw = (New) n;
                            ConstructorInstance ci = nw.constructorInstance();
                            if (ci == null)
                                Assert.assertFalse("AST: nw.constructorInstance() == null for node: " + n, true);
                            if (ci.container() == null)
                                Assert.assertFalse("AST: nw.constructorInstance().container() == null for node: " + n, true);
                            Assert.assertFalse("AST: ill type: " +ci.container(), illType(ci.container()));

                        } else if (n instanceof ClassDecl) {

                            ClassDecl classDecl = (ClassDecl) n;
                            ClassDef classDef = classDecl.classDef();
                            if (classDef == null)
                                Assert.assertFalse("AST:  classDecl.classDef() == null for node: " + n, true);
                            if (classDef != null) {
                                List<ClassType> supers = new ArrayList<ClassType>();
                                superTypes(classDef.asType(), supers);
                                for(Type s: supers) {
                                    Assert.assertFalse("AST: ill type: " +s, illType(s));
                                }
                            }

                        } else if (n instanceof FieldDecl) {

                            FieldDecl fieldDecl = (FieldDecl) n;
                            if (fieldDecl.type() == null)
                                Assert.assertFalse("AST: fieldDecl.type() == null for node: " + n, true);
                            if (fieldDecl.type().type() == null)
                                Assert.assertFalse("AST: fieldDecl.type().type() == null for node: " + n, true);
                            Assert.assertFalse("AST: ill type: " +fieldDecl.type().type(), illType(fieldDecl.type().type()));

                        } else if (n instanceof ProcedureDecl) {

                            ProcedureDecl procedureDecl = (ProcedureDecl) n;
                            for (Formal formal : procedureDecl.formals()) {
                                if (formal.type() == null)
                                    Assert.assertFalse("AST: formal.type() == null for node: " + n, true);
                                if (formal.type().type() == null)
                                    Assert.assertFalse("AST: formal.type().type() == null for node: " + n, true);
                                Assert.assertFalse("AST: ill type: " +formal.type().type(), illType(formal.type().type()));
                            }
                            if (procedureDecl instanceof MethodDecl) {
                                MethodDecl method = (MethodDecl) procedureDecl;
                                if (method.returnType() == null)
                                    Assert.assertFalse("AST: method.returnType() == null for node: " + n, true);
                                if (method.returnType().type() == null)
                                    Assert.assertFalse("AST: method.returnType().type() == null for node: " + n, true);
                                Assert.assertFalse("AST: ill type: " +method.returnType().type(), illType(method.returnType().type()));
                            }

                        } else if (n instanceof AnnotationNode) {
                            AnnotationNode annot = (AnnotationNode) n;
                            if (annot.annotationType() == null)
                                Assert.assertFalse("AST: annot.annotationType() == null for node: " + n, true);
                            if (annot.annotationType().type() == null)
                                Assert.assertFalse("AST: annot.annotationType().type() == null for node: " + n, true);
                            Assert.assertFalse("AST: ill type: " +annot.annotationType().type(), illType(annot.annotationType().type()));

                        } else if (n instanceof Import) {

                            Import node = (Import) n;
                            if (node.name() == null)
                                Assert.assertFalse("AST: node.name() == null for node: " + n, true);
                            if (node.kind() == null)
                                Assert.assertFalse("AST: node.kind() == null for node: " + n, true);

                        }
                        return super.enterCall(n);
                    }
            }.begin());
        } catch(Exception e) {
            Assert.assertFalse("AST: " + e, true);
        }
    }

    private void extractAllClassTypes(Type type, List<Type> types) {
        if (!type.isClass())
            return;
        if (type instanceof ClosureType_c) {
             ClosureType_c closure = (ClosureType_c) type;
             for(Type formal: closure.argumentTypes()) {
                 extractAllClassTypes(formal, types);
             }
             extractAllClassTypes(closure.returnType(), types);
             return;
        }
        X10ClassType classType = (X10ClassType) type.toClass();
        if (!types.contains(classType))
            types.add(classType);

        if (classType.typeArguments() != null) {
            for (Type param : classType.typeArguments())
                extractAllClassTypes(param, types);
        }
    }

    private void superTypes(ClassType type, List<ClassType> types) {
        Type parentClass = type.superClass();
        if (parentClass != null) {
            X10ClassType classType = (X10ClassType) parentClass.toClass();
            if (!types.contains(classType)) {
                types.add(classType);
                superTypes(classType, types);
            }
        }
        for(Type inter: type.interfaces()) {
            X10ClassType interc = (X10ClassType) inter.toClass();
            if (!types.contains(interc)) {
                types.add(interc);
                superTypes(interc, types);
            }
        }
    }

    private boolean illType(Type type) {
        if (type != null && type.isReference()) {
            return getCandidates((ObjectType) type);
        }
        return false;
    }

    private boolean getCandidates(ObjectType container_type) {
        List<FieldInstance> fields= new ArrayList<FieldInstance>();
        List<MethodInstance> methods= new ArrayList<MethodInstance>();
        List<ClassType> classes= new ArrayList<ClassType>();

        getFieldCandidates(container_type, fields, "", true);
        getMethodCandidates(container_type, methods, "", true);
        getClassCandidates((Type) container_type, classes,"", true);

        for (FieldInstance field : fields) {
            if (field.name() == null)
                return true;
        }

        for (MethodInstance method : methods) {
            if (method.signature() == null)
                return true;
        }

        for (ClassType type : classes) {
            if (!type.isAnonymous()) {
                if (type.name() == null)
                    return true;
            }
        }
        return false;
    }

    private void getFieldCandidates(Type container_type, List<FieldInstance> fields, String prefix, boolean emptyPrefixMatches) {
        if (container_type == null)
            return;

        if (container_type instanceof ObjectType) {
            ObjectType oType = (ObjectType) container_type;

            filterFields(fields, oType.fields(), prefix, emptyPrefixMatches);

            for (int i = 0; i < oType.interfaces().size(); i++) {
                ObjectType interf = (ObjectType) oType.interfaces().get(i);
                filterFields(fields, interf.fields(), prefix, emptyPrefixMatches);
            }

            getFieldCandidates(oType.superClass(), fields, prefix, emptyPrefixMatches);
        }
    }

    private void getMethodCandidates(ObjectType container_type, List<MethodInstance> methods, String prefix, boolean emptyPrefixMatches) {
        if (container_type == null)
            return;

        filterMethods(methods, container_type.methods(), prefix, emptyPrefixMatches);

        getMethodCandidates((ObjectType)container_type.superClass(), methods, prefix, emptyPrefixMatches);
    }

    private void getClassCandidates(Type type, List<ClassType> classes, String prefix, boolean emptyPrefixMatches) {
        if (type == null)
            return;
        ClassType container_type= type.toClass();
        if (container_type == null)
            return;

        filterClasses(classes, container_type.memberClasses(), prefix, emptyPrefixMatches);

        for (int i= 0; i < container_type.interfaces().size(); i++) {
            ClassType interf= container_type.interfaces().get(i).toClass();
            filterClasses(classes, interf.memberClasses(), prefix, emptyPrefixMatches);
        }
        getClassCandidates(container_type.superClass(), classes, prefix, emptyPrefixMatches);
    }

    private void filterFields(List<FieldInstance> fields, List<FieldInstance> in_fields, String prefix, boolean emptyPrefixMatches) {
        for (FieldInstance f: in_fields) {
            String name= f.name().toString();
            if (emptyPrefixTest(emptyPrefixMatches, prefix) && name.startsWith(prefix))
                fields.add(f);

        }

    }

    private void filterMethods(List<MethodInstance> methods, List<MethodInstance> in_methods, String prefix, boolean emptyPrefixMatches) {
        for (MethodInstance m: in_methods) {
            String name= m.name().toString();
            if (emptyPrefixTest(emptyPrefixMatches, prefix) && name.startsWith(prefix))
                methods.add(m);
        }
    }

    private void filterClasses(List<ClassType> classes, List<ClassType> in_classes, String prefix, boolean emptyPrefixMatches) {
        for (ClassType c: in_classes) {
            String name= c.name().toString();
            if (emptyPrefixTest(emptyPrefixMatches, prefix) && name.startsWith(prefix)) {
                classes.add(c);
            }
        }
    }

    private boolean emptyPrefixTest(boolean emptyPrefixMatches, String prefix) {
        if (!emptyPrefixMatches)
            return !prefix.equals("");
        return true;
    }
    
    private boolean notSevereMode(){
    	return !System.getProperty("mode").equals(SEVERE_MODE);
    }
}
