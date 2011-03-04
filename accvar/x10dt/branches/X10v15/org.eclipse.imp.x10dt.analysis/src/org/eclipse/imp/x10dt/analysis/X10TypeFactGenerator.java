package org.eclipse.imp.x10dt.analysis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.IWorkspaceModel;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.pdb.analysis.AnalysisException;
import org.eclipse.imp.pdb.analysis.IFactGenerator;
import org.eclipse.imp.pdb.analysis.IFactUpdater;
import org.eclipse.imp.pdb.facts.IRelation;
import org.eclipse.imp.pdb.facts.IRelationWriter;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISetWriter;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.ISourceRange;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.IFactKey;
import org.eclipse.imp.pdb.facts.db.context.ISourceEntityContext;
import org.eclipse.imp.pdb.facts.db.context.ProjectContext;
import org.eclipse.imp.pdb.facts.impl.hash.ValueFactory;
import org.eclipse.imp.pdb.facts.type.NamedType;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.indexing.IndexManager;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.imp.x10dt.core.builder.X10ProjectNature;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.ParsedClassType;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.ClassReader;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

public class X10TypeFactGenerator implements IFactGenerator, IFactUpdater {
    private final ValueFactory vf= ValueFactory.getInstance();

    private final static String[] sClassFilters= new String[] {
        "sun.",
        "com.sun."
    };

    private static final class SimpleMessageHandler implements IMessageHandler {
        public void clearMessages() { }

        public void startMessageGroup(String groupName) { }

        public void endMessageGroup() { }

        public void handleSimpleMessage(String msg, int startOffset, int endOffset, int startCol, int endCol, int startLine, int endLine) {
            System.err.println(msg);
        }
    }

    private static class ProjectAddDeleteListener implements IResourceChangeListener {
        private final Type[] factTypes;

        private ProjectAddDeleteListener(Type[] factTypes) {
            this.factTypes= factTypes;
        }

        public void resourceChanged(IResourceChangeEvent event) {
            IResourceDelta delta= event.getDelta();
            IResource rsrc= delta.getResource();
            int kind= delta.getKind();

            if (rsrc instanceof IProject) {
                IProject project= (IProject) rsrc;
                if (kind == IResourceDelta.REMOVED) {
                    try {
                        for(int j= 0; j < factTypes.length; j++) {
                            IFactKey key= new FactKey(factTypes[j], new ProjectContext(ModelFactory.open(project)));
                            IndexManager.cancelFactUpdating(key);
                        }
                    } catch (ModelException e) {
                        X10AnalysisPlugin.log(e);
                    }
                } else if (kind == IResourceDelta.ADDED) {
                    try {
                        for(int j= 0; j < factTypes.length; j++) {
                            IFactKey key= new FactKey(factTypes[j], new ProjectContext(ModelFactory.open(project)));
                            IndexManager.keepFactUpdated(key);
                        }
                    } catch (ModelException e) {
                        X10AnalysisPlugin.log(e);
                    }
                }
            }
        }
    }

    private static void arrangeForFactUpdates(final Type[] factTypes, IWorkspace ws) {
        IWorkspaceRoot wsRoot= ws.getRoot();
        IProject[] projects= wsRoot.getProjects();

        for(int i= 0; i < projects.length; i++) {
            IProject project= projects[i];

            try {
                if (project.hasNature(X10ProjectNature.k_natureID)) {
                    for(int j= 0; j < factTypes.length; j++) {
                        IFactKey key= new FactKey(factTypes[j], new ProjectContext(ModelFactory.open(project)));
                        // Don't ask for the fact value now - that will delay start-up. Just ask for it to get
                        // updated, and it will get done in the background.
//                      IValue fact= FactBase.getInstance().getFact(key);

                        IndexManager.keepFactUpdated(key);
                    }
                }
            } catch (CoreException e) {
                X10AnalysisPlugin.log(e);
            } catch (ModelException e) {
                X10AnalysisPlugin.log(e);
//          } catch (AnalysisException e) {
//              X10UIPlugin.log(e);
            } catch (Exception e) {
                X10AnalysisPlugin.log(e);
            }
        }
    }

