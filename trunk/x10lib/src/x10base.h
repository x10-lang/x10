/**  copyright (c) 2006 IBM
 * Namespace to provide common functionality and tie loose ends.  
 *
 * #include "x10base.h" <BR>
 * -llib 
 *
 * A longer description.
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_x10base_h
#define x10lib_x10base_h

typedef int Place;

namespace x10lib
{
  /**X10 place id for the current place.
   * @return The place id
   */
  Place Here();
  
  /**Number of X10 places. This is as of now assumed to be fixed
   * through the execution of the computation.  
   * @return The number of X10 places.
   */
  Place MaxPlaces();
  
  /**Initialize the X10 environment
   */
  void Init(int argc, char *argv[]);
  
  /**Finalize the X10 environment
   */
  void Finalize();

} //namespace x10lib

#endif  // x10lib_x10base_h

