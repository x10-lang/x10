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

package x10dt.ui.tests;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesService;
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
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import x10dt.core.X10DTCorePlugin;
import x10dt.core.preferences.generated.X10Constants;
import x10dt.ui.editor.formatting.X10AutoIndentStrategy;

/**
 * @author rfuhrer@watson.ibm.com
 */
@RunWith(Parameterized.class)
public class IndentationTests {
  private static IPreferencesService fPrefsService = new PreferencesService(null, X10DTCorePlugin.kLanguageName);

  private X10AutoIndentStrategy fIndentStrategy = new X10AutoIndentStrategy();

  private boolean fSpacesForTabsArg;

  private int fIndentWidthArg;

  @Parameters
  public static Collection<Object[]> testParams() {
    return Arrays.asList(new Object[][] { { true, 4 }, // spaces-for-tabs, indent width 4
                                          { false, 4 }, // NO spaces-for-tabs, indent width 4
                                          { true, 3 }, // spaces-for-tabs, indent width 3
                                          { false, 3 } // NO spaces-for-tabs, indent width 3
    });
  }

  @BeforeClass
  public static void setup() {
    IWorkbench wb = PlatformUI.getWorkbench();
    IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
    wb.getIntroManager().closeIntro(wb.getIntroManager().getIntro());
    try {
      win.openPage("x10dt.ui.perspective", null);
    } catch (WorkbenchException e) {
      e.printStackTrace();
    }
  }

  public IndentationTests(boolean spacesForTabs, int indentWidth) {
    fSpacesForTabsArg = spacesForTabs;
    fIndentWidthArg = indentWidth;
    fPrefsService.setIntPreference(IPreferencesService.CONFIGURATION_LEVEL, X10Constants.P_TABWIDTH, 4);
    fPrefsService.setIntPreference(IPreferencesService.CONFIGURATION_LEVEL, X10Constants.P_INDENTWIDTH, fIndentWidthArg);
    fPrefsService.setBooleanPreference(IPreferencesService.CONFIGURATION_LEVEL, X10Constants.P_SPACESFORTABS,
                                       fSpacesForTabsArg);
  }

  private DocumentCommand scanTextPopulateCommand(IDocument doc) {
    try {
      String text = doc.get();
      int markerPos = text.indexOf("// <<<");
      int markerLine = doc.getLineOfOffset(markerPos);
      int lineBegin = doc.getLineOffset(markerLine);

      if (lineBegin < 0) {
        lineBegin = 0;
        // } else if (lineBegin < markerPos && lineBegin > 0) {
        // lineBegin++; // move past newline
      }

      DocumentCommand cmd = new DocumentCommand() {
        @Override
        public String toString() {
          return "<cmd offset: " + offset + ", length: " + length + ", text: '" + text + "'>";
        }
      };

      cmd.offset = cmd.caretOffset = lineBegin;
      cmd.text = "\t";
      cmd.length = 0;
      cmd.doit = true;
      cmd.shiftsCaret = true;
      return cmd;
    } catch (BadLocationException e) {
      return new DocumentCommand() {
      };
    }
  }

  @SuppressWarnings("unused")
  private void checkForSpacesTabs(String line) {
    boolean spacesForTabs = fPrefsService.getBooleanPreference(X10Constants.P_SPACESFORTABS);
    // int tabWidth= fPrefsService.getIntPreference(X10Constants.P_TABWIDTH);

    for (int idx = 0; idx < line.length() && Character.isWhitespace(line.charAt(idx)); idx++) {
      Assert.assertTrue("Tab found when spaces-for-tabs set!", !spacesForTabs || (line.charAt(idx) == ' '));
    }
  }

  private void checkIndentation(String expect, String result) {
    if (!expect.equals(result)) {
      StringBuffer buf = new StringBuffer();
      buf.append("Indent-for-tab(");
      buf.append(fSpacesForTabsArg ? "spaces" : "tabs");
      buf.append(",");
      buf.append(fIndentWidthArg);
      buf.append(") command failed; expected '");

      for (int idx = 0; idx < expect.length() && Character.isWhitespace(expect.charAt(idx)); idx++) {
        buf.append((expect.charAt(idx) == ' ') ? "<spc>" : "<tab>");
      }
      buf.append("', but got '");
      for (int idx = 0; idx < result.length() && Character.isWhitespace(result.charAt(idx)); idx++) {
        buf.append((result.charAt(idx) == ' ') ? "<spc>" : "<tab>");
      }
      buf.append("'.");
      Assert.fail(buf.toString());
    }
  }

