public class testx10ForLoopSpaces {

    public testx10ForLoopSpaces() { }

    void main(){
        final IntBox[.] z = new IntBox[[0:4]];
        z[0] = new IntBox(new java.lang.Integer(0));
        z[1] = new IntBox(new java.lang.Integer(1));
        java.lang.Integer zval = z[0].value;
        final IntBox y = new IntBox(zval);
        for(IntBox zsub : z) {
            zsub.value=y.value;
        }
    }
}