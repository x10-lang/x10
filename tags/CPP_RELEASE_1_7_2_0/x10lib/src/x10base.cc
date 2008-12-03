/** copyright (c) 2006 IBM
 * @author Sriram Krishnamoorthy
 */

#include "x10base.h"
#include "system.h"
#include "FinishScopeManager.h"
#include "FinishRecordManager.h"
#include "ActivityManager.h"
#include "DsFinishRecord.h"

namespace x10lib 
{
  Place Here()
  {
    return (Place)system::NodeId();
  }
  
  Place MaxPlaces()
  {
    return (Place)system::NumNodes();
  }
  
  void Init(int argc, char *argv[])
  {
    system::Init(argc, argv);
    FinishScopeManager::Init();
    FinishRecordManager::Init();
    ActivityManager::Init();
    DsFinishRecord::Init();
  }
  
  void Finalize()
  {
    DsFinishRecord::Finalize();
    ActivityManager::Finalize();
    FinishRecordManager::Finalize();
    FinishScopeManager::Finalize();
    system::Finalize();
  }

} //namespace x10lib

