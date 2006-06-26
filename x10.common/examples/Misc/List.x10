import x10.lang.Object;
public class List {
    public final int(:self >= 0) n;
    protected (nullable Object) (:(n==0 && self==null)|| (n !=0 && self!=null))  value;
    protected (nullable List(n-1))(:(n==0 && self == null) || ( n!=0 && self!=null)) tail;
  
    public List(t.n+1)(Object o, List t) {
        n=t.n+1;
        tail = t;
        value = o;
    }
    public List(0) () {
        n=0;
        value=null;
        tail=null;
    }
    public List(n+l.n) append(List l) {
        return (n==0)? l : new List((Object) value, tail.append(l)); // this cast should not be needed.
    }
    public Object this(:n>0) nth(int(: (self >= 1) && (self <= n)) k) {
        return k==1 ? (Object) value : tail.nth(k-1);
     
    }
  
    public List(k) gen(int(: (self >= 0)) k) {
        return k==0 ? new List() : new List(k, gen(k-1));
    }
  
}