    static {
        if (true) {
            Type[] factTypes= new Type[] { X10FactTypes.X10Types, X10FactTypes.X10TypeHierarchy };
            IWorkspace workspace= ResourcesPlugin.getWorkspace();

            arrangeForFactUpdates(factTypes, workspace);
            workspace.addResourceChangeListener(new ProjectAddDeleteListener(factTypes), IResourceChangeEvent.POST_CHANGE);
        }
    }

    private final class TypeVisitor extends NodeVisitor {
        @Override
        public NodeVisitor enter(Node n) {
            if (n instanceof ClassDecl) {
                ClassDecl cd= (ClassDecl) n;
                ParsedClassType cdType= cd.type();
                TypeNode superTypeNode= cd.superClass();
                ClassType superClass;

                if (superTypeNode != null) {
                    polyglot.types.Type superType= superTypeNode.type();
                    if (superType instanceof ClassType) {
                        superClass= (ClassType) superType;
                    } else {
                        System.err.println("Encountered unknown type " + superType + " as super type of " + cd.name());
                        superClass= null;
                    }
                } else {
                    superClass= cdType.typeSystem().Object();
                }

                Position pos= cdType.position();

                fTypesSW.insert(vf.tuple(typeNameFor(cdType), vf.sourceLocation(pos.path(), positionToSourcePosition(pos))));
                fDerivesRW.insert(vf.tuple(typeNameFor(cdType), typeNameFor(superClass)));
        
                List<TypeNode> intfs= cd.interfaces();
                for (TypeNode tn : intfs) {
                    fDerivesRW.insert(vf.tuple(typeNameFor(cdType), typeNameFor((ClassType) tn.type())));
                }
            }
            return this;
        }

        private ISourceRange positionToSourcePosition(Position pos) {
            return vf.sourceRange(pos.offset(), pos.endOffset() - pos.offset(), pos.line(), pos.endLine(), pos.column(), pos.endColumn());
        }

        private IString typeNameFor(ClassType cdType) {
            if (cdType != null) {
                return vf.string(X10FactTypes.X10TypeName, cdType.fullName());
            } else {
                return vf.string(X10FactTypes.X10TypeName, "<unknown>");
            }
        }
    }

    private IRelationWriter fDerivesRW;
    private ISetWriter fTypesSW;
    private IParseController fParseController;
    private final TypeVisitor fTypeVisitor= new TypeVisitor();

    private final ISourceRange EMPTY_SOURCE_RANGE= vf.sourceRange(0, 0, 0, 0, 0, 0);

    public void generate(FactBase factBase, Type type, IFactContext context) throws AnalysisException {
        IRelation derivesRel= vf.relation(X10FactTypes.X10TypeHierarchy);
        ISet typesSet= vf.set(X10FactTypes.X10Types);

        fTypesSW= typesSet.getWriter();
        fDerivesRW= derivesRel.getWriter();

        ISourceEntityContext sec= (ISourceEntityContext) context;
        ISourceEntity srcEntity= sec.getEntity();

        final ISourceProject srcProject= (ISourceProject) srcEntity.getAncestor(ISourceProject.class);

        // Create a parse controller for this project, shared for all entities scanned within the project
        fParseController= ServiceFactory.getInstance().getParseController(LanguageRegistry.findLanguage("X10"));

        processEntity(srcEntity, srcProject);

        fDerivesRW.done();
        fTypesSW.done();

        factBase.defineFact(new FactKey(X10FactTypes.X10TypeHierarchy, context), derivesRel);
        factBase.defineFact(new FactKey(X10FactTypes.X10Types, context), typesSet);
    }

