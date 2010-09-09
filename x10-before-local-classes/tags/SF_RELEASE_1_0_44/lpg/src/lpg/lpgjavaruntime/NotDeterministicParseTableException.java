package lpg.lpgjavaruntime;

public class NotDeterministicParseTableException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public NotDeterministicParseTableException()
    {
        str = "NotDeterministicParseTableException";
    }
    public NotDeterministicParseTableException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
