package x10.lang;

/**
 * @author Christoph von Praun
 */

import x10.lang.TypeArgument;

/**
 * Implementation of Region. The Points in a region, aka. tuples, 
 * are implemented as one-dimensional int arrays. Instance of 
 * this class are immutable!
 *
 * @author Christoph von Praun
 * @author Christian Grothoff
 */
public interface Region extends x10.array.Region, TypeArgument {}

