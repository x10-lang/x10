/**  copyright (c) 2006 IBM
 * Stackless thread state
 *
 * #include "SltType.h" <BR>
 * -llib 
 *
 * This is used to store the state to implement finish, clock and
 * other blocking functionality.  
 * A variable of this type will be placed as a protected member (or
 * get/set-table as a protected member) in the Activity class. This
 * would be the current label in the execution code.
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_SltType_h
#define x10lib_SltType_h

#include "FinishScope.h"

// SYSTEM INCLUDES
//

// PROJECT INCLUDES
//

// LOCAL INCLUDES
//

// FORWARD REFERENCES
//

namespace x10lib
{
  class FinishRecord;

  class SltType {
  public:
    //LIFECYCLE
    /**Default constructor.
     */
    SltType();

    // OPERATORS

    // OPERATIONS                       

    /**Set finish record*/
    void Record(FinishRecord *record);

    /**Set finish scope*/
    void Scope(FinishScope scope);

    /**Set break label*/
    void Label(int label);

    // ACCESS

    /**Access finish record*/
    FinishRecord* pRecord();
    
    /**Access the finish scope*/
    FinishScope Scope();
    
    /**Access the break label*/
    int Label();

    // INQUIRY
    
  protected:
  private:
    FinishRecord* mRecord;
    FinishScope   mScope;
    int           mLabel;

  }; //class SltType

} //namespace x10lib

#endif  // x10lib_SltType_h

