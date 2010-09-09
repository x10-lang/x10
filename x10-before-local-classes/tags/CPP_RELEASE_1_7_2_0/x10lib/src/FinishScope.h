/** copyright (c) 2006 IBM
 * 
 * A globally unique identifier to define a finish scope. 
 *
 * #include "FinishScope.h" <BR>
 * -llib 
 *
 * This class defines a globally unique identifier for a finish
 * scope. A FinishScope is serializable and assignable.  
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
*/

#ifndef x10lib_FinishScope_h
#define x10lib_FinishScope_h

#include <boost/serialization/access.hpp> //for serialization


#include "x10base.h" //for definition of Place

namespace x10lib {
  class FinishScope {
  public:
    //LIFECYCLE
    
    /*Default constructor.
     */
    FinishScope();

    /*Using the default copy constructor, and destructor. Constructor
     * with a specific is private. Can only be created by
     * FinishScopeManager. */ 

    /*Using the default assignment operator*/

    //==========ACCESS==========
    Place Root() const;

    //OPERATORS
    bool operator ==(const FinishScope& fs) const;
    bool operator <=(const FinishScope& fs) const;

    //=========INQUIRY==========

  private:
    friend class FinishScopeManager; //To access private constructor
    
    /*Private constructor with a specific scope*/
    FinishScope(int scope);

    friend class boost::serialization::access;
    template<class Archive>
      void serialize(Archive& ar, unsigned int version) {
      ar & mRoot & mScope;
    }

    Place mRoot; /**<The root of this finish scope*/
    int mScope;  /**<Unique scope within host*/
  }; //class FinishScope
} //namespace x10lib

#endif //x10lib_FinishScope_h