    private void processEntity(ISourceEntity srcEntity, final ISourceProject srcProject) throws AnalysisException {
        List<IPath> scanPath= getEntitiesForContext(srcEntity);
        IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();
        IPath wsPath= wsRoot.getLocation();

        for(IPath path: scanPath) {
            if (wsPath.isPrefixOf(path)) {
                IResource resource= wsRoot.findMember(path.removeFirstSegments(wsPath.segmentCount()));
                try {
                    resource.accept(new IResourceVisitor() {
                        public boolean visit(IResource resource) throws CoreException {
                            if (resource instanceof IFile) {
                                IFile file= (IFile) resource;
                                if ("jar".equals(file.getFileExtension())) {
                                    try {
                                        processJarFile(file.getLocation(), srcProject);
                                    } catch (IOException e) {
                                        throw wrapException(e);
                                    }
                                } else if ("java".equals(file.getFileExtension())) {
                                    processSourceFile(file);
                                } else if ("class".equals(file.getFileExtension())) {
                                    processClassFile(file);
                                }
                                return false;
                            }
                            return true;
                        }
                    });
                } catch (CoreException e) {
                    throw new AnalysisException("Exception encountered during type hierarchy extraction: " + e.getMessage(), e);
                }
            } else { // it's outside the workspace
                if ("jar".equals(path.getFileExtension())) {
                    try {
                        processJarFile(path, srcProject);
                    } catch (IOException e) {
                        X10AnalysisPlugin.log("Error processing jar file " + path, e);
                    }
                } else {
                    X10AnalysisPlugin.log("Unable to handle external entity " + path);
                }
            }
        }
    }

    private interface ITuplePredicate {
        boolean isSatisfiedBy(ITuple t);
    }

    public void update(FactBase factBase, Type type, IFactContext context, IResource res) throws AnalysisException {
        try {
            FactKey hierKey= new FactKey(X10FactTypes.X10TypeHierarchy, context);
            FactKey typesKey= new FactKey(X10FactTypes.X10Types, context);
            Set<String> typesToRemove= new HashSet<String>();
            ITuplePredicate typesPred= createTypesPredicate(res);
            ISet newTypesSet= getSetForUpdating(factBase, typesKey, typesPred, typesToRemove);
            ITuplePredicate hierPred= createHierPredicate(typesToRemove);
            IRelation newDerivesRel= getRelationForUpdating(factBase, hierKey, hierPred);

            fDerivesRW= newDerivesRel.getWriter();
            fTypesSW= newTypesSet.getWriter();

            if (res instanceof IProject) {
                ISourceProject srcProject= ModelFactory.open((IProject) res);

                // Create a new parse controller for scanning this project
                fParseController= ServiceFactory.getInstance().getParseController(LanguageRegistry.findLanguage("X10"));
                processEntity(srcProject, srcProject);
            } else if (res instanceof IFile) {
                // TODO Cache the parse controller, compare its project to this one, and create a new one if they don't match
                try {
                    processSourceFile((IFile) res);
                } catch (CoreException e) {
                    
                }
//                IFile file= (IFile) res;
//                ISourceProject srcProject= ModelFactory.open(res.getProject());
//                ICompilationUnit icu= ModelFactory.open(file, srcProject);
//                IMessageHandler mh= new SimpleMessageHandler();
//                Object root= icu.getAST(mh, new NullProgressMonitor());
//                Node astRoot= (Node) root;
//                final ValueFactory vf= ValueFactory.getInstance();
//
//                astRoot.visit(fTypeVisitor);
            }
            factBase.defineFact(hierKey, newDerivesRel);
            factBase.defineFact(typesKey, newTypesSet);
        } catch (ModelException e) {
            throw new AnalysisException("Error while updating fact base from " + res.getFullPath(), e);
        }
    }

    private ITuplePredicate createTypesPredicate(IResource res) {
        ITuplePredicate pred= null;

        if (res instanceof IProject) {
            final String pathStr= res.getFullPath().toOSString();
            pred= new ITuplePredicate() {
                public boolean isSatisfiedBy(ITuple t) {
                    ISourceLocation loc= (ISourceLocation) t.get(1);
                    return loc.getPath().startsWith(pathStr);
                }
            };
        } else if (res instanceof IFile) {
            final IString pathStr= vf.string(res.getFullPath().toOSString());
            pred= new ITuplePredicate() {
                public boolean isSatisfiedBy(ITuple t) {
                    ISourceLocation loc= (ISourceLocation) t.get(1);
                    return loc.getPath().equals(pathStr);
                }
            };
        }

        return pred;
    }

