package x10.lang;

/**
 * An annotation intended to be used to mark asyncs so that the 
 * runtime is requested to aggregate them rather than sending them off
 * to the destination one at a time. An environment variable is used to
 * specify the maximum number that can be aggregated.
 * @author vj
 *
 */
public interface aggregate extends x10.lang.annotations.StatementAnnotation{

}
