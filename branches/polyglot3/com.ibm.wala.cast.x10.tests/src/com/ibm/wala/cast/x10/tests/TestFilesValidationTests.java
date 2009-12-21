package com.ibm.wala.cast.x10.tests;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import polyglot.frontend.Compiler;
import polyglot.frontend.FileResource;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.ExtensionInfo;

@RunWith(value=Parameterized.class)
public class TestFilesValidationTests {
  
  public TestFilesValidationTests(final Boolean expected, final File x10File) throws IOException {
    this.fX10File = x10File;
    this.fSource = new FileSource(new FileResource(x10File));
  }
  
  @Parameters public static Collection<?> data() {
    final Collection<File> x10Files = new ArrayList<File>(50);
    collectX10Files(x10Files, new File("../x10.tests/examples/Constructs/Async"), new X10FileFilter());
    final Collection<Object[]> data = new ArrayList<Object[]>(x10Files.size());
    for (final File x10File : x10Files) {
      if (x10File.getName().startsWith("AsyncNext")) {
        data.add(new Object[] { Boolean.TRUE, x10File });
      }
    }
//    data.add(new Object[] { Boolean.TRUE, new File("../x10.tests/examples/x10lib/harness/x10Test.x10")});
    return data;
  }

  @Test public void testCompilation() {
    final ExtensionInfo extInfo = new x10cpp.ExtensionInfo();

    final File x10FilesLoc = new File("../x10.runtime/src-x10");
    final Options options = extInfo.getOptions();
    options.assertions = true;
    options.compile_command_line_only = true;
    options.classpath = x10FilesLoc.getAbsolutePath();
    options.post_compiler = null;
    options.source_path = Arrays.asList(new File("../x10.tests/examples/x10lib"), x10FilesLoc);

    final Collection<Source> sources = new ArrayList<Source>(1);
    sources.add(this.fSource);
    final TestErrorQueue errorQueue = new TestErrorQueue();
    Compiler compiler = new Compiler(extInfo, errorQueue);
    Globals.initialize(compiler);

    final boolean result = compiler.compile(sources);
    if (! result) {
      Assert.assertTrue(getErrorMessage(errorQueue.getErrors()), false);
    }
  }
  
  public static void main(final String[] args) throws IOException {
    final File x10File = new File(args[0]);
    final TestFilesValidationTests clazz = new TestFilesValidationTests(Boolean.TRUE, x10File);
    clazz.testCompilation();
  }
  
  // --- Private code
  
  private static void collectX10Files(final Collection<File> x10Files, final File dir, final FileFilter filter) {
    for (final File file : dir.listFiles(filter)) {
      if (file.isDirectory()) {
        collectX10Files(x10Files, file, filter);
      } else {
        x10Files.add(file);
      }
    }
  }
  
  private String getErrorMessage(final Collection<ErrorInfo> errors) {
    if (errors.isEmpty()) {
      return "";
    } else {
      final StringBuilder sb = new StringBuilder();
      final String path = this.fX10File.getAbsolutePath().substring(new File("testSrc").getAbsolutePath().length() + 1);
      sb.append("In file " + path + '\n');
      for (final ErrorInfo errorInfo : errors) {
        sb.append(errorInfo.getErrorString());
        if (errorInfo.getPosition() != null) {
          sb.append('(').append(errorInfo.getPosition().line()).append(')');
        }
        sb.append(": ").append(errorInfo.getMessage()).append('\n');
      }
      return sb.toString();
    }
  }
  
  // --- Private classes
  
  private static final class X10FileFilter implements FileFilter {

    // --- Interface methods implementation
    
    public boolean accept(final File file) {
      return file.isDirectory() || file.getName().endsWith(".x10");
    }
    
  }
  
  private static final class TestErrorQueue implements ErrorQueue {
    
    // --- Interface methods implementation
    
    public void enqueue(final int type, final String message) {
      enqueue(new ErrorInfo(type, message, null));
    }

    public void enqueue(final int type, final String message, final Position position) {
      enqueue(new ErrorInfo(type, message, position));
    }

    public void enqueue(final ErrorInfo errorInfo) {
      this.fErrors.add(errorInfo);
    }

    public int errorCount() {
      return this.fErrors.size();
    }

    public void flush() {
    }

    public boolean hasErrors() {
      return false;
    }
    
    // --- Internal services
    
    Collection<ErrorInfo> getErrors() {
      return this.fErrors;
    }
    
    // --- Fields
    
    private final Collection<ErrorInfo> fErrors = new LinkedList<ErrorInfo>();
    
  }
  
  // --- Fields
  
  private final Source fSource;
  
  private final File fX10File;
  
}
