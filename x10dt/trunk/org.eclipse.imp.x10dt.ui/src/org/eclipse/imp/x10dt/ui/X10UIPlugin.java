package x10.uide;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class X10UIPlugin extends AbstractUIPlugin {
    public static final String PLUGIN_ID= "x10.uide";

    private static X10UIPlugin sInstance;

    public X10UIPlugin() {
	super();
	sInstance= this;
    }

    public static X10UIPlugin getInstance() {
	return sInstance;
    }
}
