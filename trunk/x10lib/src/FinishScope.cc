/** copyright (c) 2006 IBM
 * @author Sriram Krishnamoorthy
 */

#include "FinishScope.h"  // class implemented
#include "x10base.h" //Place and Here()
#include <boost/serialization/tracking.hpp>

BOOST_CLASS_TRACKING(x10lib::FinishScope, boost::serialization::track_never);

namespace x10lib 
{

/////////////////////////////// PUBLIC ///////////////////////////////////////

//============================= LIFECYCLE ====================================

  FinishScope::FinishScope() 
    : mRoot(-1), mScope(-1) 
  {
  }

//============================= OPERATORS ====================================

    /*Using the default assignment operator*/

  bool 
  FinishScope::operator == (const FinishScope& fs) const
  {
    return (mRoot==fs.mRoot && mScope==fs.mScope);
  } // ==
  
  bool 
  FinishScope::operator <= (const FinishScope& fs) const
  {
      return (mRoot<fs.mRoot || (mRoot==fs.mRoot && mScope<fs.mScope));
  } //less_than()


//============================= ACCESS      ==================================

  Place 
  FinishScope::Root() const 
  { 
    return mRoot; 
  } //Root()

//============================= INQUIRY    ===================================
  
  

/////////////////////////////// PRIVATE    ///////////////////////////////////

  FinishScope::FinishScope(int scope) 
    : mRoot(x10lib::Here()), mScope(scope)
  {
  }
  
} //namespace x10lib

