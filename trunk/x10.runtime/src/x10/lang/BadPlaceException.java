package x10.lang;

/**
 * @author Christian Grothoff
 */
public class BadPlaceException extends x10.lang.Exception {
    private final x10.lang.Object o_;
    private final x10.lang.place p_;

    public BadPlaceException(x10.lang.Object o) {
        o_ = o;
        p_ = null;
    }
    
    public BadPlaceException(x10.lang.place p) {
        o_ = null;
        p_ = p;
    }
    public String toString() {
        String ret;
        if (o_ == null) 
            ret = "BadPlaceException(dist. array, " + "var location=" + p_ + " access at place=" + Runtime.here() + ")";
        else
            ret = "BadPlaceException(obj=" + o_ + " o.location=" + o_.getLocation() + "access at place=" + Runtime.here() + ")";
        return ret;
    }
}
