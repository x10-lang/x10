/**
 * Value class test:
 *
 * Value class fields should be able to be declared final.
 *
 * @author Mandana Vaziri, kemal 4/2005
 *
 */
final value class myval {
    final int intval;
    final complex cval;
    final foo refval;
    final int value[.] arrayval;
    myval(int intval,complex cval,foo refval, int value[.] arrayval) {
        this.intval=intval;
        this.cval=cval;
        this.refval=refval;
        this.arrayval= arrayval;
    }
}

class foo {
    int w=19;
}

final value  complex extends x10.lang.Object {
    final int re;
    final int im;
    complex(int re, int im) {
        this.re=re;
        this.im=im;
    }
    complex add (complex other) {
        return new complex(this.re+other.re,this.im+other.im);
    }
}

public class ValueClass4  {


        public boolean run() {

        distribution d=[0:9]->here;
        final foo f= new foo();
        myval x= new myval(1,new complex(2,3),f, new int value[d]);
        myval y= new myval(1,new complex(2,3),f, new int value[d]);
	return (x==y);
        }

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ValueClass4()).run();
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