  private void indentTestHelper(String orig, String expect) {
    IDocument doc = new Document(orig);
    DocumentCommand cmd = scanTextPopulateCommand(doc);

    try {
      int lineNum = doc.getLineOfOffset(cmd.offset);

      fIndentStrategy.customizeDocumentCommand(doc, cmd);

      doc.replace(cmd.offset, cmd.length, cmd.text); // simulate execution of doc cmd

      String modLine = doc.get(doc.getLineOffset(lineNum), doc.getLineLength(lineNum));

      checkIndentation(expect, modLine);
      // disabled the following b/c current behavior reuses whatever indentation the "reference
      // line" uses, like the JDT, even if it includes tabs when spaces-for-tabs is set
      // checkForSpacesTabs(modLine);
    } catch (BadLocationException e) {
      Assert.fail("Exception caught during indent-for-tab command: " + e);
    }
  }

  @Test
  public void pkg() {
    String org = "   package foo.bar; // <<<\n";
    String exp = "package foo.bar; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void class1() {
    String org = "package foo.bar;\n" + "  public class Foo // <<<";
    String exp = "public class Foo // <<<";

    indentTestHelper(org, exp);
  }

  @Test
  public void class2() {
    String org = "package foo.bar;\n" + "\n" + "  public class Foo // <<<";
    String exp = "public class Foo // <<<";

    indentTestHelper(org, exp);
  }

  @Test
  public void class3() {
    String org = "package foo.bar;\n" + "    \n" + "  public class Foo // <<<";
    String exp = "public class Foo // <<<";

    indentTestHelper(org, exp);
  }

