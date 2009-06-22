package org.eclipse.imp.x10dt.ui.help;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import lpg.runtime.IToken;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IHelpService;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.eclipse.imp.x10dt.ui.parser.PolyglotNodeLocator;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavadocContentAccess;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IWorkbenchPart;

import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassDecl;
import polyglot.ast.Field;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.TypeNode;
import polyglot.ast.Variable;
import polyglot.types.ClassType;
import polyglot.types.Declaration;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Qualifier;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.Position;

public class X10ContextHelper implements IHelpService {
    private final static Map<String,String> sKeywordHelp= new HashMap<String, String>();

    {
        sKeywordHelp.put("static", "static: Identifies the given member as a class member (cf. instance member).");
        sKeywordHelp.put("private", "private: Specifies that the given member is only visible to members of the same class.");
        sKeywordHelp.put("async", "async: runs the child statement block in parallel with the statement(s) following the async.");
        sKeywordHelp.put("future", "future: arranges for the child expression to be lazily evaluated; see 'force'.");
    }

    public IContext getContext(IWorkbenchPart part) {
        return HelpSystem.getContext("x10EditorContextId");
    }
    
    public String getHelp(Object target, IParseController parseController) {
        Node root= (Node) parseController.getCurrentAst();

        if (target instanceof Id) {
            Id id= (Id) target;
            PolyglotNodeLocator locator= (PolyglotNodeLocator) parseController.getNodeLocator();
            Node parent= (Node) locator.getParentNodeOf(id, root);

            if (parent instanceof Field) {
                Field field= (Field) parent;
                FieldInstance fi= field.fieldInstance();
                ReferenceType ownerType= fi.container();

                if (ownerType.isClass()) {
                    ClassType ownerClass= (ClassType) ownerType;
                    String ownerName= ownerClass.fullName();

                    if (isJavaType(ownerName)) {
                        IType javaType= findJavaType(ownerName, parseController);
                        IField javaField= javaType.getField(fi.name());

                        return getJavaDocFor(javaField);
                    } else {
                        return getX10DocFor(fi);
                    }
                }
                return "Field '" + fi.name() + "' of type " + fi.type().toString();
            } else if (parent instanceof NamedVariable) {
                NamedVariable var= (NamedVariable) parent;
                Type type= var.type();

                return "Variable '" + var.name() + "' of type " + type.toString();
            } else if (parent instanceof Call) {
                Call call= (Call) parent;
                MethodInstance mi= call.methodInstance();
                ReferenceType ownerType= mi.container();

                if (ownerType.isClass()) {
                    ClassType ownerClass= (ClassType) ownerType;
                    String ownerName= ownerClass.fullName();

                    if (isJavaType(ownerName)) {
                        IType javaType= findJavaType(ownerName, parseController);
                        String[] paramTypes= convertParamTypes(mi);
                        IMethod method= javaType.getMethod(mi.name(), paramTypes);

                        return getJavaDocFor(method);
                    } else {
                        return getX10DocFor(mi);
                    }
                }
                return "Method " + mi.signature() + " of type " + mi.container().toString();
            } else if (parent instanceof ClassDecl) {
                ClassDecl cd= (ClassDecl) parent;

                return getX10DocFor(cd);
            } else if (parent instanceof MethodDecl) {
                MethodDecl md= (MethodDecl) parent;

                return getX10DocFor(md.methodInstance());
            }
            return id.id();
        } else if (target instanceof TypeNode) {
            TypeNode typeNode= (TypeNode) target;
            Type type= typeNode.type();
            String qualifiedName= typeNode.qualifier().toString();
            qualifiedName= stripArraySuffixes(qualifiedName);

            if (isJavaType(qualifiedName)) {
                IType javaType= findJavaType(qualifiedName, parseController);

                return (javaType != null) ? getJavaDocFor(javaType) : "";
            } else {
                return type.isClass() ? getX10DocFor((ClassType) type) : "";
            }
        }
        // JavadocContentAccess seems to provide no way to get at that package docs...
//        else if (target instanceof PackageNode) {
//            PackageNode pkgNode= (PackageNode) target;
//            String pkgName= pkgNode.qualifier().toString();
//            IJavaProject javaProject= JavaCore.create(parseController.getProject().getRawProject());
//            IPackageFragmentRoot[] pkgFragRoots= javaProject.getAllPackageFragmentRoots();
//            for(int i= 0; i < pkgFragRoots.length; i++) {
//                IPackageFragmentRoot pkgRoot= pkgFragRoots[i];
//                IPackageFragment pkgFrag= pkgRoot.getPackageFragment(pkgName);
//                if (pkgFrag.exists()) {
//                    JavadocContentAccess.???()
//                }
//            }
//
//        }
        return "This is a " + target.getClass().getCanonicalName();
    }

