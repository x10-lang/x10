package x10.uide.editor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.uide.core.ILanguageService;
import org.eclipse.uide.editor.IReferenceResolver;
import org.eclipse.uide.editor.ISourceHyperlinkDetector;
import org.eclipse.uide.parser.IASTNodeLocator;
import org.eclipse.uide.parser.IParseController;


/**
 * Provides a method to detect hyperlinks originating from a
 * given region in the parse stream of a given parse controller.
 */

public class HyperlinkDetector implements ISourceHyperlinkDetector, ILanguageService {
	
    public HyperlinkDetector() { }

    public IHyperlink[] detectHyperlinks(
    		final ITextViewer textViewer, final IRegion region, IParseController parseController)
    {
    	// This is the only language-specific bit ...
    	// Note:  X10 should be the name of the language
    	// represented typographically like a class name
    	IReferenceResolver linkMapper = new X10ReferenceResolver();
    	
    	// Get stuff for getting link source node
        Object ast= parseController.getCurrentAst();
        if (ast == null) return null;
        int offset= region.getOffset();
        IASTNodeLocator nodeLocator = parseController.getNodeLocator();

        // Get link source node
        Object source = nodeLocator.findNode(ast, offset);
        if (source == null) return null;
        //if (!(source instanceof IASTNodeToken)) return null;
        if (!linkMapper.hasSuitableLinkSourceType(source)) return null;

        // Got a suitable link source node; get link target node
       	final Object target = linkMapper.getLinkTarget(source, parseController);
       	if (target == null) return null;

        // Link target node exists; get info for new hyperlink
       	// Note:  source presumably has a legitimate starting offset
       	// and length (since they have been selected from the source file)
        final int srcStart= nodeLocator.getStartOffset(source);
        final int srcLength= nodeLocator.getEndOffset(source) - srcStart + 1;
        // But the target (depending on what--and where--the target is)
        // may not have a legitimate location (or one wihtin the file).
        // In that case, set the target to the beginning of the file
        // and give it a nominal length.
        final int targetStart;
        if (nodeLocator.getStartOffset(target) < 0) targetStart = 0;
        else targetStart = nodeLocator.getStartOffset(target);
        final int targetLength;
        if (targetStart == 0) targetLength = 1;
        else targetLength = nodeLocator.getEndOffset(target) - targetStart + 1;
        // Get the link text
        final String linkText = linkMapper.getLinkText(source);
       	
        // Create and return the new hyperlink
        IHyperlink[] result = new IHyperlink[] {
            new IHyperlink() {
                public IRegion getHyperlinkRegion() {
                    return new Region(srcStart, srcLength);
                }
                public String getTypeLabel() {
               	    return target.getClass().getName();
                }
                public String getHyperlinkText() {
                    return new String(linkText);
                }
                public void open() {
                    textViewer.setSelectedRange(targetStart, targetLength);
                    textViewer.revealRange(targetStart, targetLength);
                }
            }
        };
        
        return result;
    }
    
}

