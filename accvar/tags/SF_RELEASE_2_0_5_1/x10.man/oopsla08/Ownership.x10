class Owned(Object o) { }

class Map(Owned ko, Owned vo) extends Owned {
  final Vec(:self.o == this && self.vo == this) nodes;

  void put(final Owned(ko) key,
           final Owned(vo) value) {
    final Node(this) n = new Node(this, key, value);
    nodes.add(n);
  }

  Owned(vo) get(Owned(ko) key) {
    final Iter(o, vo) i = nodes.iterator();
    while (i.hasNext()) {
      Node(this, this) n = (Node(this, this)) i.next();
      
      Owned(ko) k = n.key;
      Owned(vo) v = n.value;

      if (k.equals(key)) {
          return v;
      }
    }
    return null;
  }
}

class Node(Map map) extends Owned {
  final Owned(map.ko) key;
  final Owned(map.vo) value;

  public Node(m, m)(Map m, final Owned(m.ko) key, final Owned(m.vo) value) {
    this.key = key;
    this.value = value;
  }
}

class Vec(Object vo) extends Owned {
  void add(final Owned(vo) x) { }
  Iter(this.o, this.vo) iterator() { return new Iter(o, vo); }
}


class Iter(Object vo) extends Owned {
  Iter(this.o, this.vo)(Object o, Object vo) { }
  boolean hasNext() { return false; }
  Owned(vo) next() { return next(); }
}
