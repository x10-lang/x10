package x10.lang;

/** 
    The value class representing a boolean.
 */
public value Boolean {
    def extern not(): Boolean;
    def extern and(x: Boolean): Boolean;
    def extern or(x: Boolean): Boolean;
    def extern xor(x: Boolean): Boolean;
}

