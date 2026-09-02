package lib.src.tokenutil;

import lib.src.generators.SetTerminals;

import java.util.HashSet;
import java.util.Set;

public class Token {
    private TokenType type;
    private String lexeme;
    private int lineNumber;
    private int columnNumber;

    public Token(TokenType type, String lexeme, int lineNumber, int columnNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getItem()
    {
        Set<String> terminals = new HashSet<>();
        for (SetTerminals item : SetTerminals.values())
        {
            terminals.add(item.getStringValue());
        }
        if (type == TokenType.LITERAL) //CHECKS IF LITERAL
        {
            return "LITERAL";
        }
        if (terminals.contains(lexeme)) //IF KEYWORD OR PUNCTATION OR ANY TERMINALS, RETURN AS IT IS
        {
            return lexeme.toString();
        }
        if (type == TokenType.IDENTIFIER) //CHECKS IF IDENTIFIER
        {
            return type.toString();
        }
        if(type == TokenType.EOF)
        {
            return getLexeme();
        }
        if(type == TokenType.LOOP)
            return getLexeme();
        else
        {
            return type.toString();
        }
    }


    public String toString() {
        switch (type) {
            case IDENTIFIER:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case ARITHMETIC_OP:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case COMPARISON_OP:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case LOGICAL_OP:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case ASSIGNMENT:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case CONDITIONAL:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case LOOP:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case FUNCTION:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case DATA_TYPE:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case LITERAL:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case PUNCTUATION:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case EOF:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            default:
                return "non-token";
        }
    }
}
