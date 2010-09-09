public class SpanFTRO {
  public class V  {
    public final int index;
    public V parent;
    public int degree;
    public V [] neighbors;
    public volatile int color;
    public V(int i){index=i;}
    public void compute() {
      V node = this;
      for (;;) {
        V lastV = null;
        for (int k=0; k < node.degree; k++) {
          final V v = node.neighbors[k];
          if (v.color==0 && UPDATER.compareAndSet(v,0,1)) {
            v.parent=node;
            if (lastV != null) {
              final l = lastV; async l.compute();
              lastV=v;
            }
          }
	}
	if ((node=lastV)==null) break;
     }
    }
    public void tree() {
      finish compute();
    }
    ...
}

