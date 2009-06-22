package x10.refactorings;

import java.io.PrintStream;

import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.utils.ConsoleUtil;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;

public class X10RefactoringPlugin extends PluginBase {
    public static final String kPluginID= "x10.refactoring"; // must match plugin ID in MANIFEST.MF

    private static final String CONSOLE_NAME= "X10 Refactoring";

    private static X10RefactoringPlugin sPlugin;

    public static X10RefactoringPlugin getInstance() {
        return sPlugin;
    }

    private PrintStream sConsoleStream;

    public X10RefactoringPlugin() {
        sPlugin= this;
    }

    @Override
    public String getID() {
        return kPluginID;
    }

    @Override
    public String getLanguageID() {
        return X10DTCorePlugin.kLanguageName;
    }

    public PrintStream getConsoleStream() {
        if (sConsoleStream == null) {
            sConsoleStream= ConsoleUtil.findConsoleStream(CONSOLE_NAME);
        }
        return sConsoleStream;
    }
}
