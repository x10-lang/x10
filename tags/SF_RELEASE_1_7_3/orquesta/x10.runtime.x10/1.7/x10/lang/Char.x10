package x10.lang;

/** 
    The value class representing a character.
 */
public value Char {
    def ord() = toInt();
    native def toInt(): Int;
    
    // ... methods of java.lang.Character
    // ... encodings, etc?
}

