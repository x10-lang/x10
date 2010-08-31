using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Pex.Framework;

/*
 * @author Mohsen Vakilian
 * @author Tihomir Gvero
 * 
 */
namespace CommutativityTests
{
    [PexClass]
    public class Node
    {
        public static int nextId = 1;
        public int id;
        public Node next;

        public Node(int id, Node next)
        {
            this.id = nextId;
            ++nextId;
            this.next = next;
        }

        public void SwapByNext()
        {
            if (next != null)
            {
                Node t = next;
                next = next.next;
                t.next = this;
            }
        }

        public Node OldCloneRecursive()
        {
            if (next == null)
            {
                return new Node(id, null);
            }
            else
            {
                return new Node(id, next.OldCloneRecursive());
            }
        }

        public Dictionary<Node, Node> CloneRecursive()
        {
            if (next == null)
            {
                Dictionary<Node, Node> dictionary = new Dictionary<Node, Node>();
                dictionary.Add(this, new Node(id, null));
                return dictionary;
            }
            else
            {
                Dictionary<Node, Node> dictionary = next.CloneRecursive();
                dictionary.Add(this, new Node(id, dictionary[next]));
                return dictionary;
            }
        }

    }

    [PexClass]
    public class List
    {
        public Node head;

        public List(Node head)
        {
            this.head = head;
        }

        public bool RepOK()
        {
            HashSet<int> ids = new HashSet<int>();
            Node curNode = head;
            while (curNode != null)
            {
                if (!ids.Add(curNode.id))
                {
                    return false;
                }
            }
            return true;
        }

        public Node Find(int id)
        {
            Node curNode = head;
            while (curNode != null)
            {
                if (curNode.id == id)
                {
                    return curNode;
                }
            }
            return null;
        }

        public void SwapNode(int id)
        {
            Node n = Find(id);
            if (n != null)
            {
                n.SwapByNext();
            }
        }

        public bool Contains(Node node)
        {
            Node currentNode = head;

            while (currentNode != null)
            {
                if (currentNode == node)
                {
                    return true;
                }

                currentNode = currentNode.next;
            }

            return false;
        }

        public void SwapNode(Node n)
        {
            //PexAssert.IsTrue(this.Contains(n));
            n.SwapByNext();
        }

        public List OldClone()
        {
            return new List(head.OldCloneRecursive());
        }

        public override bool Equals(Object obj)
        {
            var l = obj as List;

            if (l == null)
                return false;

            Node thisCurrentNode = head;
            Node otherCurrentNode = l.head;

            while (true)
            {
                if (thisCurrentNode == null && otherCurrentNode == null)
                    return true;
                else if (thisCurrentNode != null && otherCurrentNode != null)
                {
                    if (!((thisCurrentNode == otherCurrentNode) || (thisCurrentNode.id == otherCurrentNode.id)))
                        return false;
                    thisCurrentNode = thisCurrentNode.next;
                    otherCurrentNode = otherCurrentNode.next;
                }
                else
                {
                    return false;
                }
            }
        }

        public override int GetHashCode()
        {
            return base.GetHashCode() ^ 348204098;
        }

    }

    [PexClass]
    public class SwapNodeInputs
    {
        public List list;
        public Node n1;
        public Node n2;

