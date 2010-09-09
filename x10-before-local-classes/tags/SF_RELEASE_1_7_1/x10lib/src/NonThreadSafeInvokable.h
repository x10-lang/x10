/**  copyright (c) 2006 IBM
 * Interface for functions that might not be thread-safe (including
 * communication).  
 *
 * #include "NonThreadSafeInvokable.h" <BR>
 * -llib 
 *
 * A longer description.
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_NonThreadSafeInvokable_h
#define x10lib_NonThreadSafeInvokable_h

#include <boost/serialization/access.hpp>

namespace x10lib
{

  class NonThreadSafeInvokable
  {
  public:

    /** Virtual destructor for inheritence.
    */
    virtual ~NonThreadSafeInvokable(void) {}

    /**Pure virtual function to be implemented by children.
     */
    virtual void Invoke()=0;

  protected:
  private:
    friend class boost::serialization::access;
    template<class Archive>
      void serialize(Archive& ar, unsigned int version)
      { //VOID
      }
  }; //class Runnable
} //namespace x10lib

#endif  // x10lib_NonThreadSafeInvokable_h

