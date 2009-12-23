package x10.effects.constraints;

/**
 * Interface representing an object with mutable fields.
 * Represents an object -- and hence the set of locations specified 
 * by its mutable fields. An object is designated by an XTerm. 
 * The XTerm must be rigid -- that is all variables occurring in it 
 * must be final.
 * 
 * @author vj
 *
 */
public interface ObjLocs extends RigidTerm, Locs {

}
