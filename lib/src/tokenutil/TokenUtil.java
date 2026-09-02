package lib.src.tokenutil;

import java.util.Set;

public class TokenUtil {
    public static boolean isToken(String word, Set<String> TOKENS) {
        return TOKENS.contains(word.toLowerCase());
    }

    public static boolean isPunctuation(String word) {
        return word.matches("[;(),{}\\[\\]:.,]");
    }

    public static boolean isLiteral(String word) {
        return  word.matches("\\d+")|| // Integers
                word.matches("[+-]?\\d+\\.\\d+")|| // Floats
                word.matches("\"([^\"]*)\"")|| // Double-quoted strings
                word.matches("'[^']*'") || // Single-quoted strings
                word.matches("yes|no"); // Booleans
    }

    public static boolean isIdentifier(String word, Set<String> TOKENS) {
        return word.matches("[a-zA-Z_][a-zA-Z0-9_]*") && !isToken(word, TOKENS);
    }

    public static boolean isArithmeticOp(String word) {
        return word.matches("plus|minus|times|over|mod");
    }

    public static boolean isComparisonOp(String word) {
        return word.matches("is|isnt|less|more|lesseq|moreeq");
    }

    public static boolean isLogicalOp(String word) {
        return word.matches("and|or|not");
    }

    public static boolean isAssignment(String word) {
        return word.matches("let|be");
    }

    public static boolean isConditional(String word) {
        return word.matches("check|orcheck|otherwise");
    }

    public static boolean isLoop(String word) {
        return word.matches("while|repeat");
    }

    public static boolean isFunction(String word) {
        return word.matches("task|give|return|show");
    }

    public static boolean isDataType(String word) {
        return word.matches("int|float|string|bool");
    }

    public static boolean isDelimiter(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == ';' || c == ',' || c == '[' || c == ']';
    }
}
