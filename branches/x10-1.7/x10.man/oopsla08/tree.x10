/**
   A distributed binary tree.
   @author Satish Chandra 4/6/2006
   @author vj
 */
//                             ____P0
//                            |     |
//                            |     |
//                          _P2  __P0
//                         |  | |   |
//                         |  | |   |
//                        P3 P2 P1 P0
//                         *  *  *  *
// Right child is always on the same place as its parent;
// left child is at a different place at the top few levels of the tree,
// but at the same place as its parent at the lower levels.

class Tree(localLeft: boolean,
           left: nullable Tree(& localLeft => loc=here),
           right: nullable Tree(& loc=here),
           next: nullable Tree) extends Object {
    def postOrder:Tree = {
        val result:Tree = this;
        if (right != null) {
            val result:Tree = right.postOrder();
            right.next = this;
            if (left != null) return left.postOrder(tt);
        } else if (left != null) return left.postOrder(tt);
        this
    }
    def postOrder(rest: Tree):Tree = {
        this.next = rest;
        postOrder
    }
    def sum:int = size + (right==null => 0 : right.sum()) + (left==null => 0 : left.sum)
}
value TreeMaker {
    // Create a binary tree on span places.
    def build(count:int, span:int): nullable Tree(& localLeft==(span/2==0)) = {
        if (count == 0) return null;
        {val ll:boolean = (span/2==0);
         new Tree(ll,  eval(ll => here : place.places(here.id+span/2)){build(count/2, span/2)},
           build(count/2, span/2),count)}
    }
}
