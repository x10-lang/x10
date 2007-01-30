/**  copyright (c) 2006 IBM
 *
 * Class to create unqiue FinishScope objects.
 * #include "FinishScopeManager.h" <BR>
 * -llib 
 *
 * This is a singleton class to create unique FinishScope
 * objects. The singleton is initialized when the place is
 * initialized. 
 *  
 * @see something
 * @author Sriram Krishnamoorthy
 */

#ifndef x10lib_FinishScopeManager_h
#define x10lib_FinishScopeManager_h

#include "FinishScope.h" //To construct FinishScope objects


namespace x10lib 
{
  class FinishScopeManager 
    {
    public:
      // LIFECYCLE
      
      /** private constructor*/
      /**Disabling default copy constructor by making it private*/
      /** Using the default destructor*/

      static void Init(); //initialize the manager
      static void Finalize(); //finalize it

      //OPERATORS - none

      // OPERATIONS                       
      FinishScope CreateFinishScope();

      //ACCESS
      /**Singleton accessor*/
      static FinishScopeManager& rInstance();

    private:
      /**Default constructor*/
      FinishScopeManager();

      /**Disabling default copy constructor*/
      FinishScopeManager(const FinishScopeManager& mgr) {}

      /**Singleton object*/
      static FinishScopeManager *msScopeMgr;

      int mScopeCtr; //counter to create unique scopes
      
    }; //class FinishScopeManager
} //namespace x10lib


#endif  // x10lib_FinishScopeManager_h

