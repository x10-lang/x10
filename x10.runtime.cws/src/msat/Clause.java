package msat;

public class Clause {
	int sizeEtc;
	float act;
	int abst;
	Lit[] data;
	int size() { return sizeEtc >> 3; }
	void shrink(int i) { 
		assert i <= size(); 
		sizeEtc = (((sizeEtc >> 3) -i) << 3) | (sizeEtc & 7);
	}
	void pop() { shrink(1);}
	boolean learnt () { return (sizeEtc & 1)==1; }
	int mark() { return (sizeEtc >> 1) & 3;}
	void mark(int m) { sizeEtc = (sizeEtc & ~6) | ((m & 3) << 1); }
	Lit last () { return data[size()-1];}
	int abstraction() { return abst; }
	Lit get(int i) { return data[i];}
	void calcAbstraction() {
		int abstraction = 0;
		for (int i=0; i < size(); ++i) abstraction |= 1 << (data[i].var() & 31);
		abst = abstraction;
	}
	void strengthen(Lit p) {
	}

	Lit subsumes(Clause other) {
		if (other.size() < size() || (abst & ~other.abst) != 0)
			return Lit.LIT_ERROR;

		Lit        ret = Lit.LIT_UNDEF;
		final Lit[] c = this.data, d = other.data;
		L: for (int i = 0; i < size(); i++) {
			// search for c[i] or ~c[i]
			for (int j = 0; j < other.size(); j++)
				if (c[i] == d[j])
					break;
				else if (ret == Lit.LIT_UNDEF && c[i] == ~d[j]){
					ret = c[i];
					break;
				}

			// did not find it
			return Lit.LIT_ERROR;
		}
		return ret;
	}
}
