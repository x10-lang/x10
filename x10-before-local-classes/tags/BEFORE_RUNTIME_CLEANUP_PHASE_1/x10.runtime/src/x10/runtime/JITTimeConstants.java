/*
 * Created on Sep 28, 2004
 */
package x10.runtime;



/**
 * This class introduces static final fields that are initialized with values provided by other classes.
 * Any references to these fields should be recognized as constants by a JIT compiler compiling code that 
 * references any of these fields.  (It is important to ensure that classes that reference these fields
 * do not introduce a cycle in class loading dependences.)
 * 
 * @author Vivek Sarkar (based on ideas suggested by Allan Kielstra, Igor Peshansky, Chris Donawa, Raj Barik)
 */
public final class JITTimeConstants {

    public static final int NUMBER_OF_LOCAL_PLACES = Configuration.NUMBER_OF_LOCAL_PLACES;
    
    public static final boolean ABSTRACT_EXECUTION_STATS = Configuration.ABSTRACT_EXECUTION_STATS;
    
    public static final boolean ABSTRACT_EXECUTION_TIMES = Configuration.ABSTRACT_EXECUTION_TIMES;
}

