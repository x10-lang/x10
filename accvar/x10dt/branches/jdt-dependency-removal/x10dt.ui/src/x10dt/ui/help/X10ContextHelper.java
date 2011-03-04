package x10dt.ui.help;

import java.util.HashMap;
import java.util.Map;

import lpg.runtime.IToken;

import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.IDocumentationProvider;
import org.eclipse.imp.services.IHelpService;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IWorkbenchPart;

import x10dt.core.X10Util;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.editor.X10DocProvider;
import x10dt.ui.parser.ParseController;

/**
 * Provide information for context help (F1 "dynamic help")<br>
 * Usually defers to X10DocProvider, but has some specific info especially for X10 keywords here.
 *
 */
public class X10ContextHelper implements IHelpService {
	private static final boolean traceOn=false;
    private final static Map<String,String> sKeywordHelp= new HashMap<String, String>();

    {
        sKeywordHelp.put("static", "<b>static</b> \n\nIdentifies the given member as a class member (cf. instance member).\n");
        sKeywordHelp.put("private", "<b>private</b> \n\nSpecifies that the given member is only visible to members of the same class.\n");
        sKeywordHelp.put("async", "<b>async</b> \n\nruns the child statement block in parallel with the statement(s) following the async.\n");
        sKeywordHelp.put("final", "<b>final</b> variables whose value may not be changed after initialization. \n");
        sKeywordHelp.put("future", "<b>future</b> \n\narranges for the child expression to be lazily evaluated; see 'force'.\n");
        sKeywordHelp.put("finish", "<b>finish</b> \n\nruns the child statement block, but wait for all asyncs to terminate.\n");
        sKeywordHelp.put("foreach", "<b>foreach</b> \n\nruns the child statement block asynchonously for each point in a region.\n");
        sKeywordHelp.put("ateach", "<b>ateach</b> \n\nruns the child statement block asynchronously for each point in distribution in its place.\n");
        sKeywordHelp.put("atomic", "<b>atomic</b> \n\nruns the child statement block atomically, executed in a single step while other activities are suspended.\n");
        sKeywordHelp.put("next", "<b>next</b> \n\nsuspend till all clocks that the current activity is registered with can advance.\n");
        sKeywordHelp.put("when", "<b>when</b> \n\nruns the child statement block atomically, executed in a single step while other activities are suspended.\n");
        sKeywordHelp.put("region", "<b>region</b> \n\ncollection of index points.\n");
        sKeywordHelp.put("dist", "<b>dist</b> \n\nmapping from region to places.\n");
        sKeywordHelp.put("point", "<b>point</b> \n\nan element of an n-dimensional Cartesian space (n>=1) with integer-valued coordinates.\n");
        sKeywordHelp.put("force", "<b>force</b> \n\nblock until future has been computed.\n");
        sKeywordHelp.put("extern", "<b>extern</b> \n\nlightweight interface to native code.\n");
        sKeywordHelp.put("clock", "<b>clock</b> \n\ndeterminate and deadlock-free between a dynamically varying number of activities.\n");
        sKeywordHelp.put("clocked", "<b>clocked</b> \n\ntransmit clock to child b.\n");
        sKeywordHelp.put("nullable", "<b>nullable</b> \n\nused to add null to a type and value types.\n");
       
    }


    public IContext getContext(IWorkbenchPart part) {
        return HelpSystem.getContext("x10EditorContext");
    }
	
    /**
     * Get the info for this entity via passthru to the DocumentationProvider
     * @see IDocumentationProvider
     * @see X10DocProvider
     * 
     * Note: for context help (F1 dynamic help in Help view) we may want some trivial information 
     * such as "This is a com.foo.Thingy"  that is too annoying for other usages e.g. hover text.
     * Consider returning something if docProvider doesn't.
     */
    public String getHelp(Object target, IParseController parseController) {
		Language lang = parseController.getLanguage();
		IDocumentationProvider dp = ServiceFactory.getInstance().getDocumentationProvider(lang);
		String doc = dp.getDocumentation(target, parseController);
		return doc;
	}

    public String getHelp(IRegion target, IParseController parseController) {
        ParseController pc= (ParseController) parseController;
        // PORT1.7 --   token is (now) calculated from parseController and IRegion's offset
        IToken token = X10Util.getLeftToken(target, parseController);

        if (token != null) {
            String tokenStr= token.toString();

            if (sKeywordHelp.containsKey(tokenStr)) {
                return sKeywordHelp.get(tokenStr);
            }
        }
        ISourcePositionLocator nodeLocator= parseController.getSourcePositionLocator();
        Object node= nodeLocator.findNode(parseController.getCurrentAst(), target.getOffset());
        if (node != null) {
            return getHelp(node, parseController);
        }
        return null;
    }

    public String getContextId(String baseContextId) {
        return X10DTUIPlugin.PLUGIN_ID + ".x10EditorContext";
    }
}
