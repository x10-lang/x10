/**
 *
 * Tests assignments to final fields in constructor 
 *
 * @author kemal, 3/2005
 *
 */
class myval {
    final int intval;
    final complex cval;
    final foo refval;
    myval(int intval,complex cval,foo refval) {
        this.intval=intval;
        this.cval=cval;
        this.refval=refval;
    }
    boolean eq(myval other) {
	return 
	this.intval==other.intval &&
	this.cval.eq(other.cval) &&
	this.refval==other.refval;
    }
}

class foo {
    int w=19;
}

class complex {
    final int re;
    final int im;
    complex(int re, int im) {
        this.re=re;
        this.im=im;
    }
    complex add (complex other) {
        return new complex(this.re+other.re,this.im+other.im);
    }
    boolean eq(complex other) {
	return this.re==other.re && this.im==other.im;
    }
}

public class FinalInitializationTest  {


        public boolean run() {

        foo f= new foo();
        myval x= new myval(1,new complex(2,3),f);
        myval y= new myval(1,(new complex(1,4)).add(new complex(1,-1)),f);
        return (x.eq(y));
        }

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new FinalInitializationTest()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }


}
