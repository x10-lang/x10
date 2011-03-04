/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lpg.runtime.IMessageHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.osgi.util.NLS;

import polyglot.ast.Node;
import polyglot.frontend.Compiler;
import polyglot.frontend.Source;
import polyglot.types.ClassType;
import polyglot.types.MethodDef;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.X10CompilerOptions;
import x10.ast.X10MethodDecl;
import x10.types.X10ClassType;
import x10dt.core.builder.StreamSource;
import x10dt.core.utils.X10BundleUtils;
import x10dt.ui.Messages;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.parser.ExtensionInfo;

/**
 * Utility methods around X10 compiler.
 * 
 * @author egeay
 */
public final class X10Utils {
  
  /**
   * Finds and collects all X10 main types that are present under the Java element provided.
   * 
   * @param x10Types The container that will contain the resulting X10 main types.
   * @param javaElement The Java element that defines the scope of the search.
   * @param monitor The progress monitor to use in order to report progress or cancel the operation.
   * @throws CoreException Occurs if we could browse the resources under the Java element provided or if we could not
   * get the class path entries for the project class path containing the Java element.
   * @throws InterruptedException Occurs if the search operation got canceled.
   */
  public static void collectX10MainTypes(final Collection<ClassType> x10Types, final ISourceEntity entity,
                                         final IProgressMonitor monitor) throws CoreException, InterruptedException {
    monitor.beginTask(null, 10);
    
    final Collection<Source> x10Files = new LinkedList<Source>();
    entity.getResource().accept(new X10FileResourceVisitor(x10Files));
    if (monitor.isCanceled()) {
      throw new InterruptedException();
    }
    
    ExtensionInfo extInfo = null;
	try {
		final ISourceProject javaProject = ModelFactory.getProject(entity.getResource().getProject());
		final Set<IPath> entries = collectPathEntries(javaProject);
		final StringBuilder cpBuilder = new StringBuilder();
		int i = -1;
		for (final IPath pathEntry : entries) {
		  if (++i > 0) {
		    cpBuilder.append(File.pathSeparatorChar);
		  }
		  cpBuilder.append(pathEntry.toOSString());
		}
		
		final List<File> sourcePath = new ArrayList<File>();
		for (final IPath pathEntry : entries) {
		  final String entry = pathEntry.toOSString();
		  if (entry.contains(X10BundleUtils.X10_RUNTIME_BUNDLE_ID) || entry.contains(javaProject.getName())) {
		    sourcePath.add(pathEntry.toFile());
		  }
		}
		
		extInfo = new ExtensionInfo(null /* monitor */, new ShallowMessageHander());
		final X10CompilerOptions compilerOptions = (X10CompilerOptions) extInfo.getOptions();
		compilerOptions.assertions = true;
		compilerOptions.serialize_type_info = false;
		compilerOptions.compile_command_line_only = true;
		compilerOptions.post_compiler = null;
		compilerOptions.classpath = cpBuilder.toString();
		compilerOptions.output_classpath = compilerOptions.classpath;
		compilerOptions.source_path = sourcePath;
		
		extInfo.setInterestingSources(x10Files);
		
		if (monitor.isCanceled()) {
		  throw new InterruptedException();
		}
	} catch (ModelException e) {
		throw ModelFactory.createCoreException(e);
	}
    
    final Compiler compiler = new Compiler(extInfo, new ShallowErrorQueue());
    try {
      monitor.subTask(Messages.XU_ParsingX10Files);
      // Unfortunately we can't pass the monitor to the compiler :-/
      compiler.compile(x10Files);
      monitor.worked(7);
      monitor.subTask(Messages.XU_SearchForMainTypes);
      final NodeVisitor nodeVisitor = new X10MainTypeNodeVisitor(x10Types);
      for (final Source source : x10Files) {
        if (monitor.isCanceled()) {
          throw new InterruptedException();
        }
        final Node astRootNode = extInfo.getASTFor(source);
        if (astRootNode != null) {
          astRootNode.visit(nodeVisitor);
        }
      }
    } finally {
      monitor.worked(3);
    }
  }
  
  // --- Private code
  
  private X10Utils() {}
  
  private static Set<IPath> collectPathEntries(final ISourceProject project) throws ModelException {
    final Set<IPath> container = new HashSet<IPath>();
    final IWorkspaceRoot root = project.getResource().getWorkspace().getRoot();
    for (final IPathEntry cpEntry : project.getResolvedBuildPath(LanguageRegistry.findLanguage("X10"), true)) {
      collectCpEntries(container, cpEntry, root);
    }
    return container;
  }
  
