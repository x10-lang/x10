package x10.refactorings;

import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;

public class X10RefactoringPlugin extends PluginBase {
    public static final String kPluginID= "x10.refactoring"; // must match plugin ID in MANIFEST.MF

    private static X10RefactoringPlugin sPlugin;

    public static X10RefactoringPlugin getInstance() {
        return sPlugin;
    }

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
}
