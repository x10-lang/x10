package lpg.lpgjavaruntime;

public class MismatchedInputCharsException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public MismatchedInputCharsException()
    {
        str = "MismatchedInputCharsException";
    }
    public MismatchedInputCharsException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}
