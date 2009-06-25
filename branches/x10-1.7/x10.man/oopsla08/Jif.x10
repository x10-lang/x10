class Label {
    final static Label H = new Label();
    final static Label L = new Label();

    private Label() { }

    Label join(Label l) {
        if (this == H || l == H)
            return H;
        return L;
    }

    boolean leq(Label l) {
        if (this == H)
            return l == H;
        return true;
    }
}

// the "program counter"
class Runnable(Label pc) {
    Runnable(:pc == l)(Label l) {
        property(l);
    }

    void run() { }
}

class Labeled(Label label) {
    private int x;

    private static Label pc

    Labeled(:label = l)(final Label l, int v) {
        property(l);
        x = v;
    }

    void cmp(Labeled i, Labeled j, Runnable curr,
             Runnable(:curr.pc.join(i.label).join(j.label).leq(pc)) T,
             Runnable(:curr.pc.join(i.label).join(j.label).leq(pc)) F) {
        if (i < j) 
            T.run();
        else
            F.run();
    }

    void assign(Runnable curr, Labeled i : curr.pc.join(i.label).leq(label)) {
        this.x = i.x;
    }

    int get(Runnable curr : curr.pc.leq(label)) {
        return x;
    }
}

class Leak extends Runnable(:pc == H) {
    Labeled(:self.label == H) sec;
    Labeled(:self.label == L) pub;

    Leak(:pc == H)() {
        super(H);
    }

    void run() {
        pc = L;

        pub.assign(this, pub); // legal
        sec.assign(this, pub); // legal
        sec.assign(this, sec); // legal
        pub.assign(this, sec); // illegal

        Labeled one = new Labeled(L, 1);

        if (sec.eq(this, one)) {
            pub.assign(this, one); // illegal
        }
    }
}