  @Test
  public void member1() {
    String org = "public class Bar {\n" + " public def foo() = true; // <<<\n" + "}";
    String expTab4 = "	public def foo() = true; // <<<\n";
    String expSpc4 = "    public def foo() = true; // <<<\n";
    String expTab3 = "   public def foo() = true; // <<<\n";
    String expSpc3 = "   public def foo() = true; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void member1a() {
    String org = "public class Bar {\n" + "      \n" + " public def foo() = true; // <<<\n" + "}";
    String expTab4 = "	public def foo() = true; // <<<\n";
    String expSpc4 = "    public def foo() = true; // <<<\n";
    String expTab3 = "   public def foo() = true; // <<<\n";
    String expSpc3 = "   public def foo() = true; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void member2() {
    String org = "public class Bar {\n" + "	  public def foo() = true;\n" + " public def bar() = 0; // <<<\n" + "}";
    String exp = "	  public def bar() = 0; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void member3() {
    String org = "public class Bar {\n" + "	  public def foo() = true;\n" + " var x: Int; // <<<\n" + "}";
    String exp = "	  var x: Int; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void member4() {
    String org = "public class Bar {\n" + "	  public def foo() = true;\n" + "\n" + " public def bar() = 0; // <<<\n" + "}";
    String exp = "	  public def bar() = 0; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void stmt1a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "  Console.OUT.println(\"Hi\"); // <<<\n" + "    }\n"
    + "}";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void stmt1b() {
    String org = "public class Bar {\n" + "    public def foo() = {\n"
    + "                 Console.OUT.println(\"Hi\"); // <<<\n" + "    }\n" + "}";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void stmt1c() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\"); // <<<\n"
    + "    }\n" + "}";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void stmt1d() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "\n"
    + "        Console.OUT.println(\"Hi\"); // <<<\n" + "    }\n" + "}";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void stmt1e() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "            \n"
    + "        Console.OUT.println(\"Hi\"); // <<<\n" + "    }\n" + "}";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void stmt2a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "    Console.OUT.println(\"Hey\"); // <<<\n" + "    }\n" + "}";
    String expTab4 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expTab3 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expSpc3 = "        Console.OUT.println(\"Hey\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void stmt2b() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "             Console.OUT.println(\"Hey\"); // <<<\n" + "    }\n" + "}";
    String expTab4 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expTab3 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expSpc3 = "        Console.OUT.println(\"Hey\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void stmt2c() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        Console.OUT.println(\"Hey\"); // <<<\n" + "    }\n" + "}";
    String expTab4 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expTab3 = "        Console.OUT.println(\"Hey\"); // <<<\n";
    String expSpc3 = "        Console.OUT.println(\"Hey\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void stmt3a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "           Console.OUT.println(\"Hi\");\n"
    + "        Console.OUT.println(\"Hey\"); // <<<\n" + "    }\n" + "}";
    String exp = "           Console.OUT.println(\"Hey\"); // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void stmt4a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n" + "\n"
    + "        Console.OUT.println(\"Hey\"); // <<<\n" + "    }\n" + "}";
    String exp = "        Console.OUT.println(\"Hey\"); // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void stmt4b() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n" + "    \n"
    + "        Console.OUT.println(\"Hey\"); // <<<\n" + "    }\n" + "}";
    String exp = "        Console.OUT.println(\"Hey\"); // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void forBody1a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        val a = Array.make[int](10, ((i): Point) => i);\n" + "        for(elt: int in a) {\n"
    + "     val x = elt * elt; // <<<\n" + "     val y = 1;\n" + "        }\n" + "    }\n" + "}";
    String expTab4 = "        	val x = elt * elt; // <<<\n";
    String expSpc4 = "            val x = elt * elt; // <<<\n";
    String expTab3 = "           val x = elt * elt; // <<<\n";
    String expSpc3 = "           val x = elt * elt; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void forBody1b() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        val a = Array.make[int](10, ((i): Point) => i);\n" + "        for(elt: int in a) {\n"
    + "               val x = elt * elt; // <<<\n" + "     val y = 1;\n" + "        }\n" + "    }\n" + "}";
    String expTab4 = "        	val x = elt * elt; // <<<\n";
    String expSpc4 = "            val x = elt * elt; // <<<\n";
    String expTab3 = "           val x = elt * elt; // <<<\n";
    String expSpc3 = "           val x = elt * elt; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void forBody1c() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        val a = Array.make[int](10, ((i): Point) => i);\n" + "        for(elt: int in a) {\n"
    + "            val x = elt * elt; // <<<\n" + "     val y = 1;\n" + "        }\n" + "    }\n" + "}";
    String expTab4 = "        	val x = elt * elt; // <<<\n";
    String expSpc4 = "            val x = elt * elt; // <<<\n";
    String expTab3 = "           val x = elt * elt; // <<<\n";
    String expSpc3 = "           val x = elt * elt; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void forBody2a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        val a = Array.make[int](10, ((i): Point) => i);\n" + "        for(elt: int in a) {\n"
    + "               val x = elt * elt;\n" + "     val y = 1; // <<<\n" + "        }\n" + "    }\n" + "}";
    String expTab4 = "               val y = 1; // <<<\n";
    String expSpc4 = "               val y = 1; // <<<\n";
    String expTab3 = "               val y = 1; // <<<\n";
    String expSpc3 = "               val y = 1; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void forBody2b() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        val a = Array.make[int](10, ((i): Point) => i);\n" + "        for(elt: int in a) {\n"
    + "               val x = elt * elt;\n" + "     val y = 1; // <<<\n" + "        }\n" + "    }\n" + "}";
    String exp = "               val y = 1; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void forBody2c() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        val a = Array.make[int](10, ((i): Point) => i);\n" + "        for(elt: int in a) {\n"
    + "               val x = elt * elt;\n" + "               val y = 1; // <<<\n" + "        }\n" + "    }\n"
    + "}";
    String exp = "               val y = 1; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void asyncBody1a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        async {\n" + "     val x = 0; // <<<\n" + "     val y = 1;\n" + "        }\n" + "    }\n" + "}";
    String expTab4 = "        	val x = 0; // <<<\n";
    String expSpc4 = "            val x = 0; // <<<\n";
    String expTab3 = "           val x = 0; // <<<\n";
    String expSpc3 = "           val x = 0; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void asyncBody1b() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        async {\n" + "                  val x = 0; // <<<\n" + "     val y = 1;\n" + "        }\n"
    + "    }\n" + "}";
    String expTab4 = "        	val x = 0; // <<<\n";
    String expSpc4 = "            val x = 0; // <<<\n";
    String expTab3 = "           val x = 0; // <<<\n";
    String expSpc3 = "           val x = 0; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void asyncBody1c() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        async {\n" + "            val x = 0; // <<<\n" + "     val y = 1;\n" + "        }\n" + "    }\n"
    + "}";
    String expTab4 = "        	val x = 0; // <<<\n";
    String expSpc4 = "            val x = 0; // <<<\n";
    String expTab3 = "           val x = 0; // <<<\n";
    String expSpc3 = "           val x = 0; // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void asyncBody2a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        async {\n" + "            val x = 0;\n" + "     val y = 1; // <<<\n" + "        }\n" + "    }\n"
    + "}";
    String exp = "            val y = 1; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void asyncBody2b() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        async {\n" + "            val x = 0;\n" + "                 val y = 1; // <<<\n" + "        }\n"
    + "    }\n" + "}";
    String exp = "            val y = 1; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void asyncBody2c() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        async {\n" + "            val x = 0;\n" + "            val y = 1; // <<<\n" + "        }\n"
    + "    }\n" + "}";
    String exp = "            val y = 1; // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void closeBrkt1() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        Console.OUT.println(\"Hey\");\n" + "  } // <<<\n" + "}";
    String exp = "    } // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void closeBrkt2() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        Console.OUT.println(\"Hey\");\n" + "    }\n" + "  } // <<<";
    String exp = "} // <<<";

    indentTestHelper(org, exp);
  }

  @Test
  public void comment1() {
    String org = "public class Bar {\n" + "    public def foo() = { // say something funny\n"
    + "  Console.OUT.println(\"Hi\"); // <<<\n" + "        Console.OUT.println(\"Hey\");\n" + "    }\n" + "  }";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void comment2() {
    String org = "public class Bar {\n" + "    public def foo() = { // say something { funny }\n"
    + "  Console.OUT.println(\"Hi\"); // <<<\n" + "        Console.OUT.println(\"Hey\");\n" + "    }\n" + "  }";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void comment3() {
    String org = "public class Bar {\n" + "    public def foo() = { // say something { not so funny\n"
    + "  Console.OUT.println(\"Hi\"); // <<<\n" + "        Console.OUT.println(\"Hey\");\n" + "    }\n" + "  }";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void comment4() {
    String org = "public class Bar {\n" + "    public def foo() = { // say something not so funny }\n"
    + "  Console.OUT.println(\"Hi\"); // <<<\n" + "        Console.OUT.println(\"Hey\");\n" + "    }\n" + "  }";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void comment5a() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "  // bletch\n"
    + "  Console.OUT.println(\"Hi\"); // <<<\n" + "    }\n" + "  }";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void comment5b() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "      // bletch\n"
    + "  Console.OUT.println(\"Hi\"); // <<<\n" + "    }\n" + "  }";
    String expTab4 = "    	Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc4 = "        Console.OUT.println(\"Hi\"); // <<<\n";
    String expTab3 = "       Console.OUT.println(\"Hi\"); // <<<\n";
    String expSpc3 = "       Console.OUT.println(\"Hi\"); // <<<\n";

    indentTestHelper(org, (fIndentWidthArg == 4) ? (fSpacesForTabsArg ? expSpc4 : expTab4) : (fSpacesForTabsArg ? expSpc3
                                                                                                                : expTab3));
  }

  @Test
  public void blockComment1() {
    String org = "public class Bar {\n" + "    public def foo() = {\n" + "        Console.OUT.println(\"Hi\");\n"
    + "        /**\n" + "  * // <<<\n" + "         */\n" + "        Console.OUT.println(\"Hey\");\n" + "    }\n"
    + "  }";
    String exp = "         * // <<<\n";

    indentTestHelper(org, exp);
  }

  @Test
  public void typeArgs1() { // Test for RTC bug #835
    String org = "public class Bar[T]\n" + "    { // <<<\n" + "    public def foo() { }\n" + "  }";
    String exp = "{ // <<<\n";

    indentTestHelper(org, exp);
  }
}
