using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;

/*
 * @author Mohsen Vakilian
 */
namespace CommutativityTests
{
    [PexClass]
    class SetInserter
    {
        [PexMethod]
        public void setInsertionShouldBeCommutative(int a, int b)
        {
            HashSet<int> set1 = new HashSet<int>();
            set1.Add(a);
            set1.Add(b);

            HashSet<int> set2 = new HashSet<int>();
            set2.Add(b);
            set2.Add(a);

            //PexAssert.AreEqual(set1, set2);
            PexAssert.IsTrue(set1.SetEquals(set2));
        }
    }
}
