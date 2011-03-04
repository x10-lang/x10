package x10.lang;

/**
 * Top is a special interpreted interface. 
 * It may be used in an interface definition. When this interface is instantiated by a class
 * (subclass of x10.lang.Object), Top is interpreted as x10.lang.Object.
 * 
 * When it is instantiated by a struct S, Top is interpreted as S.
 * @author vj 10/25/09
 */
public interface Top {}