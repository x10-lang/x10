package x10.types.constraints.redesign;

import x10.constraint.redesign.XType;
import x10.constraint.redesign.XVar;

/**
 * Representation of the "self" variable from the constraints' point of view, 
 * along with its type information. 
 * 
 * @author lshadare
 *
 * @param <T>
 */
public interface CSelf <T extends XType> extends XVar <T> {

}