    private String getX10DocFor(Declaration decl) {
        Position pos= decl.position();
        String path= pos.file();

        try {
            Reader reader= new FileReader(new File(path));
            String fileSrc= readReader(reader);

            int idx= pos.offset();
            idx= skipBackwardWhite(fileSrc, idx);
            if (lookingPastEndOf(fileSrc, idx, "*/")) {
                String doc= collectBackwardTo(fileSrc, idx, "/**");
                return doc;
            }
        } catch (IOException e) {
        }
        return null;
    }

    private String collectBackwardTo(String fileSrc, int idx, String string) {
        return fileSrc.substring(fileSrc.lastIndexOf(string, idx), idx);
    }

    private boolean lookingPastEndOf(String fileSrc, int endIdx, String string) {
        int idx= endIdx - string.length();
        return fileSrc.indexOf(string, idx) == idx;
    }

    private int skipBackwardWhite(String fileSrc, int idx) {
        while (idx > 0 && Character.isWhitespace(fileSrc.charAt(idx-1)))
            idx--;
        return idx;
    }

    private String getX10DocFor(ClassDecl decl) {
        Position pos= decl.position();
        String path= pos.file();

        try {
            Reader reader= new FileReader(new File(path));
            String fileSrc= readReader(reader);
            int idx= pos.offset();

            idx= skipBackwardWhite(fileSrc, idx);
            if (lookingPastEndOf(fileSrc, idx, "*/")) {
                String doc= collectBackwardTo(fileSrc, idx, "/**");
                return doc;
            }
        } catch (IOException e) {
        }
        return null;
    }

    private String stripArraySuffixes(String qualifiedName) {
        while (qualifiedName.endsWith("[]")) {
            qualifiedName= qualifiedName.substring(0, qualifiedName.length() - 2);
        }
        return qualifiedName;
    }

    private boolean isJavaType(String qualifiedName) {
        return qualifiedName.startsWith("java.");
    }

    private String[] convertParamTypes(MethodInstance mi) {
        String[] paramTypes= new String[mi.formalTypes().size()];

        int i= 0;
        for(Iterator iterator= mi.formalTypes().iterator(); iterator.hasNext(); ) {
            Type formalType= (Type) iterator.next();
            String typeName= formalType.toString();
            String typeSig= (typeName.indexOf('.') > 0) ? "L" + formalType.toString() + ";" : typeName;
            paramTypes[i++]= typeSig;
        }
        return paramTypes;
    }

    private String getJavaDocFor(IMember member) {
        try {
            Reader reader= JavadocContentAccess.getHTMLContentReader(member, true, true);

            return readReader(reader);
        } catch (JavaModelException e) {
            return "";
        }
    }

    private IType findJavaType(String qualName, IParseController parseController) {
        try {
            IJavaProject javaProject= JavaCore.create(parseController.getProject().getRawProject());
            IType javaType= javaProject.findType(qualName);

            return javaType;
        } catch (JavaModelException e) {
            return null;
        }
    }

    private String readReader(Reader reader) {
        try {
            StringBuffer buffer= new StringBuffer();
            char[] part= new char[2048];
            int read= 0;
    
            while ((read= reader.read(part)) != -1)
                buffer.append(part, 0, read);

            return buffer.toString();
        } catch (IOException ex) {
            System.err.println("I/O Exception: " + ex.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // silently ignored
                }
            }
        }
        return "";
    }

    public String getHelp(IRegion target, IParseController parseController) {
        ParseController pc= (ParseController) parseController;
        IToken token= pc.getLexer().getLexStream().getPrsStream().getTokenAtCharacter(target.getOffset());

        if (token != null) {
            String tokenStr= token.toString();

            if (sKeywordHelp.containsKey(tokenStr)) {
                return sKeywordHelp.get(tokenStr);
            }
        }
        ISourcePositionLocator nodeLocator= parseController.getNodeLocator();
        Object node= nodeLocator.findNode(parseController.getCurrentAst(), target.getOffset());
        if (node != null) {
            return getHelp(node, parseController);
        }
        return null;
    }

    public String getContextId(String baseContextId) {
        return X10UIPlugin.PLUGIN_ID + ".x10EditorContext";
    }
}
