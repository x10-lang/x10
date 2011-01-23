/**
 * 
 */
package x10.constraint;

/**
 * @author vijay
 *
 */
public class XNameInt_c implements XName {
   int n;
   String s;
    public XNameInt_c(int n) {
        this.n= n;
    }
    public XNameInt_c(String s, int n) {
        this.s =s;
        this.n=n;
    }
    
    public String toString() {
        return (s == null ? "_" : s) + n;
    }

    public int hashCode() {
        return n;
    }

    public boolean equals(Object o) {
        return o instanceof XNameInt_c && ((XNameInt_c) o).n==n;
    }
}
