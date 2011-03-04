/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.lang;

/**
 * @author Christian Grothoff
 */
public class BadPlaceException extends x10.lang.Exception {
    private final x10.lang.Object o_;
    private final x10.lang.place pAccess_;
    private final x10.lang.place pVar_;

    public BadPlaceException(x10.lang.Object o, x10.lang.place p_access) {
        o_ = o;
        pAccess_ = p_access;
        pVar_ = null;
    }
    
    public BadPlaceException(x10.lang.place p_var, x10.lang.place p_access) {
        o_ = null;
        pAccess_ = p_access;
        pVar_ = p_var;
    }
    public String toString() {
        String ret;
        if (o_ == null) 
            ret = "BadPlaceException(dist. array, " + "var location=" + pVar_ + " access at place=" + pAccess_ + ")";
        else
            ret = "BadPlaceException(obj=" + o_ + " o.location=" + o_.getLocation() + "access at place=" + pAccess_ + ")";
        return ret;
    }
}
