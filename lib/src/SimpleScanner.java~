package lib.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static lib.src.tokenutil.TokenUtil.*;
import lib.src.tokenutil.*;

public class SimpleScanner {
    private Scanner scanner;
    private SymbolTable symbolTable;
    private static final Set<String> TOKENS = new HashSet<>();
    private int lineNumber = 1;
    private int columnNumber = 1;
    private StringBuilder unreadBuffer = new StringBuilder(); // For unreading characters
    private static String currentToken = "";

    static {
        for (Terminals token : Terminals.values()) {
            TOKENS.add(token.name().toLowerCase());
        }
        TOKENS.add(",");
        TOKENS.add("(");
        TOKENS.add(")");
        TOKENS.add("{");
        TOKENS.add("}");
        TOKENS.add("[");
        TOKENS.add("]");
        TOKENS.add(";");
        TOKENS.add(":");
        System.out.println(TOKENS);
    }


    public SimpleScanner(String filePath) throws FileNotFoundException {
        this.scanner = new Scanner(new File(filePath));
        this.symbolTable = new SymbolTable();
        this.scanner.useDelimiter("");
    }

    public Token getNextToken() {
        StringBuilder tokenBuilder = new StringBuilder();
        boolean inString = false;
        boolean inComment = false;
        char stringDelimiter = '\0';

        while (scanner.hasNext() || unreadBuffer.length() > 0) {
            char c = readChar();

            if (c == '\n') {
                lineNumber++;
                columnNumber = 1;
            } else {
                columnNumber++;
            }

            // Handle comments (/* ... */)
            if (c == '|' && !inString) {
                if (scanner.hasNext()) {
                    char nextChar = scanner.next().charAt(0);
                    if (nextChar == '*') {
                        inComment = true;
                    } else {
                        while (scanner.hasNext() && scanner.next().charAt(0) != '\n') {
                        }
                        lineNumber++;
                        columnNumber = 1;
                        continue;
                    }
                }
                continue;
            }

            // Handle comment end
            if (inComment) {
                if (c == '*' && scanner.hasNext() && scanner.next().charAt(0) == '|') {
                    inComment = false;
                }
                continue;
            }

            // Handle string literals
            if (c == '"' || c == '\'') {
                if (!inString) {
                    inString = true;
                    stringDelimiter = c;
                } else if (c == stringDelimiter) {
                    inString = false;
                }
            }

            // Handle tokenization logic (identifier, number, punctuation)
            if (!inString && !inComment && (Character.isWhitespace(c) || isDelimiter(c))) {
                // If a token is accumulated, return it
                String token = tokenBuilder.toString().trim();
                if (!token.isEmpty()) {
                    Token result = createToken(token);
                    if (result != null)
                    {
                        return result;
                    } else {
                        skipInvalidToken();
                        tokenBuilder.setLength(0);
                        continue;
                    }
                }
                tokenBuilder.setLength(0);

                // If the character is a delimiter, immediately return the token for it
                if (isDelimiter(c)) {
                    return createToken(String.valueOf(c));  // return the delimiter as a token
                }
            } else {
                // Append the current character to the token being built
                tokenBuilder.append(c);
            }
        }

        // Handle final token if there's any remaining
        String token = tokenBuilder.toString().trim();
        if (!token.isEmpty()) {
            Token result = createToken(token);
            if (result != null) {
                return result;
            } else {
                skipInvalidToken();
            }
        }

        return null;
    }

    private char readChar() {
        if (unreadBuffer.length() > 0) {
            char c = unreadBuffer.charAt(unreadBuffer.length() - 1);
            unreadBuffer.deleteCharAt(unreadBuffer.length() - 1);
            return c;
        }
        return scanner.next().charAt(0);
    }

    private void skipInvalidToken() {
        while (scanner.hasNext()) {
            char c = scanner.next().charAt(0);
            if (Character.isWhitespace(c)) {
                break;
            }
            if (isDelimiter(c))
            {

                break;
            }

        }
    }

    private Token createToken(String token) {
        TokenType type = getTokenType(token);
        if (type == null) {
            System.err.println("Error: Invalid token '" + token + "' at line " + lineNumber + ", column "
                    + (columnNumber - token.length()));
            return null;
        }
        if (type == TokenType.LITERAL) {
            symbolTable.add(currentToken, token);
            currentToken = "";
        }
        else if (type == TokenType.IDENTIFIER) //CHECKS IF IDENTIFIER
        {
            currentToken = token;
        }



        return new Token(type, token, lineNumber, columnNumber - token.length() + 1);
    }

    public static TokenType getTokenType(String token) {
        if (isArithmeticOp(token)) return TokenType.ARITHMETIC_OP;
        if (isComparisonOp(token)) return TokenType.COMPARISON_OP;
        if (isLogicalOp(token)) return TokenType.LOGICAL_OP;
        if (isAssignment(token)) return TokenType.ASSIGNMENT;
        if (isConditional(token)) return TokenType.CONDITIONAL;
        if (isLoop(token)) return TokenType.LOOP;
        if (isFunction(token)) return TokenType.FUNCTION;
        if (isDataType(token)) return TokenType.DATA_TYPE;
        if (isPunctuation(token)) return TokenType.PUNCTUATION;
        if (isLiteral(token)) return TokenType.LITERAL;
        if (isIdentifier(token, TOKENS)) return TokenType.IDENTIFIER;
        return null;
    }

    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
