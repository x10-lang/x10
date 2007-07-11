import java.lang.Object;

class Owned(Object o) {
  Owned(:self.o == o)(final Object o) {
    property(o);
  }
}

class Map(Owned ko, Owned vo) extends Owned {
  final Vec(:self.o == this && self.vo == this) nodes;

  Map(:self.o == o && self.vo == vo && self.ko == ko)
     (final Object o, final Owned ko, final Owned vo) {
    super(o);
    property(ko, vo);
  }

  void put(final Owned(:self.o == this.ko) key,
           final Owned(:self.o == this.vo) value) {

	  // Problem is here.
	  //
	  // this.nodes : Vec(:self.vo == self.o && this == self.o)
          // n          : Node(:self.o == this)
          //
          // add        : Owned(:self.o == this.vo) -> void
          // after sustitution:
	  // add        : Owned(:self.o == this.nodes.vo) -> void
          //
          // The constraint solver cannot determine this == this.nodes.vo.
          // This information is available in the type of this.nodes, but is
          // not used.
          //
          // I tried adding the constraint on this.nodes with [this.nodes/self]
          // to the substituted type for add's formal, but got an infinite loop
          // in setTerm as it tried to substitute.
          //
    final Node(:self.o == this) n = new Node(this, key, value);
    nodes.add(n);
  }

  Owned(:self.o == this.vo) get(final Owned(:self.o == this.ko) key) {
      Iter(:self.o == this.o && self.o == this.vo) i = nodes.iterator();
      while (i.hasNext()) {
          Node(:self.o == this && key.o == this.ko && value.o == this.vo) mn =
        	  (Node(:self.o == this && key.o == this.ko && value.o == this.vo)) i.next();

          if (mn.key.equals(key)) {
              return mn.value;
          }
      }
      return null;
  }
}

class Node(Map map) extends Owned {
	Owned(:self.o == this.map.ko) key;
	Owned(:self.o == this.map.vo) value;

	  public Node(:self.o == self.map && self.map == m)
	      (final Map m, final Owned(:self.o == m.ko) key, final Owned(:self.o == m.vo) value) {
	    super(m);
	    property(m);
	    this.key = key;
	    this.value = value;
	  }
}

/*
class Node(java.lang.Object ko, java.lang.Object vo) extends Owned {
  Owned(:self.o == this.ko) key;
  Owned(:self.o == this.vo) value;

  public Node(:self.o == m && self.ko == key.o && self.vo == value.o)
      (final Map m, final Owned(:self.o == m.ko) key, final Owned(:self.o == m.vo) value) {
    super(m);
    property(m.ko, m.vo);
    this.key = key;
    this.value = value;
  }
}
*/


class Vec(java.lang.Object vo) extends Owned {
  Vec(:self.o == o && self.vo == vo)(final Owned o, final Owned vo) {
    super(o);
    property(vo);
  }

  void add(final Owned(:self.o == this.vo) x) { }
}


class Iter(Owned vo) extends Owned {
  Iter(:self.o == o && self.vo == vo)(final Owned o, final Owned vo) {
    super(o);
    property(vo);
  }

  boolean hasNext() { return false; }
  Owned(:self.o == this.vo) next() { return next(); }
}
