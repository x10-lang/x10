/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.impl.java;

import java.io.PrintStream;
import java.util.StringTokenizer;

import x10.config.ConfigurationError;

/**
 * Code for outputting usage information for the X10 runtime.
 */
public abstract class Usage {
	/**
	 * The name of the language this runtime represents.
	 */
	public static final String LANGUAGE = "x10";

	/**
	 * Print usage information
	 */
	public static void usage(PrintStream out, ConfigurationError err) {
		if (err != null)
			out.println("Error: "+err.getMessage());
		out.println("Usage: " + LANGUAGE + " [options] " +
				"<main-class> [arg0 arg1 ...]");
		out.println("where [options] includes:");
		String[][] options = Configuration.options();
		for (int i = 0; i < options.length; i++) {
			String[] optinfo = options[i];
			String optflag = "-"+optinfo[0]+"="+optinfo[1];
			String optdesc = optinfo[2]+"(default = "+optinfo[3]+")";
			usageForFlag(out, optflag, optdesc);
		}
	}

	/* The following code was borrowed from Polyglot's Options.java */
    /**
     * The maximum width of a line when printing usage information. Used
     * by <code>usageForFlag</code> and <code>usageSubsection</code>.
     */
    protected static final int USAGE_SCREEN_WIDTH = 76;

    /**
     * The number of spaces from the left that the descriptions for flags will
	 * be displayed. Used by <code>usageForFlag</code>.
     */
    private static final int USAGE_FLAG_WIDTH = 34;

    /**
     * The number of spaces from the left that the flag names will be
	 * indented. Used by <code>usageForFlag</code>.
     */
    private static final int USAGE_FLAG_INDENT = 4;

    /**
     * Utility method to print a number of spaces to a PrintStream.
     * @param out output PrintStream
     * @param n number of spaces to print.
     */
    protected static void printSpaces(PrintStream out, int n) {
        while (n-- > 0) {
            out.print(' ');
        }
    }

    /**
     * Output a flag and a description of its usage in a nice format.
     *
     * @param out output PrintStream
     * @param flag
     * @param description description of the flag.
     */
    protected static void usageForFlag(PrintStream out, String flag, String description) {
		printSpaces(out, USAGE_FLAG_INDENT);
        out.print(flag);
        // cur is where the cursor is on the screen.
        int cur = flag.length() + USAGE_FLAG_INDENT;

        // print space to get up to indentation level
        if (cur < USAGE_FLAG_WIDTH) {
            printSpaces(out, USAGE_FLAG_WIDTH - cur);
        }
        else {
            // the flag is long. Get a new line before printing the
            // description.
            out.println();
            printSpaces(out, USAGE_FLAG_WIDTH);
        }
        cur = USAGE_FLAG_WIDTH;

        // break up the description.
        StringTokenizer st = new StringTokenizer(description);
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (cur + s.length() > USAGE_SCREEN_WIDTH) {
                out.println();
                printSpaces(out, USAGE_FLAG_WIDTH);
                cur = USAGE_FLAG_WIDTH;
            }
            out.print(s);
            cur += s.length();
            if (st.hasMoreTokens()) {
                if (cur + 1 > USAGE_SCREEN_WIDTH) {
                    out.println();
                    printSpaces(out, USAGE_FLAG_WIDTH);
                    cur = USAGE_FLAG_WIDTH;
                }
                else {
                    out.print(" ");
                    cur++;
                }
            }
        }
        out.println();
    }
}
