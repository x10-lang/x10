package x10.lang;

/**
 * An annotation intended to be used to mark variables (particularly fields) 
 * that are to be used to reliably communicate values between multiple 
 * activities. (Such variables are called volatile in some languages such as Java.)
 * @author vj
 *
 */
public interface shared extends x10.lang.annotations.FieldAnnotation{

}
