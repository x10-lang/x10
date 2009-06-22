/*
 * Created on Feb 9, 2006
 */
package x10.uide.editor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import polyglot.ast.Node;

public final class SourceHyperlink implements IHyperlink {
    private final ITextViewer fViewer;

    private final IRegion fRegion;

    private final Node fNode;

    public SourceHyperlink(ITextViewer viewer, IRegion region, Node node) {
        super();
        fViewer= viewer;
        fRegion= region;
        fNode= node;
    }

    public IRegion getHyperlinkRegion() {
        return new Region(fRegion.getOffset() - 2, fRegion.getLength() + 4);
    }

    public String getTypeLabel() {
        return null;
    }

    public String getHyperlinkText() {
        return fNode.toString();
    }

    public void open() {
        fViewer.setSelectedRange(0, 5);
    }
}