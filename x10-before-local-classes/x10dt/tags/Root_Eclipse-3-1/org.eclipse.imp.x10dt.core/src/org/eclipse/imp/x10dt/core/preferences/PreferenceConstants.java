package org.eclipse.imp.x10dt.core.preferences;

/**
 * Holds the ID's of the various preferences for the X10 plugin.
 */
public class PreferenceConstants {
    /**
     * The path to the X10 configuration file (e.g. standard.cfg)
     */
    public static final String P_X10CONFIG_FILE= "compilerConfigurationFile";

    /**
     * The name of an X10 configuration (e.g. "standard" or "multivm").<br>
     * There should be an X10 configuration file in the "etc" folder of the x10.common
     * plugin whose name is the value of this preference, with a ".cfg" suffix. E.g.,
     * if this preference is "standard", then the file "x10.common/etc/standard.cfg"
     * must exist.
     */
    public static final String P_X10CONFIG_NAME= "compilerConfiguration";

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