    private ITuplePredicate createHierPredicate(final Set<String> typesToRemove) {
        ITuplePredicate pred= new ITuplePredicate() {
            public boolean isSatisfiedBy(ITuple t) {
                IString lhsType= (IString) t.get(0);
                IString rhsType= (IString) t.get(1);

                return typesToRemove.contains(lhsType.getValue()) || typesToRemove.contains(rhsType.getValue());
            }
        };
        return pred;
    }

    private ISet getSetForUpdating(FactBase factBase, IFactKey key, ITuplePredicate pred, Set<String> typesToRemove) {
        ISet resultSet;

        if (factBase.getAllKeys().contains(key)) {
            ISet oldSet;

            try {
                oldSet= factBase.getSet(key);
            } catch (AnalysisException e) { // should never happen -- the fact base the fact exists
                oldSet= vf.set((NamedType) key.getType()); // just use an empty set in case of error above
            }
                
            resultSet= vf.set((NamedType) key.getType());
            ISetWriter sw= resultSet.getWriter();

            // Copy over the old set elements that aren't being updated (that aren't from this resource)
            for(Iterator<IValue> iter= oldSet.iterator(); iter.hasNext(); ) {
                ITuple t= (ITuple) iter.next();

                if (!pred.isSatisfiedBy(t)) {
                    sw.insert(t);
                } else {
                    typesToRemove.add(((IString) t.get(0)).getValue());
                }
            }
        } else {
            resultSet= vf.set((NamedType) key.getType());
        }
        return resultSet;
    }

    private IRelation getRelationForUpdating(FactBase factBase, IFactKey key, ITuplePredicate pred) {
        IRelation resultRel;

        if (factBase.getAllKeys().contains(key)) {
            IRelation oldRel;

            try {
                oldRel= factBase.getRelation(key);
            } catch (AnalysisException e) {
                oldRel= vf.relation((NamedType) key.getType());
            }

            resultRel= vf.relation((NamedType) key.getType());
            fDerivesRW= resultRel.getWriter();

            // Copy over the old relation tuples that aren't being updated (that aren't from this resource)
            for(Iterator<ITuple> iter= oldRel.iterator(); iter.hasNext(); ) {
                ITuple t= iter.next();

                if (!pred.isSatisfiedBy(t)) {
                    fDerivesRW.insert(t);
                }
            }
        } else {
            resultRel= vf.relation((NamedType) key.getType());

            fDerivesRW= resultRel.getWriter();
        }
        return resultRel;
    }

    private void processJarFile(IPath path, ISourceProject project) throws IOException {
        JarFile jarFile= new JarFile(new File(path.toOSString()));
        Enumeration<JarEntry> entryEnum= jarFile.entries();

        while (entryEnum.hasMoreElements()) {
            JarEntry jarEntry= entryEnum.nextElement();
            String entryName= jarEntry.getName();
            IPath entryPath= path.append(jarEntry.getName());

            if (entryName.endsWith(".class")) {
                processClassFile(entryPath, jarFile.getInputStream(jarEntry), (int) jarEntry.getSize());
            } else if (entryName.endsWith(".java")) {
                // TODO: this triggers a FileNotFoundException in polyglot.frontend.FileSource:30
//                processSourceFile(entryPath, jarFile.getInputStream(jarEntry), project);
            }
        }
    }

    private void processClassFile(IFile file) throws CoreException {
        File javaFile= new File(file.getLocation().toOSString());
        processClassFile(file.getLocation(), file.getContents(), (int) javaFile.length());
    }

