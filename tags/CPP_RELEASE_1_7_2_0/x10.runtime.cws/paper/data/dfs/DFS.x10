
public class V {
    final int index;
    V parent;
    int degree;
    V [] neighbors;
    Color color;
    V (int i) {index=i;}

    public void compute()  {
	V node = this;
	for (int k=0; k < node.degree; k++) {
	    final V v = node.neighbors[k];
	    async (v) if (v.color.compareAndSet(0,1)) {
		v.parent=node;
		v.compute();
	    }
	}
    }
    void tree() {
	finish compute();
    }
}
