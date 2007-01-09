package lpg.lpgjavaruntime;

public class NotBacktrackParseTableException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public NotBacktrackParseTableException()
    {
        str = "NotBacktrackParseTableException";
    }
    public NotBacktrackParseTableException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
