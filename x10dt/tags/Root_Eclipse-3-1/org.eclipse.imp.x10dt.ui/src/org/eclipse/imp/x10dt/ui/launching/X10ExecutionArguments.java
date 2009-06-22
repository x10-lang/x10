package org.eclipse.imp.x10dt.ui.launching;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jdt.launching.ExecutionArguments;

/**
 * Like the base Java ExecutionArguments class, but adds support for X10 runtime arguments.
 * @author rfuhrer
 */
public class X10ExecutionArguments extends ExecutionArguments {
    private final String fRuntimeArgs;

    public X10ExecutionArguments(String vmArgs, String runtimeArgs, String programArgs) {
	super(vmArgs, programArgs);
	fRuntimeArgs= runtimeArgs;
    }

    public String[] getRuntimeArgumentsArray() {
        return DebugPlugin.parseArguments(fRuntimeArgs);
    }
}
