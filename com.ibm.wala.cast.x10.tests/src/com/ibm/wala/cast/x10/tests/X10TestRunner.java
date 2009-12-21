package com.ibm.wala.cast.x10.tests;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * Test runner for running some test cases in X10 tests suite. It will mainly collect X10 files from a given root directory,
 * with the help of some filters.
 * 
 * @author egeay
 */
public final class X10TestRunner extends Suite {
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public static @interface X10Tests {
    
    public String exclude_filter() default "";
    
    public String include_filter() default "";
    
    public boolean recursive() default false;
    
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface TestRootPath {
    
    public String path();
    
  }
  
  public X10TestRunner(final Class<?> klass) throws Throwable {
    super(klass, Collections.<Runner>emptyList());
    
    final TestRootPath testRootPath = klass.getAnnotation(TestRootPath.class);
    if (testRootPath == null) {
      throw new InitializationError(String.format("Class '%s' must have a TestRootPath annotation", klass.getName()));
    }
    
    this.fRunners = new ArrayList<Runner>();
    
    final Map<String, File> nameToFile = new HashMap<String, File>();
    final File root = new File(testRootPath.path());
    for (final File file : root.listFiles()) {
      nameToFile.put(file.getName(), file);
    }
    for (final FrameworkMethod method : getX10TestsMethods(getTestClass())) {
      final File file = nameToFile.get(method.getName());
      if (file != null) {
        final X10Tests x10TestsAnnotation = method.getAnnotation(X10Tests.class);
        final String[] includeFilterStrings = getFilters(x10TestsAnnotation.include_filter());
        final String[] excludeFilterStrings = getFilters(x10TestsAnnotation.exclude_filter());
        final boolean isRecursive = x10TestsAnnotation.recursive();
        
        searchForTests(file, toPatterns(includeFilterStrings), toPatterns(excludeFilterStrings), method, isRecursive);
      }
    }
    if (this.fRunners.isEmpty()) {
      throw new InitializationError(String.format("We could not find any X10 files to test in '%s'", klass.getName()));
    }
  }
  
  // --- Overridden methods
  
  protected List<Runner> getChildren() {
    return this.fRunners;
  }
  
  // --- Private code
  
  private String[] getFilters(final String filterString) {
    return filterString.isEmpty() ? new String[0] : filterString.split(REGEX_SEPARATOR);
  }
  
  private boolean isIncludedByPatterns(final Pattern[] patterns, final String element) {
    if (patterns.length == 0) {
      return true;
    }
    for (final Pattern pattern : patterns) {
      if (pattern.matcher(element).matches()) {
        return true;
      }
    }
    return false;
  }
  
  private void searchForTests(final File dir, final Pattern[] includePatterns, final Pattern[] excludePatterns,
                              final FrameworkMethod method, final boolean isRecursive) throws InitializationError {
    for (final File testedFile : dir.listFiles(new X10FileFilter(includePatterns, isRecursive))) {
      if (testedFile.isDirectory()) {
        if (isRecursive) {
          searchForTests(testedFile, includePatterns, excludePatterns, method, isRecursive);
        }
      } else if (! isIncludedByPatterns(excludePatterns, testedFile.getAbsolutePath().replace('\\', '/'))) {
        this.fRunners.add(new TestClassRunnerForX10Tests(getTestClass().getJavaClass(), testedFile));
      }
    }
  }
  
  private List<FrameworkMethod> getX10TestsMethods(final TestClass testClass) {
    final List<FrameworkMethod> allMethods = testClass.getAnnotatedMethods(X10Tests.class);
    final List<FrameworkMethod> methods = new ArrayList<FrameworkMethod>(allMethods.size());
    for (final FrameworkMethod frameworkMethod : allMethods) {
      final int modifiers = frameworkMethod.getMethod().getModifiers();
      if (Modifier.isPublic(modifiers)) {
        methods.add(frameworkMethod);
      }
    }
    return methods;
  }
  
  private Pattern[] toPatterns(final String[] filters) {
    final Pattern[] patterns = new Pattern[filters.length];
    int i = -1;
    for (final String filterStr : filters) {
      patterns[++i] = Pattern.compile(filterStr);
    }
    return patterns;
  }
  
  // --- Private classes
  
  private static final class TestClassRunnerForX10Tests extends BlockJUnit4ClassRunner {

    TestClassRunnerForX10Tests(final Class<?> klass, final File testedFile) throws InitializationError {
      super(klass);
      this.fTestedFile = testedFile;
    }
    
    // --- Overridden methods
    
    protected String getName() {
      return this.fTestedFile.getParentFile().getName();
    }
    
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
      return new X10InvokeMethod(method, test, this.fTestedFile);
    }
    
    protected String testName(final FrameworkMethod method) {
      return this.fTestedFile.getName();
    }
    
    protected void validateTestMethods(final List<Throwable> errors) {
      for (final FrameworkMethod frameworkMethod : getTestClass().getAnnotatedMethods(Test.class)) {
        frameworkMethod.validatePublicVoid(false, errors);
        final Class<?>[] parameterTypes = frameworkMethod.getMethod().getParameterTypes();
        if (parameterTypes.length == 1) {
          if (File.class != parameterTypes[0]) {
            errors.add(new Exception(String.format("Parameter type for method '%s' must be java.io.File", 
                                                   frameworkMethod.getName())));
          }
        } else {
          errors.add(new Exception(String.format("Method '%s' should have one parameter of type java.io.File", 
                                                 frameworkMethod.getName())));
        }
      }
    }
    
    // --- Fields
    
    private final File fTestedFile;
    
  }
  
  private final class X10FileFilter implements FileFilter {
    
    X10FileFilter(final Pattern[] includedPatterns, final boolean isRecursive) {
      this.fPatterns = includedPatterns;
      this.fIsRecursive = isRecursive;
    }

    // --- Interface methods implementation
    
    public boolean accept(final File file) {
      if (file.isDirectory()) {
        return this.fIsRecursive;
      }
      if (file.getName().endsWith(".x10")) {
        return isIncludedByPatterns(this.fPatterns, file.getAbsolutePath().replace('\\', '/'));
      } else {
        return false;
      }
    }
    
    // --- Fields
    
    private final Pattern[] fPatterns;
    
    private final boolean fIsRecursive;
    
  }
  
  private static final class X10InvokeMethod extends Statement {
    
    X10InvokeMethod(final FrameworkMethod frameworkMethod, final Object test, final File testedFile) {
      this.fFrameworkMethod = frameworkMethod;
      this.fTest = test;
      this.fTestedFile = testedFile;
    }

    // --- Abstract methods implementation
    
    public void evaluate() throws Throwable {
      this.fFrameworkMethod.invokeExplosively(this.fTest, this.fTestedFile);
    }
    
    // --- Fields
    
    private final FrameworkMethod fFrameworkMethod;
    
    private final Object fTest;
    
    private final File fTestedFile;
    
  }
  
  // --- Fields
  
  private final List<Runner> fRunners;
  
  
  private static final String REGEX_SEPARATOR = ","; //$NON-NLS-1$
  
}
