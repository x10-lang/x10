import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
public class Ejc extends Javac {
    public static final String EJC_COMPILER = "org.eclipse.jdt.core.JDTCompilerAdapter";
    public Ejc() {
        super();
        //System.err.println("Creating ejc task: "+getClass().getClassLoader().getResource(EJC_COMPILER.replaceAll(".","/")+".class"));
        //try {
        //    System.err.println("Creating ejc task: "+getClass().forName(EJC_COMPILER));
        //} catch (ClassNotFoundException e) { }
    }
    public String getCompiler() { return EJC_COMPILER; }
    // Ugly ugly hack: because ant is broken, replicate the contents of Javac.compile()
    protected void compile() {
        //try {
        //    System.err.println("Compiling: "+Class.forName(EJC_COMPILER));
        //} catch (ClassNotFoundException e) {
        //    System.err.println(EJC_COMPILER+" not found");
        //}
        String compilerImpl = getCompiler();

        if (compileList.length > 0) {
            log("Compiling " + compileList.length + " source file"
                + (compileList.length == 1 ? "" : "s")
                + (getDestdir() != null ? " to " + getDestdir() : ""));

            if (listFiles) {
                for (int i = 0; i < compileList.length; i++) {
                  String filename = compileList[i].getAbsolutePath();
                  log(filename);
                }
            }

            CompilerAdapter adapter = null;
            try {
                adapter = (CompilerAdapter) getClass().forName(compilerImpl).newInstance();
            } catch (ClassNotFoundException e) {
                System.err.println(EJC_COMPILER+" not found");
                throw new BuildException(e, getLocation());
            } catch (IllegalAccessException e) {
            } catch (InstantiationException e) {
            }

            // now we need to populate the compiler adapter
            adapter.setJavac(this);

            // finally, lets execute the compiler!!
            if (adapter.execute()) {
                //FROM: ANT_17_BRANCH
                // Success - check
                List updateDirList = getUpdateDirList();
                if (updateDirList != null) {
                    for (Iterator i = updateDirList.iterator(); i.hasNext();) {
                        File file = (File) i.next();
                        file.setLastModified(System.currentTimeMillis());
                    }
                }
                //FROM: HEAD
                // Success
                try {
                    invokeGenerateMissingPackageInfoClasses();
                } catch (IOException x) {
                    // Should this be made a nonfatal warning?
                    throw new BuildException(x, getLocation());
                }
            } else {
                //FROM: ANT_17_BRANCH
                // Fail path
                setTaskSuccess(false);
                String errorProperty = getErrorProperty();
                if (errorProperty != null) {
                    getProject().setNewProperty(
                        errorProperty, "true");
                }
                if (failOnError) {
                    throw new BuildException(FAIL_MSG, getLocation());
                } else {
                    log(FAIL_MSG, Project.MSG_ERR);
                }
            }
        }
    }
    // More stupidity: because errorProperty is private and not gettable, must use reflection
    public String getErrorProperty() {
        try {
            java.lang.reflect.Field eP = getClass().getSuperclass().getDeclaredField("errorProperty");
            eP.setAccessible(true);
            return (String) eP.get(this);
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) { }
        return null;
    }
    // Ditto for updateDirList
    public List getUpdateDirList() {
        try {
            java.lang.reflect.Field uDL = getClass().getSuperclass().getDeclaredField("updateDirList");
            uDL.setAccessible(true);
            return (List) uDL.get(this);
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) { }
        return null;
    }
    // Even more stupidity: because errorProperty is private and not settable, must use reflection
    public void setTaskSuccess(boolean value) {
        try {
            java.lang.reflect.Field tS = getClass().getSuperclass().getDeclaredField("taskSuccess");
            tS.setAccessible(true);
            tS.setBoolean(this, value);
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) { }
    }
    // Finally: because generateMissingPackageInfoClasses is private, must use reflection
    public void invokeGenerateMissingPackageInfoClasses() throws IOException {
        try {
            java.lang.reflect.Method gMPIC = getClass().getSuperclass().getDeclaredMethod("generateMissingPackageInfoClasses");
            gMPIC.setAccessible(true);
            gMPIC.invoke(this, new Object[]{});
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable c = e.getCause();
            if (c instanceof IOException) throw (IOException)c;
            else if (c instanceof RuntimeException) throw (RuntimeException)c;
            else if (c instanceof Error) throw (Error)c;
            else throw new Error(c);
        } catch (IllegalAccessException e) {
        } catch (NoSuchMethodException e) { }
    }
    // For the same reasons, need to replicate FAIL_MSG
    protected static final String FAIL_MSG =
        "Compile failed; see the compiler error output for details.";
}
