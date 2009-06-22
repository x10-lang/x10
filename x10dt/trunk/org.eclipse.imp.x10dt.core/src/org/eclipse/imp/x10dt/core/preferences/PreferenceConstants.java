package com.ibm.watson.safari.x10.preferences;

/**
 * Holds the ID's of the various preferences for the X10 plugin.
 */
public class PreferenceConstants {
    /**
     * The path to the x10.common directory
     */
    public static final String P_X10COMMON_PATH= "commonPath";

    /**
     * The path to the X10 configuration file (e.g. standard.cfg)
     */
    public static final String P_X10CONFIG_FILE= "configFile";

    /**
     * true if the builder and various other entities should emit diagnostic
     * messages to the plugin log while running
     */
    public static final String P_EMIT_MESSAGES= "emitMessages";

    /**
     * true if the X10 builder should automatically add x10.runtime/classes
     * to the Polyglot build path.
     * @deprecated This has been superceded by the "New X10 Project" wizard
     * adding explicit entries to the classpath
     */
    public static final String P_AUTO_ADD_RUNTIME= "autoAddRuntimme";

    /**
     * The path to the compiler template directory (used by X10PrettyPrinterVisitor).
     * A mirror of the COMPILER_FRAGMENT_DATA_DIRECTORY setting in the X10
     * configuration file (e.g. standard.cfg).
     */
    public static final String P_COMPILER_DATA_DIR= "compilerDataDir";

    /**
     * The runtime sampling frequency.
     * A mirror of the SAMPLING_FREQUENCY setting in the X10
     * configuration file (e.g. standard.cfg).
     */
    public static final String P_SAMPLING_FREQ= "samplingFreq";

    /**
     * 
     * A mirror of the STATISTICS_DISABLE setting in the X10
     * configuration file (e.g. standard.cfg).
     */
    public static final String P_STATS_DISABLE= "statsDisable";
}