        public SwapNodeInputs(List list, Node n1, Node n2)
        {
            this.list = list;
            this.n1 = n1;
            this.n2 = n2;
        }

    }
/*
    public static class SwapNodeInputsFactory
    {
        [PexFactoryMethod(typeof(SwapNodeInputs))]
        public static SwapNodeInputs generateSwapNodeInputs()
        {
            List<Node> nodesList = new List<Node>();
            int length = PexChoose.ValueFromRange("list length", 3, 10);
            //int length = 3;
            //int length = PexChoose.Value<int>("list length");
            Node currentHead = null;
            for (int i = 0; i < length; ++i)
            {
                currentHead = new Node(PexChoose.Value<int>("elem " + i), currentHead);
                nodesList.Add(currentHead);
            }

            Node[] nodes = nodesList.ToArray();
            List l = new List(currentHead);
            Node n1 = nodes[PexChoose.ValueFromRange("nodes.indexOf(n1)", 0, nodes.Length - 1)];
            Node n2 = nodes[PexChoose.ValueFromRange("nodes.indexOf(n2)", 0, nodes.Length - 1)];
            return new SwapNodeInputs(l, n1, n2);
        }

    }

 */
    [PexClass]
    public partial class SwapNode
    {
        [PexMethod]
        public void IsSwapNodeByIdCommutative(List l1, List l2, int id1, int id2)
        {

            PexAssume.IsNotNull(l1);
            PexAssume.IsNotNull(l2);
            PexAssume.AreEqual(l1, l2);

            l1.SwapNode(id1);
            l1.SwapNode(id2);

            l2.SwapNode(id2);
            l2.SwapNode(id1);

            PexAssert.AreEqual(l1, l2);

        }
/*
        [PexMethod]
        public bool IsSwapNodeCommutative(List l, Node n1, Node n2)
        {
            bool precondition = l != null && n1 != null && n2 != null && l.Contains(n1) && l.Contains(n2);
            PexAssume.IsTrue(precondition);
            Contract.Requires(precondition);
            Dictionary<Node, Node> clone1 = l.head.CloneRecursive();
            Dictionary<Node, Node> clone2 = l.head.CloneRecursive();

            List l1 = new List(clone1[l.head]);
            List l2 = new List(clone2[l.head]);

            Node n1Clone1 = clone1[n1];
            Node n2Clone1 = clone1[n2];
            Node n1Clone2 = clone2[n1];
            Node n2Clone2 = clone2[n2];

            PexAssert.IsTrue(l1.Contains(n1Clone1));
            PexAssert.IsTrue(l1.Contains(n2Clone1));
            PexAssert.IsTrue(l2.Contains(n1Clone2));
            PexAssert.IsTrue(l2.Contains(n2Clone2));

            //Contract.Assert(l1.Contains(n1Clone1));
            //Contract.Assert(l1.Contains(n2Clone1));
            //Contract.Assert(l2.Contains(n1Clone2));
            //Contract.Assert(l2.Contains(n2Clone2));

            l1.SwapNode(n1Clone1);
            l1.SwapNode(n2Clone1);

            l2.SwapNode(n2Clone2);
            l2.SwapNode(n1Clone2);

            return l1.Equals(l2);
        }

        [PexMethod]
        public bool SwapNodeShouldNotBeCommutativeOnPexInput()
        {
            SwapNodeInputs inputs = SwapNodeInputsFactory.generateSwapNodeInputs();
            List l = inputs.list;
            Node n1 = inputs.n1;
            Node n2 = inputs.n2;

            bool postcondition = IsSwapNodeCommutative(l, n1, n2);
            PexAssert.IsTrue(postcondition);
            Contract.Ensures(postcondition);
            return postcondition;
        }

        [PexMethod]
        public bool SwapNodeShouldNotBeCommutativeOnCounterExample()
        {
            Node n3 = new Node(3, null);
            Node n2 = new Node(2, n3);
            Node n1 = new Node(1, n2);
            List l = new List(n1);

            PexAssert.IsTrue(l.Contains(n1));
            PexAssert.IsTrue(l.Contains(n2));
            PexAssert.IsTrue(l.Contains(n3));
            bool postcondition = !IsSwapNodeCommutative(l, n1, n2);
            PexAssert.IsTrue(postcondition);
            Contract.Ensures(postcondition);
            return postcondition;
        }

        [PexMethod]
        public bool SwapNodeShouldBeCommutativeOnExample()
        {
            Node n3 = new Node(3, null);
            Node n2 = new Node(2, n3);
            Node n1 = new Node(1, n2);
            List l = new List(n1);

            bool postcondition = IsSwapNodeCommutative(l, n1, n1);
            PexAssert.IsTrue(postcondition);
            Contract.Ensures(postcondition);
            return postcondition;
        }
*/
    }
}