  private static <T> void collectCpEntries(final Set<IPath> container, final IPathEntry cpEntry, 
                                           final IWorkspaceRoot root) throws ModelException {
    switch (cpEntry.getEntryType()) {
      case SOURCE_FOLDER:
        if (cpEntry.getRawPath().isRoot()) {
          container.add(cpEntry.getRawPath());
        } else {
          container.add(root.getLocation().append(cpEntry.getRawPath()));
        }
        break;
        
      case ARCHIVE:
        container.add(cpEntry.getRawPath());
        break;
      
      case PROJECT:
        final IResource resource = root.findMember(cpEntry.getRawPath());
        if (resource != null) {
          final ISourceProject refProject = ModelFactory.getProject((IProject) resource);
          for (final IPathEntry newCPEntry : refProject.getBuildPath(LanguageRegistry.findLanguage("X10"))) {
            collectCpEntries(container, newCPEntry, root);
          }
        }
        break;
    }
  }
  
  // --- Private classes
  
  private static final class ShallowMessageHander implements IMessageHandler {
    
    // --- Interface methods implementation
    
    public void handleMessage(final int errorCode, final int[] msgLocation, final int[] errorLocation, 
                              final String filename, final String[] errorInfo) {
      // Do nothing
    }
    
  }
  
  private static final class X10FileResourceVisitor implements IResourceVisitor {
    
    X10FileResourceVisitor(final Collection<Source> x10Files) {
      this.fX10Files = x10Files;
    }
    
    // --- Interface methods implementation
    
    public boolean visit(final IResource resource) throws CoreException {
      if (resource.getType() == IResource.FILE) {
        final IFile file = (IFile) resource;
        if (X10_EXT.equalsIgnoreCase(file.getFileExtension())) {
          try {
            this.fX10Files.add(new StreamSource(file.getContents(), file.getLocation().toOSString()));
          } catch (IOException except) {
            throw new CoreException(new Status(IStatus.ERROR, X10DTUIPlugin.PLUGIN_ID, 
                                               NLS.bind(Messages.XU_X10FileReadingError, file.getLocation().toString()), 
                                               except));
          }
        }
      }
      return true;
    }
    
    // --- Fields
    
    private final Collection<Source> fX10Files;
    
  }
  
  private static final class ShallowErrorQueue implements ErrorQueue {

    // --- Interface methods implementation
    
    public void enqueue(final int type, final String message) {
    }

    public void enqueue(final int type, final String message, final Position position) {
    }

    public void enqueue(final ErrorInfo errorInfo) {
    }

    public int errorCount() {
      return 0;
    }

    public void flush() {
    }

    public boolean hasErrors() {
      return false;
    }
    
  }
  
  private static final class X10MainTypeNodeVisitor extends NodeVisitor {
    
    X10MainTypeNodeVisitor(final Collection<ClassType> x10Types) {
      this.fX10Types = x10Types;
    }
    
    // --- Overridden methods
    
    public Node override(final Node node) {
      if (node instanceof X10MethodDecl) {
        final X10MethodDecl methodDecl = (X10MethodDecl) node;
        final MethodDef methodDef = methodDecl.methodDef();
        if (methodDef == null) {
          return null;
        }
        final X10ClassType classType = (X10ClassType) methodDef.asInstance().container();
        final TypeSystem typeSystem = (TypeSystem) classType.typeSystem();
        if (methodDecl.name().toString().equals(MAIN_METHOD_NAME) && methodDecl.flags().flags().isPublic() &&
            methodDecl.flags().flags().isStatic() && methodDecl.returnType().type().isVoid() &&
            (methodDecl.formals().size() == 1) && 
            typeSystem.isSubtype(methodDecl.formals().get(0).type().type(), 
                                 typeSystem.Array(typeSystem.String()), typeSystem.emptyContext())) {
          this.fX10Types.add(classType);
        }
        // We don't search for a "main method" within methods.
        return node;
      } else {
        return null;
      }
    }
    
    // --- Fields
    
    private final Collection<ClassType> fX10Types;
    
  }
  
  // --- Fields
  
  private static final String X10_EXT = "x10"; //$NON-NLS-1$
  
  private static final String MAIN_METHOD_NAME = "main"; //$NON-NLS-1$

}