    private void processClassFile(IPath path, InputStream contents, int length) {
        try {
            byte[] classBytes= readStreamFully(contents, length, path);

            ClassInstrumenter ci= new ClassInstrumenter(classBytes);
            ClassReader cr= ci.getReader();
            String className= cr.getName().replace('/', '.');

            for(int i= 0; i < sClassFilters.length; i++) {
                if (className.startsWith(sClassFilters[i])) {
                    return;
                }
            }

            String superName= cr.getSuperName();
            String[] intfNames= cr.getInterfaceNames();
            IString classNameVal= vf.string(X10FactTypes.X10TypeName, className);

            if (superName != null) {
                superName= superName.replace('/', '.');
            }
            fTypesSW.insert(vf.tuple(classNameVal, vf.sourceLocation(path.toOSString(), EMPTY_SOURCE_RANGE)));
            if (superName != null) {
                fDerivesRW.insert(vf.tuple(classNameVal, vf.string(X10FactTypes.X10TypeName, superName)));
            }
            for(int i= 0; i < intfNames.length; i++) {
                String intfName= intfNames[i].replace('/', '.');

                fDerivesRW.insert(vf.tuple(classNameVal, vf.string(X10FactTypes.X10TypeName, intfName)));
            }
        } catch (InvalidClassFileException e) {
            e.printStackTrace();
        }
    }

    private byte[] readStreamFully(InputStream contents, int length, IPath path) {
        byte[] buf= new byte[length];
        int offset= 0;
        int bytesLeft= length;

        do {
            try {
                int readLen= contents.read(buf, offset, bytesLeft);

                if (readLen == bytesLeft) {
                    break;
                } else if (readLen < 0) {
                    throw new IllegalArgumentException("Unable to fully read class file " + path);
                }
                bytesLeft -= readLen;
                offset += readLen;
            } catch (IOException e) {
                X10AnalysisPlugin.log(e);
                break;
            }
        } while (true);

        return buf;
    }

    private void processSourceFile(IFile file) throws CoreException {
        try {
            processSourceFile(file.getLocation(), file.getContents(), ModelFactory.open(file.getProject()));
        } catch (ModelException e) {
            throw wrapException(e);
        }
    }

    private CoreException wrapException(Exception e) {
        return new CoreException(new Status(IStatus.ERROR, X10AnalysisPlugin.PLUGIN_ID, 0, e.getMessage(), e));
    }

    private void processSourceFile(IPath path, InputStream is, ISourceProject srcProject) {
        String contents= StreamUtils.readStreamContents(is);
        IMessageHandler mh= new SimpleMessageHandler();

        fParseController.initialize(path.removeFirstSegments(srcProject.getRawProject().getLocation().segmentCount()), srcProject, mh);

        SourceFile astRoot= (SourceFile) fParseController.parse(contents, false, new NullProgressMonitor());

        astRoot.visit(fTypeVisitor);
    }

    /**
     * @return a list of folders and jars to be scanned for source and class files
     */
    private List<IPath> getEntitiesForContext(ISourceEntity entity) {
        if (entity instanceof ISourceFolder) {
            return Collections.singletonList(entity.getResource().getLocation());
        }
        if (entity instanceof ISourceProject) {
            // TODO Pick up JDT project config if project has Java nature?
            ISourceProject sp= (ISourceProject) entity;
            List<IPath> result= new ArrayList<IPath>();
            IPath wsPath= ResourcesPlugin.getWorkspace().getRoot().getLocation();
            List<IPathEntry> buildPath= sp.getBuildPath();

            for(IPathEntry entry : buildPath) {
                if (entry.getEntryType() == IPathEntry.PathEntryType.ARCHIVE) {
                    result.add(entry.getPath());
                } else if (entry.getEntryType() == IPathEntry.PathEntryType.PROJECT) {
                    throw new UnsupportedOperationException("Don't handle cross-project dependencies");
                } else if (entry.getEntryType() == IPathEntry.PathEntryType.SOURCE_FOLDER) {
                    if (entry.getPath().isAbsolute()) {
                        result.add(wsPath.append(entry.getPath()));
                    } else {
                        result.add(sp.getRawProject().getLocation().append(entry.getPath()));
                    }
                } else if (entry.getEntryType() == IPathEntry.PathEntryType.CONTAINER) {
                    throw new UnsupportedOperationException("Don't handle container classpath entries");
                }
            }
            return result;
        }
        if (entity instanceof ICompilationUnit) {
            return Collections.singletonList(entity.getResource().getLocation());
        }
        if (entity instanceof IWorkspaceModel) {
            throw new UnsupportedOperationException("Can't handle workspace context");
        }
        return null;
    }
}
