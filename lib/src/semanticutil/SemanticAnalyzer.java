package lib.src.semanticutil;

import lib.src.interpreterUtil.SymbolTable;
import lib.src.tokenutil.Token;
import lib.src.tokenutil.TokenType;
import java.util.*;

public class SemanticAnalyzer {
    private List<String> errors;
    private List<String> warnings;
    private Map<String, VariableInfo> symbolTable;
    private Stack<Token> tokens;
    private Token currentToken;
    private String lastProcessedIdentifier;
    private SymbolTable interpreterTable;

    public SemanticAnalyzer() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.symbolTable = new HashMap<>();
        this.interpreterTable = new SymbolTable();
    }

    public boolean analyze(Stack<Token> inputTokens) {
        Stack<Token> tempStack = new Stack<>();
        while (!inputTokens.isEmpty()) {
            tempStack.push(inputTokens.pop());
        }

        this.tokens = new Stack<>();
        while (!tempStack.isEmpty()) {
            this.tokens.push(tempStack.pop());
        }

        while (!this.tokens.isEmpty()) {
            currentToken = this.tokens.pop();
            checkToken(currentToken);
        }

        // Return false if there are any errors to prevent AST generation
        return errors.isEmpty();
    }

    private void checkToken(Token token) {
        if (token.getType() == TokenType.IDENTIFIER) {
            lastProcessedIdentifier = token.getLexeme();
        }

        switch (token.getLexeme()) {
            case "let":
                handleVariableDeclaration();
                break;
            case "show":
                handleShow();
                break;
            case "be":
                handleAssignment();
                break;
            case "check":
                handleCondition();
                break;
            case "give":
                handleGive();
                break;
            case "task":
                handleFunctionDeclaration();
                break;
            case "repeat":
                handleForLoop();
                break;
        }
    }

    private void handleForLoop() {
        while (!tokens.isEmpty() && !tokens.peek().getLexeme().equals("}")) {
            tokens.pop();
        }
        tokens.pop();
    }


    private void handleFunctionDeclaration() {

        while(!tokens.isEmpty() && !tokens.peek().getLexeme().equals("}"))
        {
            tokens.pop();
        }
        tokens.pop();
    }

    public void handleFunctionCall(Stack<Token> functionBody) {
        Stack<Token> tempStack = new Stack<>();
        tempStack.addAll(functionBody);
        analyze(tempStack);
    }
    private void handleGive() {
        // Skip opening parenthesis
        if (!tokens.isEmpty() && tokens.peek().getLexeme().equals("(")) {
            tokens.pop();
        }

        // Get variable name
        String varName = null;
        if (!tokens.isEmpty() && tokens.peek().getType() == TokenType.IDENTIFIER) {
            varName = tokens.pop().getLexeme();
        }

        // Get variable info
        if (varName != null) {
            VariableInfo info = symbolTable.get(varName);
            if (info == null) {
                addError("Variable '" + varName + "' not declared", currentToken);
            } else {
                // Mark as initialized since it will receive input
                info.initialized = true;
            }
        }

        // Skip closing parenthesis and semicolon
        while (!tokens.isEmpty() && !tokens.peek().getLexeme().equals(";")) {
            tokens.pop();
        }
        if (!tokens.isEmpty()) {
            tokens.pop(); // pop the semicolon
        }
    }

    private void handleVariableDeclaration() {
        String type = null;
        String varName = null;

        while (!tokens.isEmpty() && !tokens.peek().getLexeme().equals(";")) {
            Token token = tokens.pop();
            if (token.getType() == TokenType.DATA_TYPE) {
                type = token.getLexeme();
            } else if (token.getType() == TokenType.IDENTIFIER) {
                varName = token.getLexeme();
            }
        }

        if (!tokens.isEmpty()) {
            tokens.pop();
        }

        if (type != null && varName != null) {
            if (symbolTable.containsKey(varName)) {
                addError("Variable '" + varName + "' already declared", currentToken);
            } else {
                // Include float in default value assignment
                String defaultValue = null;
                if (type.equals("bool")) {
                    defaultValue = "no";
                } else if (type.equals("int")) {
                    defaultValue = "0";
                } else if (type.equals("float")) {
                    defaultValue = "0.0";
                }
                VariableInfo info = new VariableInfo(type, defaultValue, false);
                symbolTable.put(varName, info);
            }
        }
    }

    private void handleShow() {
        String varName = null;

        while (!tokens.isEmpty() && !tokens.peek().getLexeme().equals(";")) {
            Token token = tokens.pop();
            if (token.getType() == TokenType.IDENTIFIER) {
                varName = token.getLexeme();
                break;
            }
        }

        if (varName != null) {
            VariableInfo info = symbolTable.get(varName);
            if (info == null) {
                addError("Variable '" + varName + "' not declared", currentToken);
            }
            else if (!info.initialized) {
                if (info.type.equals("string")) {
                    addError("String variable '" + varName + "' must be initialized before use", currentToken);
                } else if (info.type.equals("bool")) {
                    addWarning("Variable " + varName + " not initialized. Boolean Variable '" + varName + "' defaults to 'no'", currentToken);
                } else if (info.type.equals("int")) {
                    addWarning("Variable " + varName + " not initialized. Int Variable '" + varName + "' defaults to '0'", currentToken);
                } else if (info.type.equals("float")) {
                    addWarning("Variable " + varName + " not initialized. Float Variable '" + varName + "' defaults to '0.0'", currentToken);
                }
            }
        }

        while (!tokens.isEmpty() && !tokens.peek().getLexeme().equals(";")) {
            tokens.pop();
        }
        if (!tokens.isEmpty()) {
            tokens.pop();
        }
    }

    public void printErrors() {
        if (!errors.isEmpty()) {
            System.out.println("Semantic errors found:");
            for (String error : errors) {
                System.out.println(error);
            }
            //return;  // Don't print symbol table if there are errors
        }

        if (!warnings.isEmpty()) {
            System.out.println("Warnings:");
            for (String warning : warnings) {
                System.out.println(warning);
            }
        }




        // Convert the existing symbolTable to the format used by SymbolTable class
        for (Map.Entry<String, VariableInfo> entry : symbolTable.entrySet()) {
            String name = entry.getKey();
            VariableInfo info = entry.getValue();
            String type = null;
            if (info.type.equals("int")) {
                type = "Integer";
            } else if (info.type.equals("float")) {
                type = "Float";
            } else if (info.type.equals("bool")) {
                type = "Boolean";
            } else if(info.type.equals("string")) {
                type = "String";
            }
            else {
                System.out.println("Unknown type: " + info.type);
            }

            String value = info.value != null ? info.value : "";
            interpreterTable.define(name, value, type);
        }

    }


    private void handleAssignment() {
        String varName = lastProcessedIdentifier;
        Token assignmentToken = currentToken;

        if (assignmentToken.getLexeme().equals("be")) {
            VariableInfo info = symbolTable.get(varName);

            List<Token> expressionTokens = new ArrayList<>();
            while (!tokens.isEmpty()) {
                Token nextToken = tokens.peek();
                // Stop at semicolon or non-arithmetic tokens
                if (nextToken.getLexeme().equals(";") ||
                        nextToken.getLexeme().equals("show") ||
                        nextToken.getLexeme().equals("be")) {
                    break;
                }
                expressionTokens.add(tokens.pop());
            }

            if (info == null) {
                // Implicit type declaration from context
                if (!expressionTokens.isEmpty()) {
                    Token first = expressionTokens.get(0);
                    String type = null;
                    String value = first.getLexeme();
                    if (first.getType() == TokenType.LITERAL) {
                        if (isValidStringValue(value)) {
                            type = "string";
                        } else if (isValidBooleanValue(value)) {
                            type = "bool";
                        } else if (value.contains(".") && isValidFloatValue(value)) {
                            type = "float";
                        } else if (isValidIntValue(value)) {
                            type = "int";
                        }
                    }
                    if (type != null) {
                        VariableInfo newInfo = new VariableInfo(type, value, true);
                        symbolTable.put(varName, newInfo);
                        if (!tokens.isEmpty() && tokens.peek().getLexeme().equals(";")) {
                            tokens.pop();
                        }
                        return;
                    }
                }
                addError("Variable '" + varName + "' not declared", assignmentToken);
                return;
            }

            if (info.type.equals("int")) {
                try {
                    int result = evaluateExpression(expressionTokens);
                    info.value = String.valueOf(result);
                    info.initialized = true;
                } catch (IllegalArgumentException e) {
                    addError(e.getMessage(), assignmentToken);
                    info.initialized = false;
                    if (e.getMessage().contains("Invalid integer value")) {
                        addWarning(e.getMessage() + ". Defaulting to '0'", assignmentToken);
                    }
                }
            } else {
                String value = tokensToString(expressionTokens);
                handleNonIntegerAssignment(info, value, assignmentToken);
            }
        }

        // Consume the semicolon
        if (!tokens.isEmpty() && tokens.peek().getLexeme().equals(";")) {
            tokens.pop();
        }
    }

    private int evaluateExpression(List<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }

        // For single token expressions
        if (tokens.size() == 1) {
            Token token = tokens.get(0);
            String value = token.getLexeme();

            // If it's an identifier
            if (token.getType() == TokenType.IDENTIFIER) {
                VariableInfo info = symbolTable.get(value);
                if (info != null && info.initialized) {
                    try {
                        return Integer.parseInt(info.value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid integer value '" + value + "'");
                    }
                }
                throw new IllegalArgumentException("Invalid integer value '" + value + "'");
            }

            // If it's a number
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid integer value '" + value + "'");
            }
        }

        // Process tokens for arithmetic operations
        List<Token> processedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String lexeme = token.getLexeme();

            // If it's in operator position (odd index)
            if (i % 2 == 1) {
                if (lexeme.equals("+") || lexeme.equals("-") ||
                        lexeme.equals("*") || lexeme.equals("/")) {
                    throw new IllegalArgumentException("Invalid operator '" + lexeme + "'. Please use 'plus', 'minus', 'times', or 'over' instead");
                } else if (!lexeme.equals("plus") && !lexeme.equals("minus") &&
                        !lexeme.equals("times") && !lexeme.equals("over")) {
                    throw new IllegalArgumentException("Invalid operator '" + lexeme + "'. Valid operators are: plus, minus, times, over");
                } else {
                    processedTokens.add(token);
                }
            }
            // If it's in operand position (even index)
            else {
                if (isNumber(lexeme)) {
                    processedTokens.add(token);
                } else if (token.getType() == TokenType.IDENTIFIER) {
                    VariableInfo info = symbolTable.get(lexeme);
                    if (info != null && info.initialized && info.value != null) {
                        processedTokens.add(new Token(token.getType(), info.value,
                                token.getLineNumber(), token.getColumnNumber()));
                    } else {
                        throw new IllegalArgumentException("Invalid integer value '" + lexeme + "'");
                    }
                }
            }
        }

        // Convert to numeric values and operators
        List<Object> values = new ArrayList<>();
        for (Token token : processedTokens) {
            String lexeme = token.getLexeme();
            if (lexeme.equals("plus") || lexeme.equals("minus") ||
                    lexeme.equals("times") || lexeme.equals("over")) {
                values.add(lexeme);
            } else {
                try {
                    values.add(Integer.parseInt(lexeme));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid integer value '" + lexeme + "'");
                }
            }
        }

        // Process multiplication and division
        List<Object> secondPass = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Object current = values.get(i);
            if (current instanceof String &&
                    (((String) current).equals("times") || ((String) current).equals("over"))) {
                if (secondPass.isEmpty() || i >= values.size() - 1) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                int left = (int) secondPass.remove(secondPass.size() - 1);
                int right = (int) values.get(++i);

                if (((String) current).equals("times")) {
                    secondPass.add(left * right);
                } else if (right != 0) {
                    secondPass.add(left / right);
                } else {
                    throw new IllegalArgumentException("Division by zero");
                }
            } else {
                secondPass.add(current);
            }
        }

        // Process addition and subtraction
        int result = (int) secondPass.get(0);
        for (int i = 1; i < secondPass.size(); i += 2) {
            String operator = (String) secondPass.get(i);
            int number = (int) secondPass.get(i + 1);

            if (operator.equals("plus")) {
                result += number;
            } else if (operator.equals("minus")) {
                result -= number;
            }
        }

        return result;
    }

    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String tokensToString(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(t.getLexeme());
        }
        return sb.toString();
    }

    private void handleNonIntegerAssignment(VariableInfo info, String value, Token token) {
        switch (info.type) {
            case "string":
                if (isValidStringValue(value)) {
                    info.value = value;
                    info.initialized = true;
                } else {
                    addError("Invalid string value. String literals must be enclosed in quotes", token);
                    info.initialized = false;
                }
                break;
            case "bool":
                if (isValidBooleanValue(value)) {
                    info.value = value;
                    info.initialized = true;
                } else {
                    info.value = "no";
                    info.initialized = true;
                    addWarning("Invalid boolean value '" + value + "'. Defaulting to 'no'", token);
                }
                break;
            case "float":
                // Check if it's an arithmetic expression
                String[] parts = value.split(" ");
                if (parts.length == 3 && parts[1].equals("plus")) {
                    // Get the operands
                    float operand1 = getFloatOperandValue(parts[0]);
                    float operand2 = getFloatOperandValue(parts[2]);
                    if (operand1 != Float.MIN_VALUE && operand2 != Float.MIN_VALUE) {
                        info.value = String.valueOf(operand1 + operand2);
                        info.initialized = true;
                        return;
                    }
                }

                // Fall back to original behavior for non-arithmetic expressions
                if (isValidFloatValue(value)) {
                    info.value = value;
                    info.initialized = true;
                } else {
                    info.value = "0.0";
                    info.initialized = true;
                    addWarning("Invalid float value '" + value + "'. Defaulting to '0.0'", token);
                }
                break;
        }
    }

    private float getFloatOperandValue(String operand) {
        // First check if it's a direct float value
        try {
            return Float.parseFloat(operand);
        } catch (NumberFormatException e) {
            // If not a direct value, check if it's a variable
            VariableInfo info = symbolTable.get(operand);
            if (info != null && info.initialized && info.type.equals("float")) {
                return Float.parseFloat(info.value);
            }
        }
        return Float.MIN_VALUE; // Return sentinel value for invalid operands
    }

    private boolean isValidFloatValue(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidStringValue(String value) {
        return value.startsWith("\"") && value.endsWith("\"");
    }

    private boolean isValidIntValue(String value) {
        // Remove any non-digit characters
        String numericPart = value.replaceAll("[^-?\\d.]", "");

        // If no digits left, it's invalid
        if (numericPart.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(numericPart);
            return numericPart.equals(value); // Only true if original value was purely numeric
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void handleCondition() {
        String varName = null;

        while (!tokens.isEmpty() && !tokens.peek().getLexeme().equals(";")) {
            Token token = tokens.pop();
            if (token.getType() == TokenType.IDENTIFIER) {
                varName = token.getLexeme();
                break;
            }
        }

        if (varName != null) {
            VariableInfo info = symbolTable.get(varName);
            if (info == null) {
                addError("Variable '" + varName + "' not declared", currentToken);
            } else if (!info.initialized) {
                addError("Variable '" + varName + "' used before initialization", currentToken);
            }
        }
    }

    public boolean hasOnlyInitializationErrors() {
        for (String error : errors) {
            // Consider string initialization errors as critical errors
            if (error.contains("String variable") || (!error.contains("before initialization") && !error.contains("defaults to"))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidBooleanValue(String value) {
        return value.equals("yes") || value.equals("no") ||
                value.equals("true") || value.equals("false") ||
                value.equals("1") || value.equals("0");
    }

    private void addError(String message, Token token) {
        String error = "Line " + token.getLineNumber() + ": " + message;
        errors.add(error);
    }

    private void addWarning(String message, Token token) {
        String warning = "Line " + token.getLineNumber() + ": Warning - " + message;
        warnings.add(warning);
    }


    private static class VariableInfo {
        String type;
        String value;
        boolean initialized;
        boolean invalidAttempt;  // Track invalid attempts

        VariableInfo(String type, String value, boolean initialized) {
            this.type = type;
            this.value = value;
            this.initialized = initialized;
            this.invalidAttempt = false;
        }
    }
    public void addToSymbolTable(String varName, String type, String value) {
        VariableInfo info = new VariableInfo(type, value, true);
        symbolTable.put(varName, info);
    }

    public SymbolTable getSymbolTable() {
        return interpreterTable;
    }
    public void setSymbolTable(SymbolTable symbolTable) {
        this.interpreterTable = symbolTable;
    }
    public void printSymbolTable() {;
        interpreterTable.printSymbols();
    }

}