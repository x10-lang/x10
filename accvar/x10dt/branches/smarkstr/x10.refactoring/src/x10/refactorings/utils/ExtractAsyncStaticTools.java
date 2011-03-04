package x10.refactorings.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.osgi.framework.Bundle;

import polyglot.ast.ArrayAccess;
import polyglot.ast.NamedVariable;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.x10.translator.X10CAstEntity;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.debug.Assertions;

public class ExtractAsyncStaticTools {

	public static String javaHomePath = System.getProperty("java.home");

	public static String eclipseHomePath = System.getProperty("user.dir");

	public static String testSrcPath = "." + File.separator + "testSrc";

	public static IPath getLanguageRuntimePath(IJavaProject javaProject) {
		IClasspathEntry[] entries;
		try {
			entries = javaProject.getResolvedClasspath(true);
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i];
				switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_LIBRARY: {
					if (entry.getPath().toPortableString().contains(
							"x10.runtime")) {
						return entry.getPath();
					}
					break;
				}
				}
			}
		} catch (JavaModelException e) {
			MessageDialog.openError(null, "Extract Async error",
					"Cannot resolve project's classpath!");
			e.printStackTrace();
		}
		return getDefaultX10RuntimePath();
	}

	private static IPath getDefaultX10RuntimePath() {
		Bundle x10RuntimeBundle = Platform.getBundle("x10.runtime");
		String bundleVersion = (String) x10RuntimeBundle.getHeaders().get(
				"Bundle-Version");
		IPath x10RuntimePath = new Path("/plugins/x10.runtime_" + bundleVersion
				+ ".jar");

		return new Path(ExtractAsyncStaticTools.eclipseHomePath
				+ x10RuntimePath);
	}

	public static Collection<String> singleTestSrc(IFile grammarFile) {
		IWorkspace myWorkspace = ResourcesPlugin.getWorkspace();
		return Collections.singletonList(myWorkspace.getRoot().getLocation()
				.toString()
				+ grammarFile.getFullPath().toString());
	}

	/*
	 * extractNeededEntities returns a collection of the CAstEntities which have
	 * the specified kind and are scoped entities of the given CAstEntity.
	 */

	public static Collection<CAstEntity> extractNeededEntities(
			CAstEntity topEntity, int kind) {
		Collection<CAstEntity> neededEntities = new ArrayList<CAstEntity>(8);
		for (Iterator i = topEntity.getAllScopedEntities().values().iterator(); i
				.hasNext();) {
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
	 * extractProcEntities returns a collection of the ProcedureEntities defined
	 * in the top level classes of a given CompilationUnitEntity.
	 */

	public static Collection<CAstEntity> extractProcEntities(
			CAstEntity rootEntity) {
		Collection rootEntityChildren = rootEntity.getAllScopedEntities()
				.values();
		Collection<CAstEntity> procEntities = new ArrayList<CAstEntity>(10);
		for (Object classEntityColObj : rootEntityChildren) {
			Collection classEntityCol = (Collection) classEntityColObj;
			for (Object classEntityObj : classEntityCol) {
				CAstEntity classEntity = (CAstEntity) classEntityObj;
				procEntities.addAll(extractNeededEntities(classEntity,
						CAstEntity.FUNCTION_ENTITY));
			}
		}
		return procEntities;
	}

	/*
	 * extractAsyncEntites returns a collection of AsyncEntities which are found
	 * in the bodies of the provided ProcedureEntities. This method is was
	 * created in order to analyze the bodies of AsyncEntities, which are not
	 * found in the ASTs of the parent ProcedureEntities.
	 */
	public static Collection<CAstEntity> extractAsyncEntities(
			Collection<CAstEntity> procEntities) {
		Collection<CAstEntity> asyncEntities = new ArrayList<CAstEntity>(
				procEntities.size());
		for (CAstEntity procEntity : procEntities) {
			asyncEntities.addAll(extractNeededEntities(procEntity,
					X10CAstEntity.ASYNC_BODY));
		}

		if (asyncEntities.size() != 0)
			asyncEntities.addAll(extractAsyncEntities(asyncEntities));
		return asyncEntities;
	}

	public static AnalysisScope createScope(Collection sources, List libs)
			throws IOException {
		JavaSourceAnalysisScope scope = new JavaSourceAnalysisScope();

		boolean foundLib = false;
		for (Iterator iter = libs.iterator(); iter.hasNext();) {
			String lib = (String) iter.next();

			File libFile = new File(lib);
			if (libFile.exists()) {
				foundLib = true;
				scope.addToScope(scope.getPrimordialLoader(), new JarFile(
						libFile));
			}
		}
		Assertions._assert(foundLib);

		for (Iterator iter = sources.iterator(); iter.hasNext();) {
			String srcFile = (String) iter.next();

			scope.addSourceFileToScope(scope.getSourceLoader(), new File(
					srcFile), ".");
		}
		return scope;
	}

	public static void dumpIR(CallGraph cg) throws IOException {
		// WarningSet warnings= new WarningSet();
		IClassHierarchy cha = cg.getClassHierarchy();
		IClassLoader sourceLoader = cha
				.getLoader(JavaSourceAnalysisScope.SOURCE);
		for (Iterator iter = sourceLoader.iterateAllClasses(); iter.hasNext();) {
			IClass clazz = (IClass) iter.next();

			System.out.println(clazz);
			if (clazz.isInterface())
				continue;

			for (Iterator iterator = clazz.getDeclaredMethods().iterator(); iterator
					.hasNext();) {
				IMethod m = (IMethod) iterator.next();
				if (m.isAbstract())
					System.out.println(m);
				else {
					Iterator nodeIter = cg.getNodes(m.getReference())
							.iterator();
					if (!nodeIter.hasNext()) {
						System.err.println("Source method " + m.getReference()
								+ " not reachable?");
						continue;
					}
					CGNode node = (CGNode) nodeIter.next();
					System.out.println(node.getIR(/* , warnings */));
				}
			}
		}
	}

	// public static Iterator getIRIterator(final AnalysisOptions options,
	// ClassLoaderFactory loaders,
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

	/**
	 * Extracts the base array name from an array access. Will not handle method
	 * invocations or other expression which result in arrays.
	 * 
	 * @param v
	 *            - The array access or an array variable
	 * @return - the base array name or null
	 */
	public static NamedVariable extractArrayName(Variable v) {
		if (v instanceof NamedVariable)
			return (NamedVariable) v;
		if (v instanceof ArrayAccess)
			return extractArrayName((Variable) ((ArrayAccess) v).array());
		if (v instanceof X10ArrayAccess)
			return extractArrayName((Variable) ((X10ArrayAccess) v).array());
		if (v instanceof X10ArrayAccess1)
			return extractArrayName((Variable) ((X10ArrayAccess1) v).array());
		return null;
	}

	public static String IFilePathtoCAstPath(String path) {
		String retval = ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toString()
				+ path;
		return retval.replace(File.separatorChar, '/');
	}

	/**
	 * A cheap hack that hardcodes the path to the exclusions file. Will be
	 * updated to a legitimate project property in the future.
	 * 
	 * @return path to exclusions file
	 */
	public static String cheapHack() {
		return "/Users/sm053/Documents/refactoring-workspace/com.ibm.wala.core.tests/dat/Java60RegressionExclusions.txt";
	}

	/**
	 * Does a functional programming style map from one collection to another
	 * (the collection type and ordering is not preserved!).
	 * 
	 * @param <S>
	 *            The original type of elements in the collection
	 * @param <T>
	 *            The type of the elements after map is applied
	 * @param orig
	 *            The original collection
	 * @param mapFun
	 *            The function to map to the collection
	 * @return A collection of objects generated via mapping the provided
	 *         function onto the original collection
	 */
	public static <S, T> Collection<T> map(Collection<S> orig,
			Function<S, T> mapFun) {
		HashSet<T> dest = new HashSet<T>();
		for (S x : orig) {
			dest.add(mapFun.eval(x));
		}
		return dest;
	}
}