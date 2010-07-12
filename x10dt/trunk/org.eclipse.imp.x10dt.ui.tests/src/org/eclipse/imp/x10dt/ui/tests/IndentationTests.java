/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.imp.x10dt.ui.tests;

import junit.framework.Assert;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.x10dt.core.preferences.generated.X10Constants;
import org.eclipse.imp.x10dt.ui.editor.X10AutoIndentStrategy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * @author rfuhrer@watson.ibm.com
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class IndentationTests {
    private X10AutoIndentStrategy fIndentStrategy= new X10AutoIndentStrategy();
    private IPreferencesService fPrefsService= new PreferencesService();

    @BeforeClass
    public static void setup() {
        IWorkbench wb= PlatformUI.getWorkbench();
        IWorkbenchWindow win= wb.getActiveWorkbenchWindow();
        wb.getIntroManager().closeIntro(wb.getIntroManager().getIntro());
        try {
            win.openPage("org.eclipse.imp.x10dt.ui.perspective", null);
        } catch (WorkbenchException e) {
            e.printStackTrace();
        }
    }

    private DocumentCommand scanTextPopulateCommand(IDocument doc) {
        String text= doc.get();
        int markerPos= text.indexOf("// <<<");
        String[] lineDelims= doc.getLegalLineDelimiters();
        int lineBegin= text.lastIndexOf(lineDelims[0], markerPos);

        if (lineBegin < 0) {
            lineBegin= 0;
        } else if (lineBegin < markerPos && lineBegin > 0) {
            lineBegin++; // move past newline
        }

        DocumentCommand cmd= new DocumentCommand() {
            @Override
            public String toString() {
                return "<cmd offset: " + offset + ", length: " + length + ", text: '" + text + "'>";
            }
        };

        cmd.offset= cmd.caretOffset= lineBegin;
        cmd.text= "\t";
        cmd.length= 0;
        cmd.doit= true;
        cmd.shiftsCaret= true;
        return cmd;
    }

    private void checkForSpacesTabs(String doc, int lineBegin) {
        boolean spacesForTabs= fPrefsService.getBooleanPreference(X10Constants.P_SPACESFORTABS);
        int tabWidth= fPrefsService.getIntPreference(X10Constants.P_TABWIDTH);

        for(int idx= lineBegin; idx < doc.length() && Character.isSpaceChar(doc.charAt(idx)); idx++) {
            Assert.assertTrue(spacesForTabs == (doc.charAt(idx) == ' '));
        }
    }

    private void indentTestHelper(String orig, String expect) {
        IDocument doc= new Document(orig);
        DocumentCommand cmd= scanTextPopulateCommand(doc);

        try {
            int lineNum= doc.getLineOfOffset(cmd.offset);

            fIndentStrategy.customizeDocumentCommand(doc, cmd);

            String modLine= doc.get(doc.getLineOffset(lineNum), doc.getLineLength(lineNum));

            Assert.assertEquals("Indent-for-tab command failed", expect, modLine);
            checkForSpacesTabs(modLine, cmd.offset);
        } catch (BadLocationException e) {
            Assert.fail("Exception caught during tab command: " + e);
        }
    }

    @Test
    public void testPkg() {
        String org= "   package foo.bar; // <<<\n";
        String exp= "package foo.bar; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testClass1() {
        String org= "package foo.bar;\n" +
                    "  public class Foo // <<<";
        String exp= "public class Foo // <<<";

        indentTestHelper(org, exp);
    }

    @Test
    public void testClass2() {
        String org= "package foo.bar;\n" +
                    "\n" +
                    "  public class Foo // <<<";
        String exp= "public class Foo // <<<";

        indentTestHelper(org, exp);
    }

    @Test
    public void testClass3() {
        String org= "package foo.bar;\n" +
                    "    \n" +
                    "  public class Foo // <<<";
        String exp= "public class Foo // <<<";

        indentTestHelper(org, exp);
    }

    @Test
    public void testMember1() {
        String org= "public class Bar {\n" +
                    " public def foo() = true; // <<<\n" +
                    "}";
        String exp= "    public def foo() = true; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testMember1a() {
        String org= "public class Bar {\n" +
                    "      \n" +
                    " public def foo() = true; // <<<\n" +
                    "}";
        String exp= "    public def foo() = true; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testMember2() {
        String org= "public class Bar {\n" +
                    "      public def foo() = true;\n" +
                    " public def bar() = 0; // <<<\n" +
                    "}";
        String exp= "      public def foo() = true; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testMember3() {
        String org= "public class Bar {\n" +
                    "      public def foo() = true;\n" +
                    " var x: Int; // <<<\n" +
                    "}";
        String exp= "      var x: Int; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testMember4() {
        String org= "public class Bar {\n" +
                    "      public def foo() = true;\n" +
                    "\n" +
                    " public def bar() = 0; // <<<\n" +
                    "}";
        String exp= "      public def foo() = true; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt1a() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "  Console.OUT.println(\"Hi\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt1b() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "                 Console.OUT.println(\"Hi\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt1c() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt1d() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "\n" +
                    "        Console.OUT.println(\"Hi\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt1e() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "            \n" +
                    "        Console.OUT.println(\"Hi\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt2a() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "    Console.OUT.println(\"Hey\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hey\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt2b() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "             Console.OUT.println(\"Hey\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hey\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt2c() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        Console.OUT.println(\"Hey\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hey\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt3a() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "           Console.OUT.println(\"Hi\");\n" +
                    "        Console.OUT.println(\"Hey\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "           Console.OUT.println(\"Hey\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt4a() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "\n" +
                    "        Console.OUT.println(\"Hey\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hey\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testStmt4b() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "    \n" +
                    "        Console.OUT.println(\"Hey\"); // <<<\n" +
                    "    }\n" +
                    "}";
        String exp= "        Console.OUT.println(\"Hey\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testForBody1a() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        val a = Array.make[int](10, ((i): Point) => i);\n" +
                    "        for(elt: int in a) {\n" +
                    "     val x = elt * elt; // <<<\n" +
                    "     val y = 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val x = elt * elt; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testForBody1b() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        val a = Array.make[int](10, ((i): Point) => i);\n" +
                    "        for(elt: int in a) {\n" +
                    "               val x = elt * elt; // <<<\n" +
                    "     val y = 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val x = elt * elt; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testForBody1c() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        val a = Array.make[int](10, ((i): Point) => i);\n" +
                    "        for(elt: int in a) {\n" +
                    "            val x = elt * elt; // <<<\n" +
                    "     val y = 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val x = elt * elt; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testForBody2a() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        val a = Array.make[int](10, ((i): Point) => i);\n" +
                    "        for(elt: int in a) {\n" +
                    "               val x = elt * elt;\n" +
                    "     val y = 1; // <<<\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "               val y = 1; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testForBody2b() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        val a = Array.make[int](10, ((i): Point) => i);\n" +
                    "        for(elt: int in a) {\n" +
                    "               val x = elt * elt;\n" +
                    "     val y = 1; // <<<\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "               val y = 1; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testForBody2c() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        val a = Array.make[int](10, ((i): Point) => i);\n" +
                    "        for(elt: int in a) {\n" +
                    "               val x = elt * elt;\n" +
                    "               val y = 1; // <<<\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "               val y = 1; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testAsyncBody1a() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        async {\n" +
                    "     val x = 0; // <<<\n" +
                    "     val y = 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val x = 0; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testAsyncBody1b() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        async {\n" +
                    "                  val x = 0; // <<<\n" +
                    "     val y = 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val x = 0; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testAsyncBody1c() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        async {\n" +
                    "            val x = 0; // <<<\n" +
                    "     val y = 1;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val x = 0; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testAsyncBody2a() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        async {\n" +
                    "            val x = 0;\n" +
                    "     val y = 1; // <<<\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val y = 1; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testAsyncBody2b() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        async {\n" +
                    "            val x = 0;\n" +
                    "                 val y = 1; // <<<\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val y = 1; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testAsyncBody2c() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        async {\n" +
                    "            val x = 0;\n" +
                    "            val y = 1; // <<<\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
        String exp= "            val y = 1; // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testCloseBrkt1() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        Console.OUT.println(\"Hey\");\n" +
                    "  } // <<<\n" +
                    "}";
        String exp= "    } // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testCloseBrkt2() {
        String org= "public class Bar {\n" +
                    "    public def foo() = {\n" +
                    "        Console.OUT.println(\"Hi\");\n" +
                    "        Console.OUT.println(\"Hey\");\n" +
                    "    }\n" +
                    "  } // <<<";
        String exp= "} // <<<";

        indentTestHelper(org, exp);
    }

    @Test
    public void testComment1() {
        String org= "public class Bar {\n" +
                    "    public def foo() = { // say something funny\n" +
                    "  Console.OUT.println(\"Hi\"); // <<<\n" +
                    "        Console.OUT.println(\"Hey\");\n" +
                    "    }\n" +
                    "  }";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testComment2() {
        String org= "public class Bar {\n" +
                    "    public def foo() = { // say something { funny }\n" +
                    "  Console.OUT.println(\"Hi\"); // <<<\n" +
                    "        Console.OUT.println(\"Hey\");\n" +
                    "    }\n" +
                    "  }";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testComment3() {
        String org= "public class Bar {\n" +
                    "    public def foo() = { // say something { not so funny\n" +
                    "  Console.OUT.println(\"Hi\"); // <<<\n" +
                    "        Console.OUT.println(\"Hey\");\n" +
                    "    }\n" +
                    "  }";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }

    @Test
    public void testComment4() {
        String org= "public class Bar {\n" +
                    "    public def foo() = { // say something not so funny }\n" +
                    "  Console.OUT.println(\"Hi\"); // <<<\n" +
                    "        Console.OUT.println(\"Hey\");\n" +
                    "    }\n" +
                    "  }";
        String exp= "        Console.OUT.println(\"Hi\"); // <<<\n";

        indentTestHelper(org, exp);
    }
}
