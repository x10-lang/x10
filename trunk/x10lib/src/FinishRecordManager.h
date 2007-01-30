/**  copyright (c) 2006 IBM
 * Manager for the finish records at a place. Provides functionality
 * similar to a hash table.  
 *
 * #include "XX.h" <BR>
 * -llib 
 *
 * One object per place (singleton). This is essentially a hash table,
 * one per place. We have a class just to create a singleton.  
 *  
 * @see something
 * @author Sriram Krishnamoorthy 
 */

#ifndef x10lib_FinishRecordManager_h
#define x10lib_FinishRecordManager_h

#include <map>

#include "FinishScope.h"

namespace x10lib 
{
  class FinishRecord; //forward declaration

  class FinishRecordManager
    {
    public:
      // LIFECYCLE

      /** Default constructor is private. */

      /**Singleton initializer.*/
      static void Init();

      /**Singleton finalizer.*/
      static void Finalize();

      /**Singleton accessor.
       * @return Reference to the singleton object.
       */
      static FinishRecordManager& rInstance();

      /*No copy constructor*/
      
      /*Using the default destructor*/

      // OPERATORS
      // OPERATIONS                       

      /**Insert a Finish Record.
       * @param scope The finish scope of the finish record
       * @param p_fr  The finish record
       * @return The inserted finish record
       */
      FinishRecord* Insert(const FinishScope& scope, 
			   FinishRecord*      p_fr);

      /**Remove the finish record for the given finish scope.
       * @para, scope The relevant finish scope.
       */
      void Remove(const FinishScope& scope);

      /**Lookup the finish record for the given finish scope.
       * @param scope The relevant finish scope.
       * @return The finishrecord if present. NULL otherwise
       */ 
      FinishRecord* pLookup(const FinishScope& scope);

// ACCESS
// INQUIRY
      
    protected:
    private:
      typedef struct FinishScopeLessThanType {
	bool operator()(const FinishScope& rFs1,
			const FinishScope& rFs2) {
	  return rFs1 <= rFs2;
	}
      } FinishScopeLessThanType;

      std::map<FinishScope,FinishRecord*,FinishScopeLessThanType> mRecordsMap; /*<Map of extant finish records at this place*/ 
      
      /**Private default constructor*/
      FinishRecordManager();
      
      /**Private singleton object*/
      static FinishRecordManager* mpObject;      
    }; //class FinishRecordManager
  
  // INLINE METHODS
  //
  
  // EXTERNAL REFERENCES
  //

} //namespace x10lib

#endif  // x10lib_FinishRecordManager.h

