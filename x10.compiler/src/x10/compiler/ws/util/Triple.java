package x10.compiler.ws.util;

/**
 * A utility class to wrap three objects together.
 * It is typically used to store fast/slow/back path stmts. 
 * 
 * @author Haichuan
 *
 * @param <F>
 * @param <S>
 * @param <T>
 */
public class Triple <F,S,T>
{
    protected F first;
    protected S second;
    protected T third;

    public Triple(){
        //all null;
    }
    
    public Triple(F f, S s, T t) {
            this.first = f;
            this.second = s;
            this.third = t;
    }

    
    public void setFirst(F f){
        this.first = f;
    }
    
    public void setSecond(S s){
        this.second = s;
    }
    
    public void setThird(T t){
        this.third = t;
    }
    
    public F first() {
        return first;
    }

    public S second() {
        return second;
    }
    
    public T third() {
        return third;
    }
    
    @Override
    public boolean equals(Object obj) {
            if (obj instanceof Triple<?, ?, ?>) {
                Triple<?,?, ?> p = (Triple<?,?, ?>) obj;
                    boolean r1 = first == null ? p.first == null : first.equals(p.first);
                    boolean r2 = second == null ? p.second == null : second.equals(p.second);
                    boolean r3 = third == null ? p.third == null : third.equals(p.third);
                    return r1 && r2 && r3;
            }
            return false;
    }

    @Override
    public int hashCode() {
            return (first == null ? 0 : first.hashCode()) + (second == null ? 0 : second.hashCode())
             + (third == null ? 0 : third.hashCode());
    }

    public String toString() {
            return "<" + first + ", " + second + ", " + third +">";
    }
}
