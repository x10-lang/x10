package x10.refactorings.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.translator.X10CAstEntity;
import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.debug.Assertions;

public class WALAUtils {
    /**
     * @return the set of all *.x10 source files located in all folders on the source path of the given (Java-natured)
     *         project
     */
    public static Collection<String> allSources(IProject project) {
        IJavaProject javaProject = JavaCore.create(project);
        IWorkspaceRoot wsRoot = project.getWorkspace().getRoot();
        final Collection<String> result = new HashSet<String>();
        try {
            IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                    IPath entryPath = entry.getPath();
                    IResource entryRsrc = wsRoot.findMember(entryPath);
                    entryRsrc.accept(new IResourceVisitor() {
                        public boolean visit(IResource resource) throws CoreException {
                            if (resource instanceof IFile) {
                                IFile file = (IFile) resource;
                                if ("x10".equals(file.getFileExtension())) {
                                    result.add(file.getLocation().toPortableString());
                                }
                            }
                            return true;
                        }
                    });
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * extractNeededEntities returns a collection of the CAstEntities which have the specified kind and are scoped
     * entities of the given CAstEntity.
     */
    public static Collection<CAstEntity> extractNeededEntities(CAstEntity topEntity, int kind) {
        Collection<CAstEntity> neededEntities = new ArrayList<CAstEntity>(8);
        for (Iterator i = topEntity.getAllScopedEntities().values().iterator(); i.hasNext();) {
            Collection topEntityCol = (Collection) i.next();
            for (Object topEntityObj : topEntityCol) {
                CAstEntity cEntity = (CAstEntity) topEntityObj;
                if (cEntity.getKind() == kind)
                    neededEntities.add(cEntity);
            }
        }

        return neededEntities;
    }

    /*
     * extractProcEntities returns a collection of the ProcedureEntities defined in the top level classes of a given
     * CompilationUnitEntity.
     */

    public static Collection<CAstEntity> extractProcEntities(CAstEntity rootEntity) {
        Collection rootEntityChildren = rootEntity.getAllScopedEntities().values();
        Collection<CAstEntity> procEntities = new ArrayList<CAstEntity>(10);
        for (Object classEntityColObj : rootEntityChildren) {
            Collection classEntityCol = (Collection) classEntityColObj;
            for (Object classEntityObj : classEntityCol) {
                CAstEntity classEntity = (CAstEntity) classEntityObj;
                procEntities.addAll(extractNeededEntities(classEntity, CAstEntity.FUNCTION_ENTITY));
            }
        }
        return procEntities;
    }

    /*
     * extractAsyncEntites returns a collection of AsyncEntities which are found in the bodies of the provided
     * ProcedureEntities. This method is was created in order to analyze the bodies of AsyncEntities, which are not
     * found in the ASTs of the parent ProcedureEntities.
     */
    public static Collection<CAstEntity> extractAsyncEntities(Collection<CAstEntity> procEntities) {
        Collection<CAstEntity> asyncEntities = new ArrayList<CAstEntity>(procEntities.size());
        for (CAstEntity procEntity : procEntities) {
            asyncEntities.addAll(extractNeededEntities(procEntity, X10CAstEntity.ASYNC_BODY));
        }

        if (asyncEntities.size() != 0)
            asyncEntities.addAll(extractAsyncEntities(asyncEntities));
        return asyncEntities;
    }

    public static AnalysisScope createScope(Collection sources, List libs) throws IOException {
        JavaSourceAnalysisScope scope = new JavaSourceAnalysisScope();

        boolean foundLib = false;
        for (Iterator iter = libs.iterator(); iter.hasNext();) {
            String lib = (String) iter.next();

            File libFile = new File(lib);
            if (libFile.exists()) {
                foundLib = true;
                scope.addToScope(scope.getPrimordialLoader(), new JarFile(libFile));
            }
        }
        Assertions._assert(foundLib);

        for (Iterator iter = sources.iterator(); iter.hasNext();) {
            String srcFile = (String) iter.next();

            scope.addSourceFileToScope(scope.getSourceLoader(), new File(srcFile), ".");
        }
        return scope;
    }

    public static void dumpIR(CallGraph cg) throws IOException {
        // WarningSet warnings= new WarningSet();
        IClassHierarchy cha = cg.getClassHierarchy();
        IClassLoader sourceLoader = cha.getLoader(X10SourceLoaderImpl.X10SourceLoader);

        for (Iterator iter = sourceLoader.iterateAllClasses(); iter.hasNext(); ) {
            IClass clazz = (IClass) iter.next();

            System.out.println(clazz);
            if (clazz.isInterface())
                continue;

            for (Iterator iterator = clazz.getDeclaredMethods().iterator(); iterator.hasNext();) {
                IMethod m = (IMethod) iterator.next();
                if (m.isAbstract())
                    System.out.println(m);
                else {
                    Iterator nodeIter = cg.getNodes(m.getReference()).iterator();
                    if (!nodeIter.hasNext()) {
                        System.err.println("Source method " + m.getReference() + " not reachable?");
                        continue;
                    }
                    CGNode node = (CGNode) nodeIter.next();
                    System.out.println(node.getIR(/* , warnings */));
                }
            }
        }
    }

    // public static Iterator getIRIterator(final AnalysisOptions options, ClassLoaderFactory loaders,
    // final ClassHierarchy cha) throws IOException {
    // final AnalysisScope scope = options.getAnalysisScope();
    // final ClassLoaderReference sourceLoaderRef = scope
    // .getLoader(EclipseProjectPath.SOURCE);
    // final JavaSourceLoaderImpl sourceLoader = (JavaSourceLoaderImpl) loaders
    // .getLoader(sourceLoaderRef, cha, scope);
    //
    // return new Iterator() {
    // Iterator clazzIter = sourceLoader.iterateAllClasses();
    //
    // Iterator methIter;
    //
    // {
    // if (clazzIter.hasNext())
    // methIter = ((IClass) clazzIter.next()).getDeclaredMethods()
    // .iterator();
    // else
    // methIter = null;
    // }
    //
    // public boolean hasNext() {
    // if (methIter.hasNext() || clazzIter.hasNext())
    // return true;
    // return false;
    // }
    //
    // public Object next() {
    // while (!methIter.hasNext())
    // if (clazzIter.hasNext())
    // methIter = ((IClass) clazzIter.next())
    // .getDeclaredMethods().iterator();
    // else
    // throw new NoSuchElementException();
    // IMethod m = (IMethod) methIter.next();
    // if (m.isAbstract())
    // return next();
    // return options.getSSACache().findOrCreateIR(m,
    // Everywhere.EVERYWHERE, /*cha,*/
    // SSAOptions.defaultOptions()/*, warnings*/);
    // }
    //
    // public void remove() {
    // throw new UnsupportedOperationException();
    // }
    // };
    // }

    public static String IFilePathtoCAstPath(String path) {
        String retval = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + path;
        return retval.replace('/', '\\');
    }
}
