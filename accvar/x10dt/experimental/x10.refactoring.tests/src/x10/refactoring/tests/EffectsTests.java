package x10.refactoring.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import lpg.runtime.Monitor;

import org.eclipse.imp.x10dt.refactoring.analysis.ReachingDefsVisitor;
import org.eclipse.imp.x10dt.refactoring.effects.EffectsVisitor;

import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileResource;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.main.Report;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import polyglot.util.StdErrorQueue;
import x10.constraint.XArray;
import x10.constraint.XArrayElement;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.effects.constraints.ArrayLocs;
import x10.effects.constraints.Effect;
import x10.effects.constraints.LocalLocs;
import x10.effects.constraints.Locs;

public class EffectsTests extends TestCase {
    private static final class NoCancelMonitor implements Monitor {
        public boolean isCancelled() { return false; }
    }

    private TestExtensionInfo fExtInfo;
    private File fTestSrcFile;

    private File getX10RuntimeSrc() {
        return new File("../x10.runtime.17/src-x10");
    }

    private SourceFile getAST() throws IOException {
        String testSrcName= getName().substring(4) + ".x10";
        String testSrcPath = "testSrc" + File.separator + testSrcName;
        fTestSrcFile = new File(testSrcPath);

        Monitor monitor= new NoCancelMonitor();
        fExtInfo = new TestExtensionInfo(monitor);
        ErrorQueue eq= new StdErrorQueue(System.err, 100, "stderr");
        Options opts = fExtInfo.getOptions();

        opts.assertions= true;
        opts.serialize_type_info= false;
        opts.post_compiler= null;
        opts.compile_command_line_only= true;
        opts.source_path.add(new File("."));
        opts.source_path.add(getX10RuntimeSrc());

        Compiler compiler = new Compiler(fExtInfo, eq);
        Globals.initialize(compiler);
        Report.setQueue(eq);

        Source fileSource= new FileSource(new FileResource(fTestSrcFile));
        Collection<Source> sources= new ArrayList<Source>();

        sources.add(fileSource);
        fExtInfo.setInterestingSources(sources);
        compiler.compile(sources);

        return (SourceFile) fExtInfo.getASTFor(fileSource);
    }

    private String readFileContents(File testSrcFile) throws IOException {
        FileInputStream is= new FileInputStream(testSrcFile);
        int len= is.available();
        FileReader rdr= new FileReader(testSrcFile);
        char[] buf= new char[len];
        if (rdr.read(buf) < 0) {
            assertTrue("Unable to read test input source", false);
        }
        rdr.close();
        return new String(buf);
    }

    private static final String TEXT_MARKER_START= "/*<<<*/";
    private static final String TEXT_MARKER_END= "/*>>>*/";

    private Position findMarkedText() {
        try {
            String src = readFileContents(fTestSrcFile);
            int start= src.indexOf(TEXT_MARKER_START);
            int end= src.indexOf(TEXT_MARKER_END);

            return new Position(fTestSrcFile.getAbsolutePath(), fTestSrcFile.getName(), 1, 0, 1, 0, start, end);
        } catch (IOException e) {
            assertTrue(e.getMessage(), false);
            return null;
        }
    }

    public Node findNode(Node node, Position pos) {
        PolyglotNodeLocator loc= new PolyglotNodeLocator();

        return loc.findNode(node, pos.offset(), pos.endOffset());
    }

    /**
     * Return the unique method in the given test source whose name is "test".
     * It can have any signature.
     */
    private X10MethodDecl getTestMethod(SourceFile srcFileNode) {
        List<TopLevelDecl> tlds= srcFileNode.decls();

        for(TopLevelDecl tld: tlds) {
            if (tld instanceof ClassDecl) {
                ClassDecl cd = (ClassDecl) tld;
                List<ClassMember> members= cd.body().members();

                for(ClassMember member: members) {
                    if (member instanceof MethodDecl) {
                        MethodDecl md = (MethodDecl) member;
                        if (md.name().toString().equals("test")) {
                            return (X10MethodDecl) md;
                        }
                    }
                }
            }
        }
        return null;
    }

    private interface EffectAssertion {
        public Locs satisfiedBy(Effect e);
    }

    private final List<EffectAssertion> fExpectedEffects= new ArrayList<EffectAssertion>();

