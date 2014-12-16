/**
 * 
 */
package x10.constraint.xnative;

import x10.util.CollectionFactory;

/**
 * Class represents those XVar's that can be roots, viz., 
 * existentially or universally quantified variables, 
 * local variables.
 * 
 * @author vj
 *
 */
public abstract class XRoot extends XNativeVar {
    private static final long serialVersionUID = -8464916210710432790L;
    public XRoot() {}

    @Override public XPromise nfp(XNativeConstraint c) {
        assert c != null;
        XPromise p = null;
        if (c.roots == null) {
            c.roots = CollectionFactory.<XNativeTerm, XPromise> newHashMap();
            p = c.intern(this);
        } else {
            p = c.roots.get(this);
            if (p == null) p = c.intern(this);
        }
        return p.lookup();
    }
    
}
