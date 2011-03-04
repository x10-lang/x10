/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    @author Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*    @author pcharles@us.ibm.com
*******************************************************************************/
package x10dt.ui.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lpg.runtime.Monitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.editor.quickfix.IAnnotation;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.utils.LoggingUtils;

import polyglot.ast.SourceFile;
import polyglot.frontend.Compiler;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.UsageError;
import polyglot.util.AbstractErrorQueue;
import polyglot.util.CodedErrorInfo;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.X10CompilerOptions;
import x10.parser.X10Lexer;
import x10.parser.X10SemanticRules;
import x10dt.core.X10DTCorePlugin;
import x10dt.core.builder.BuildPathUtils;
import x10dt.core.preferences.generated.X10Constants;
import x10dt.core.utils.CompilerOptionsFactory;
import x10dt.core.utils.X10BundleUtils;
import x10dt.ui.X10DTUIPlugin;

public class CompilerDelegate {
	private class EditorErrorQueue extends AbstractErrorQueue {
		private final IPath filePath;
		private final IMessageHandler handler;

		private EditorErrorQueue(int limit, String name, IPath filePath, IMessageHandler handler) {
			super(limit, name);
			this.filePath = filePath;
			this.handler = handler;
			
		}

		protected void displayError(ErrorInfo error) {
			if (isValidationMsg(error)) {
				if (fViolationHandler != null) {
					fViolationHandler.handleViolation(error);
				}
			} else {
//				System.out.println(error.getMessage());
				if (BuildPathUtils.isExcluded(filePath, fX10Project, language)) {
					return;
				}
				
				Map<String, Object> attributes = getAttributes(error);
				Position pos = error.getPosition();
				if (pos != null) {
					IPath errorPath = new Path(pos.file());
					if (filePath.equals(errorPath)) {
						handler.handleSimpleMessage(error.getMessage(),
													pos.offset(), pos.endOffset(), pos.column(),
													pos.endColumn(), pos.line(), pos.endLine(),
													attributes);
					}
				} else {
					handler.handleSimpleMessage(error.getMessage(), 0, 0, 1, 1, 1, 1, attributes);
				}
			}
		}
	}

	private x10dt.ui.parser.ExtensionInfo fExtInfo;

    private final ISourceProject fX10Project;

    private final ParseController.InvariantViolationHandler fViolationHandler;

    private final IPath fFilePath;
    
    private Language language;

    CompilerDelegate(Monitor monitor, final IMessageHandler handler, final IProject project, final IPath filePath, ParseController.InvariantViolationHandler violationHandler) throws CoreException {
        this.fX10Project= (project != null) ? ModelFactory.getProject(project) : null;
        this.fFilePath= filePath;
        language = LanguageRegistry.findLanguage("X10");
        fViolationHandler= violationHandler;

        IPreferencesService prefSvc= new PreferencesService(project, X10DTCorePlugin.kLanguageName);
        boolean perfMode= prefSvc.getBooleanPreference(X10Constants.P_EDITORPERFORMANCEMODE);

        if (perfMode) {
        	fExtInfo = new x10dt.ui.parser.ParseExtensionInfo(monitor, new MessageHandlerAdapterFilter(handler, filePath, fX10Project));
        } else { //The project is either null, or it is not null and has X10 nature
        	fExtInfo= new x10dt.ui.parser.ExtensionInfo(monitor, new MessageHandlerAdapterFilter(handler, filePath, fX10Project));
        }

        buildOptions(fExtInfo);

		ErrorQueue eq = new EditorErrorQueue(1000000, fExtInfo.compilerName(), filePath, handler);
        new Compiler(fExtInfo, eq); // This also stores the compiler in fExtInfo
    }
    
