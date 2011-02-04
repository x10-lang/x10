/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10;

import x10.config.ConfigurationError;
import x10.config.OptionError;

/**
 * This class provides the configuration for the X10 compiler.
 * The configuration is a set of values that can be used to
 * configure the compiler, for example in order to tune performance
 * of the generated code.
 *
 * @see x10.config.Configuration
 *
 * @author Christian Grothoff
 * @author Igor Peshansky
 */
public final class Configuration extends x10.config.Configuration {

    public String MAIN_CLASS = null;
    private static final String MAIN_CLASS_desc = "The class whose main() will be invoked";

    public boolean CHECK_INVARIANTS = false;
    private static final String CHECK_INVARIANTS_desc = "Check AST invariants such as position containment, existence of xxxInstance(), etc";

    public boolean ONLY_TYPE_CHECKING = false;
    private static final String ONLY_TYPE_CHECKING_desc = "Do only type-checking, without optimizations or code generation";

    public boolean OPTIMIZE = false;
    private static final String OPTIMIZE_desc = "Generate optimized code";

    public boolean DEBUG = false;
    private static final String DEBUG_desc = "Generate debug information";

    public boolean NO_TRACES = false;
    private static final String NO_TRACES_desc = "Disable traces";

    public boolean NO_CHECKS = false;
    private static final String NO_CHECKS_desc = "Disable generation of all null, bounds, and place checks";

    public boolean LOOP_OPTIMIZATIONS = true;
    private static final String LOOP_OPTIMIZATIONS_desc = "Optimize rectangular region iteration";

    public boolean EXPERIMENTAL = false;
    private static final String EXPERIMENTAL_desc = "Enable experimental optimizations";

    public boolean ELIMINATE_DEAD_VARIABLES = false;
    private static final String ELIMINATE_DEAD_VARIABLES_desc = "Get rid of unused local variables";

    public boolean INLINE_CONSTANTS = true;
    private static final String INLINE_CONSTANTS_desc = "Enable inlining of command-line flag values";

    public boolean INLINE_METHODS = true;
    private static final String INLINE_METHODS_desc = "Perform method inlining optimizations";
    
    // Note: this has more limited affect than CLOSURE_INLINING which also must be true for the Inliner to inline closure calls
    public boolean INLINE_CLOSURES = true;
    private static final String INLINE_CLOSURES_desc = "Enable inlining of closure literal calls";

    public boolean INLINE_METHODS_IMPLICIT = false;
    private static final String INLINE_METHODS_IMPLICIT_desc = "Inline methods that don't make many calls";

    public boolean ALLOW_STATEMENT_EXPRESSIONS = true;
    private static final String ALLOW_STATEMENT_EXPRESSIONS_desc = "Allow statement expressions in generated code";

    public boolean FLATTEN_EXPRESSIONS = false;
    private static final String FLATTEN_EXPRESSIONS_desc = "Flatten all expressions";

    public String PLUGINS = "";
    private static final String PLUGINS_desc = "Comma-separated list of compiler plugins to run.";

    public String PLUGIN_COMPILER = "";
    private static final String PLUGIN_COMPILER_desc = "Javac-like compiler to use to compile plugins";

    public boolean WORK_STEALING = false;
    private static final String WORK_STEALING_desc = "Code generation for work-stealing scheduling";

    public boolean WALA = false;
    private static final String WALA_desc = "Produce WALA IR from X10 sources";

    public boolean WALADEBUG = false;
    private static final String WALADEBUG_desc = "Produce WALA call graph from X10 sources";

    public boolean VERBOSE_CALLS = false;
    private static final String VERBOSE_CALLS_desc = "Print details of casts introduced for dynamically-checked calls.";

    public boolean STATIC_CALLS = false;
    private static final String STATIC_CALLS_desc = "Treat dynamically-checked calls as errors.";

    public boolean FINISH_ASYNCS = false;
    private static final String FINISH_ASYNCS_desc = "finish-async analysis.";

    public boolean MULTI_NODE = true;
    private static final String MULTI_NODE_desc = "(Managed X10) each place is hosted by different Java VM";

    /**
     * Parses one argument from the command line.  This allows the user
     * to specify options also on the command line (in addition to the
     * configuration file and the defaults).
     *
     * @param arg the current argument, e.g., -STATISTICS_DISABLE=all
     * @throws OptionError if the argument is not recognized
     * @throws ConfigurationError if there was a problem processing the argument
     */
    public void parseArgument(String arg) throws OptionError, ConfigurationError {
        parseArgument(this, Configuration.class, arg);
    }

    public Object get(String key) throws ConfigurationError, OptionError {
        return get(this, Configuration.class, key);
    }

    /**
     * Return an array of (option,description) pairs.
     */
    public String[][] options() {
        return options(this, Configuration.class);
    }

    public Configuration() {
        super(Configuration.class);
    }

    public Configuration(String cfg) {
        super(Configuration.class, cfg);
    }
}
