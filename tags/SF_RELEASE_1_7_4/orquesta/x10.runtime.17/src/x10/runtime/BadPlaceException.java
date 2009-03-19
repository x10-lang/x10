/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

/**
 * @author Christian Grothoff
 */
public class BadPlaceException extends RuntimeException {
    private final Object o_;
    private final Place pAccess_;
    private final Place pVar_;

    public BadPlaceException(Object o, Place p_access) {
        o_ = o;
        pAccess_ = p_access;
        pVar_ = null;
    }
    
    public BadPlaceException(Place p_var, Place p_access) {
        o_ = null;
        pAccess_ = p_access;
        pVar_ = p_var;
    }
    public String toString() {
        String ret;
        if (o_ == null) 
            ret = "BadPlaceException(dist. array, " + "var location=" + pVar_ + " access at place=" + pAccess_ + ")";
        else
            ret = "BadPlaceException(obj=" + o_ + " o.location=" + Runtime.location(o_) + "access at place=" + pAccess_ + ")";
        return ret;
    }
}
