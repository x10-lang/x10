package x10.lang;
import x10.lang.annotations.MethodAnnotation;
import x10.lang.annotations.FieldAnnotation;
import x10.lang.annotations.ExpressionAnnotation;
import x10.lang.annotations.StatementAnnotation;

/**
 * Annotation intended to be used to mark flat asyncs, i.e. asyncs 
 * whose bodies do not further spawn asyncs.
 */
public interface flat
    extends ExpressionAnnotation, StatementAnnotation, MethodAnnotation {
}
