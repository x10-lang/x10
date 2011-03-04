package lpg.lpgjavaruntime;

public class BadParseException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public int error_token;
    public BadParseException(int error_token)
    {
        this.error_token = error_token;
    }
}
