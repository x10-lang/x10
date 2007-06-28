inteface Label {
    Label H = new Label();
    Label L = new Label();

    boolean lt(Label x, Label y);
    Label join(Label x, Label y);
}

class LT(Label x, Label y) { }

class Labeled(Label lbl) { }

class LabeledInt(Label lbl, int v) extends Labeled(lbl) {
    Labeled(Label lbl, int v) { super(lbl); prop(v); }
}

class JifEx {
    LabeledInt(:self.lbl == H) sec;
    LabeledInt(:self.lbl == L) pub;

    void leak() {
        pub.v = 0
        if (sec.v == 1)
            // need pub.v >= join(pc,label(1)) = sec.v
            pub.v = 1
    }
}