	private boolean isX10Project() {
		try {
			return fX10Project.getRawProject().hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID) ||
			       fX10Project.getRawProject().hasNature(X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID);
		} catch (CoreException e) {
			X10DTUIPlugin.log(e);
			return false;
		}
	}

	protected Map<String, Object> getAttributes(ErrorInfo errorInfo) {
		Map<String, Object> map = null;
		if (errorInfo instanceof CodedErrorInfo) {
			map = ((CodedErrorInfo) errorInfo).getAttributes();
		}

		if (map == null) {
			map = new HashMap<String, Object>();
		}

		if (!map.containsKey(IMessageHandler.SEVERITY_KEY)) {
			if (errorInfo.getErrorKind() == ErrorInfo.WARNING) {
				map.put(IMessageHandler.SEVERITY_KEY, IAnnotation.WARNING);
			}

			else {
				map.put(IMessageHandler.SEVERITY_KEY, IAnnotation.ERROR);
			}
		}

		return map;
	}

    protected boolean isValidationMsg(ErrorInfo error) {
    	return (error.getErrorKind() == ErrorInfo.INVARIANT_VIOLATION_KIND);
    }

    public ExtensionInfo getExtInfo() { return fExtInfo; }

    public X10Lexer getLexerFor(Source src) { return fExtInfo.getLexerFor(src); }
    public X10SemanticRules getParserFor(Source src) { return fExtInfo.getParserFor(src); }
    public SourceFile getASTFor(Source src) { return (SourceFile) fExtInfo.getASTFor(src); }
    public Job getJobFor(Source src) { return fExtInfo.getJobFor(src); }

    public boolean compile(Collection<Source> sources) {
        if (fViolationHandler != null) {
        	fViolationHandler.clear();
        }

        fExtInfo.setInterestingSources(sources);
    	return fExtInfo.compiler().compile(sources);
    }

    private static final Pattern PKG_DECL_PATTERN= Pattern.compile("package[ \t]+([a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*)[ \t]*;");

    /**
     * Attempts to heuristically determine the path of the "package root" for X10
     * source files living outside the workspace, or within a non-X10-natured project.
     * @return
     */
    private IPath determinePkgRootPath() {
    	FileReader fileReader= null;
    	try {
    		String filePathStr = fFilePath.toOSString();
			File file= new File(filePathStr);

			if (file.exists()) {
    			fileReader= new FileReader(file);
    			char[] buf= new char[4096]; // Assume package decl lies within the first 4096 bytes.
    			int len= fileReader.read(buf, 0, 4096);
    			String bufStr= new String(buf, 0, len);
    			Matcher pkgMatcher= PKG_DECL_PATTERN.matcher(bufStr);

    			if (pkgMatcher.find()) {
    				String pkgName= pkgMatcher.group(1);
    				String pkgFolder= pkgName.replaceAll("\\.", "/");
    				String folderPathStr = fFilePath.removeLastSegments(1).toPortableString();

    				if (folderPathStr.endsWith(pkgFolder)) {
    					IPath pkgRootPath= new Path(folderPathStr.substring(0, folderPathStr.length() - pkgFolder.length()));

    					return pkgRootPath;
    				}
    			} else {
    				return fFilePath.removeLastSegments(1);
    			}
    		}
    	} catch (Exception e) {
    		X10DTUIPlugin.log(e);
    	} finally {
    		if (fileReader != null) {
    			try {
					fileReader.close();
				} catch (IOException e) {
				}
    		}
    	}
    	return null;
    }

    /**
     * @return a list of all project-relative CPE_SOURCE-type classpath entries.
     * @throws ModelException
     */
    private List<IPath> getProjectSrcPath() throws CoreException {
        List<IPath> srcPath= new ArrayList<IPath>();

        // Produce a search path heuristically for files living outside the workspace,
        // and for workspace files living in non-X10-natured projects.
        if (fX10Project == null || !isX10Project()) {
        	IPath pkgRootPath= determinePkgRootPath();

        	if (pkgRootPath != null) {
        		if (fX10Project != null && fX10Project.getRawProject().getName().equals("x10.runtime")) {
        			// If the containing project happens to be x10.runtime,
        			// don't add the runtime bound into the X10DT to the search path
        			return Arrays.asList(pkgRootPath);
        		} else {
                    return Arrays.asList(pkgRootPath, new Path(getRuntimePath()));
        		}
        	} else {
                return Arrays.asList((IPath) new Path(getRuntimePath()));
        	}
        }
		try
		{
			List<IPathEntry> classPath= fX10Project.getResolvedBuildPath(language, true);
			for(IPathEntry e : classPath) {
			    if (e.getEntryType() == PathEntryType.SOURCE_FOLDER) {
			        srcPath.add(e.getRawPath());
			    } else if (e.getEntryType() == PathEntryType.PROJECT) {
			        //PORT1.7 Compiler needs to see X10 source for all referenced compilation units,
			        // so add source path entries of referenced projects to this project's sourcepath.
			        // Assume that goal dependencies are such that Polyglot will not be compelled to
			        // compile referenced X10 source down to Java source (causing duplication; see below).
			        //
			        // RMF 6/4/2008 - Don't add referenced projects to the source path:
			        // 1) doing so should be unnecessary, since the classpath will include
			        //    the project, and the class files should satisfy all references,
			        // 2) doing so will cause Polyglot to compile the source files found in
			        //    the other project to Java source files located in the *referencing*
			        //    project, causing duplication, which is not what we want.
			        //
			        IProject refProject= ResourcesPlugin.getWorkspace().getRoot().getProject(e.getRawPath().toPortableString());
			        ISourceProject refJavaProject= ModelFactory.getProject(refProject);
			        List<IPathEntry> refJavaCPEntries= refJavaProject.getResolvedBuildPath(language, true);
			        for(IPathEntry refJavaCPEntry:  refJavaCPEntries) {
			            if (refJavaCPEntry.getEntryType() == PathEntryType.SOURCE_FOLDER) {
			                srcPath.add(refJavaCPEntry.getRawPath());
			            }
			        }
			    } else if (e.getEntryType() == PathEntryType.ARCHIVE) {
			        // PORT1.7 Add the X10 runtime jar to the source path, since the compiler
			        // needs to see the X10 source for the user-visible runtime classes (like
			        // x10.lang.Region) to get the extra type information (for deptypes) that
			        // can't be stored in Java class files, and for now, these source files
			        // actually live in the X10 runtime jar.
			        IPath path= e.getRawPath();
			        if (path.toPortableString().contains(X10BundleUtils.X10_RUNTIME_BUNDLE_ID)) {
			            srcPath.add(path);
			        }
			    }
			}
		}
		catch(ModelException e)
		{
			LoggingUtils.log(e);
		}
		if (srcPath.size() == 0)
		    srcPath.add(fX10Project.getRawProject().getLocation());

        return srcPath;
    }

    private String pathListToPathString(List<IPath> pathList) {
        StringBuffer buff= new StringBuffer();
        IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();

        for(Iterator<IPath> iter= pathList.iterator(); iter.hasNext(); ) {
            IPath path= iter.next();
            IProject projectRef= wsRoot.getProject(path.segment(0));

            if (projectRef != null && projectRef.exists()) {
                // This is a workspace-relative path, but the project may not actually
                // live inside the workspace, so use its actual location as the prefix
                // for the rest of the specified path.
                buff.append(projectRef.getLocation().append(path.removeFirstSegments(1)).toOSString());
            } else if (fX10Project != null && fX10Project.getRawProject().exists(path)) {
                buff.append(fX10Project.getRawProject().getLocation().append(path).toOSString());
            } else {
                buff.append(path.toOSString());
            }
            if (iter.hasNext())
                buff.append(File.pathSeparatorChar);
        }
        return buff.toString();
    }

    private void buildOptions(ExtensionInfo extInfo) {
        X10CompilerOptions opts = extInfo.getOptions();

        try {
            List<IPath> projectSrcLoc = getProjectSrcPath();
            String projectSrcPath = pathListToPathString(projectSrcLoc);
            opts.x10_config.CHECK_INVARIANTS= (fViolationHandler != null);
            opts.parseCommandLine(new String[] { "-c", "-commandlineonly",
                    "-cp", buildClassPathSpec(), "-sourcepath", projectSrcPath
            }, new HashSet<String>());
            final IPreferencesService prefService = X10DTCorePlugin.getInstance().getPreferencesService();
            CompilerOptionsFactory.setOptionsNoCodeGen(prefService, opts);
        } catch (UsageError e) {
            if (!e.getMessage().equals("must specify at least one source file")) {
                X10DTUIPlugin.getInstance().writeErrorMsg(e.getMessage());
            }
        } catch (CoreException e) {
            X10DTUIPlugin.getInstance().writeErrorMsg("Unable to obtain resolved class path: " + e.getMessage());
        }
        // X10UIPlugin.getInstance().maybeWriteInfoMsg("Source path = " + opts.source_path);
        // X10UIPlugin.getInstance().maybeWriteInfoMsg("Class path = " + opts.classpath);
        // System.out.println("Source path = " + opts.source_path);
        // System.out.println("Class path = " + opts.classpath);
    }

    private String buildClassPathSpec() {
        StringBuffer buff= new StringBuffer();
        boolean hasRuntime= false;
        boolean runtimeValid= false;

        if (fX10Project == null) {
            return getRuntimePath();
        }
        if (!isX10Project()) {
        	return ".";
        }

        try {
	        List<IPathEntry> classPath= Collections.emptyList();
	        if(fX10Project != null)
	        	classPath = fX10Project.getResolvedBuildPath(language, true);
	
	        for(int i= 0; i < classPath.size(); i++) {
	            IPathEntry entry= classPath.get(i);
	            final String entryPath= entry.getRawPath().toOSString();
	
	            if (i > 0)
	                buff.append(File.pathSeparatorChar);
	            buff.append(entryPath);
	
	            if (entryPath.contains(X10BundleUtils.X10_RUNTIME_BUNDLE_ID)) {//PORT1.7 use constant
	                hasRuntime= true;
	                if (new File(entryPath).exists())
	                    runtimeValid= true;
	            }
	        }
	        if (!hasRuntime || !runtimeValid) {
	            if (buff.length() > 0)
	                buff.append(File.pathSeparatorChar);
	            buff.append(getRuntimePath());
	        }
	        
        } catch (ModelException e) {
            X10DTCorePlugin.getInstance().writeErrorMsg("Error resolving class path: " + e.getMessage());
        }
        
        return buff.toString();
    }

    /**
     * Find and return the location of the X10 runtime, to be used as part of the
     * compiler's search path when editing files (like the XRX sources themselves)
     * that have no associated workspace project.
     */
    private String getRuntimePath() {
        try {
            final URL x10RuntimeURL = X10BundleUtils.getX10RuntimeURL();
            return (x10RuntimeURL == null) ? "" : x10RuntimeURL.getPath();
        } catch (CoreException e) {
            return "";
        }
    }
}
