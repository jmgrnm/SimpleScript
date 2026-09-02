package lib.src.interpreterUtil;

public class Symbol {
    private String lexeme;
    private String tokenType;
    private Object value;

    public Symbol(String lexeme, String tokenType) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
        this.value = null;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Name: %-10s Type: %-8s Value: %s", lexeme, tokenType, value);
    }
}