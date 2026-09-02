package lib.src.interpreterUtil;



public class interUtil {
    public static boolean isString(String word) {
        return word.matches("[^\"]*\"");
    }

    public static boolean isBoolean(String word) {
        return word.matches("yes|no");
    }

    public static boolean isInteger(String word) {
        return word.matches("\\d+");
    }

    public static boolean isFloat(String word) {
        return word.matches("[+-]?\\d+\\.\\d+");
    }

    public static boolean isArithmeticOp(String word) {
        return word.matches("plus|minus|times|over|mod");
    }

    public static int precedence(String op) {
        switch (op) {
            case "is":
            case "isnt":
            case "less":
            case "more":
            case "lesseq":
            case "moreeq":
                return 1;
            case "plus":
            case "minus":
                return 2;
            case "times":
            case "mod":
            case "over":
                return 3;
            default:
                return -1;
        }
    }

    public static boolean isComparisonOp(String op)
    {
        return op.equals("is") || op.equals("isnt") ||
                op.equals("less") || op.equals("more") ||
                op.equals("lesseq") || op.equals("moreeq");
    }
}