    private final Set<Locs> fExtraReadEffects= new HashSet<Locs>();
    private final Set<Locs> fExtraWriteEffects= new HashSet<Locs>();
    private final Set<Locs> fExtraAtomicEffects= new HashSet<Locs>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fExpectedEffects.clear();
        fExtraReadEffects.clear();
        fExtraWriteEffects.clear();
        fExtraAtomicEffects.clear();
    }

    private static abstract class EffectBase implements EffectAssertion {
        protected final String fVarName;
        protected final String fIndex;

        public EffectBase(String varName) {
            fVarName = varName;
            fIndex= null;
        }

        public EffectBase(String arrayName, String index) {
            fVarName= arrayName;
            fIndex = index;
        }
        protected Locs searchFor(String varName, Set<Locs> locs) {
            for(Locs loc: locs) {
                if (loc instanceof LocalLocs) {
                    LocalLocs ll = (LocalLocs) loc;
                    XLocal x= ll.local();
                    if (x.toString().equals(varName)) {
                        return loc;
                    }
                }
            }
            return null;
        }

        protected Locs searchFor(String arrayName, String index, Set<Locs> locs) {
            for(Locs loc: locs) {
                if (loc instanceof ArrayLocs) {
                    ArrayLocs al = (ArrayLocs) loc;
                    XTerm arrayTerm= al.designator();
                    if (arrayTerm instanceof XArrayElement) {
                        XArrayElement ae= (XArrayElement) al.designator();
                        XArray a= ae.array();
                        XTerm i= ae.index();
                        if (a.toString().equals(arrayName) && i.toString().equals(index)) {
                            return loc;
                        }
                    } else if (arrayTerm instanceof XArray) {
                        XArray array= (XArray) arrayTerm;
                        if (((XLocal) array).name().toString().equals(fVarName)) {
                            return loc;
                        }
                    }
                }
            }
            return null;
        }

        protected abstract String typeString();
        protected abstract Set<Locs> getLocSet(Effect e);
        protected abstract Set<Locs> getExtraEffectsSet();

        public Locs satisfiedBy(Effect e) {
            Locs satisfyingLoc;
            if (fIndex != null) {
                satisfyingLoc= searchFor(fVarName, fIndex, getLocSet(e));
            } else {
                satisfyingLoc= searchFor(fVarName, getLocSet(e));
            }
            if (satisfyingLoc != null) {
                getExtraEffectsSet().remove(satisfyingLoc);
            }
            return satisfyingLoc;
        }

        public String toString() {
            return typeString() + ": [" + fVarName + (fIndex != null ? ("(" + fIndex + ")"): "]");
        }
    }

    private class ReadAssertion extends EffectBase {
        public ReadAssertion(String varName) { super(varName); }
        public ReadAssertion(String array, String index) {
            super(array, index);
        }
        @Override
        protected Set<Locs> getLocSet(Effect e) { return e.readSet(); }
        @Override
        protected Set<Locs> getExtraEffectsSet() { return fExtraReadEffects; }
        
        @Override
        public String typeString() {
            return "r";
        }
    }

    private class WriteAssertion extends EffectBase {
        public WriteAssertion(String varName) { super(varName); }
        public WriteAssertion(String array, String index) {
            super(array, index);
        }
        @Override
        protected Set<Locs> getLocSet(Effect e) { return e.writeSet(); }
        @Override
        protected Set<Locs> getExtraEffectsSet() { return fExtraWriteEffects; }
        @Override
        public String typeString() {
            return "w";
        }
    }

    private class AtomicIncAssertion extends EffectBase {
        public AtomicIncAssertion(String varName) { super(varName); }
        public AtomicIncAssertion(String array, String index) {
            super(array, index);
        }
        @Override
        protected Set<Locs> getLocSet(Effect e) { return e.atomicIncSet(); }
        @Override
        protected Set<Locs> getExtraEffectsSet() { return fExtraAtomicEffects; }
        @Override
        public String typeString() {
            return "a";
        }
    }

    /**
     * convenience method for creating a ReadAssertion and putting it in the proper
     * set of expected effects
     */
    private void read(String name) {
        fExpectedEffects.add(new ReadAssertion(name));
    }

    /**
     * convenience method for creating a WriteAssertion and putting it in the proper
     * set of expected effects
     */
    private void write(String name) {
        fExpectedEffects.add(new WriteAssertion(name));
    }

    /**
     * convenience method for creating a AtomicIncAssertion and putting it in the proper
     * set of expected effects
     */
    private void atomicInc(String name) {
        fExpectedEffects.add(new AtomicIncAssertion(name));
    }

    private boolean verifyEffects(Effect eff) {
        // Assume they're all extra effects, and remove them as we find matching assertions
        fExtraAtomicEffects.addAll(eff.atomicIncSet());
        fExtraReadEffects.addAll(eff.readSet());
        fExtraWriteEffects.addAll(eff.writeSet());

        boolean foundAll= true;

        for(EffectAssertion ea: fExpectedEffects) {
            if (ea.satisfiedBy(eff) == null) {
                System.out.println(" *** Unsatisfied effect assertion: " + ea);
                foundAll= false;
            }
        }
        boolean foundExtra= !fExtraAtomicEffects.isEmpty() || !fExtraReadEffects.isEmpty() || !fExtraWriteEffects.isEmpty();

        return foundAll && !foundExtra;
    }

    private void runEffectsTest() throws Exception {
        SourceFile srcFileNode= getAST();
        X10MethodDecl md= getTestMethod(srcFileNode);
        Position markPos = findMarkedText();
        Node markedNode= findNode(srcFileNode, markPos);
        EffectsVisitor ev= new EffectsVisitor(new ReachingDefsVisitor.ValueMap(), md);

        ev.setVerbose(System.out);
        markedNode.visit(ev);
        Effect eff= ev.getEffectFor(markedNode);

        System.out.println("Effect of node " + markedNode);
        System.out.println(" ==> " + eff);

        assertTrue("Unsatisfied effect assertions", verifyEffects(eff));
    }

    public void testLocals1() throws Exception {
        runEffectsTest();
    }

    public void testLocals2() throws Exception {
        write("z");
        runEffectsTest();
    }
}
