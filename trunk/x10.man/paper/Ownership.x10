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

  nullable< Owned(:self.o == this.vo) > get(final Owned(:self.o == this.ko) key) {
      final Iter(:self.o == this && self.vo == this) i = nodes.iterator();
      // while (i.hasNext()) {
          // Node(:self.o == this && self.map == this) mn =
        	  // (Node(:self.o == this && self.map == this)) i.next();
          
          // Owned(:self.o == this.ko) k = mn.key;
          // Owned(:self.o == this.vo) v = mn.value;

          // if (k.equals(key)) {
              // return v;
          // }
      // }
      return null;
  }
}

class Node(Map map) extends Owned {
	final Owned(:self.o == this.map.ko) key;
	final Owned(:self.o == this.map.vo) value;

	  public Node(:self.o == self.map && self.map == m)
	      (final Map m, final Owned(:self.o == m.ko) key, final Owned(:self.o == m.vo) value) {
	    super(m);
	    property(m);
	    this.key = key;
	    this.value = value;
	  }
}

class Vec(java.lang.Object vo) extends Owned {
  Vec(:self.o == o && self.vo == vo)(final Owned o, final Owned vo) {
    super(o);
    property(vo);
  }

  void add(final Owned(:self.o == this.vo) x) { }
  Iter(:self.o == this.o && self.vo == this.vo) iterator() { return new Iter(o, vo); }
}


class Iter(java.lang.Object vo) extends Owned {
  Iter(:self.o == o && self.vo == vo)(final Owned o, final Owned vo) {
    super(o);
    property(vo);
  }

  boolean hasNext() { return false; }
  Owned(:self.o == this.vo) next() { return next(); }
}
