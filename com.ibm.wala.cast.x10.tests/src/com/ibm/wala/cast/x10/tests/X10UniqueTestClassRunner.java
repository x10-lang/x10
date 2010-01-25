package com.ibm.wala.cast.x10.tests;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.ibm.wala.cast.x10.tests.X10TestRunner.TestRootPath;
import com.ibm.wala.cast.x10.tests.X10TestRunner.X10InvokeMethod;


public final class X10UniqueTestClassRunner extends BlockJUnit4ClassRunner {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public static @interface X10UniqueTest {
    
    public String path() default "";
        
  }
  
  public X10UniqueTestClassRunner(final Class<?> klass) throws InitializationError {
    super(klass);
    
    final TestRootPath testRootPath = klass.getAnnotation(TestRootPath.class);
    if (testRootPath == null) {
      throw new InitializationError(String.format("Class '%s' must have a TestRootPath annotation", klass.getName()));
    }
    
    this.fRootFile = new File(testRootPath.path());
    if (! this.fRootFile.exists()) {
      throw new InitializationError(String.format("Root file '%s' does not exist.", this.fRootFile.getAbsolutePath()));
    }
  }
  
  // --- Overridden methods
  
  protected List<FrameworkMethod> computeTestMethods() {
    return getTestClass().getAnnotatedMethods(X10UniqueTest.class);
  }
  
  protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
    final X10UniqueTest x10UniqueTest = method.getAnnotation(X10UniqueTest.class);
    final File testedFile = new File(this.fRootFile, x10UniqueTest.path());
    if (testedFile.exists()) {
      return new X10InvokeMethod(method, test, testedFile);
    } else {
      throw new AssertionError(String.format("Test file '%s' does not exist.", testedFile.getAbsolutePath()));
    }
  }
  
  protected void collectInitializationErrors(final List<Throwable> errors) {
    // Valid by default.
  }
  
  protected String testName(final FrameworkMethod method) {
    final String path = method.getAnnotation(X10UniqueTest.class).path();
    final StringBuilder sb = new StringBuilder();
    sb.append(method.getName()).append(" for ").append(path.substring(path.lastIndexOf('/') + 1));
    return sb.toString();
  }
  
  // --- Fields
  
  private final File fRootFile;

}
