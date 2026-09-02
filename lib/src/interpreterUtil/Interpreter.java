package lib.src.interpreterUtil;

import lib.src.semanticutil.SemanticAnalyzer;
import lib.src.tokenutil.Token;
import lib.src.tokenutil.TokenType;

import java.util.*;

import static lib.src.interpreterUtil.interUtil.*;

public class Interpreter {
    Stack <Token> inputStack;
    SymbolTable symbolTable = new SymbolTable();
    Map <Integer, String> paramTable = new HashMap<>();
    boolean check = false;
    boolean orcheck = false;
    public Interpreter()
    {

    }
    public Interpreter(Stack<Token> inputStack)
    {
        this.inputStack = inputStack;
        evaluate();
    }

    public void evaluate() {
        //System.out.println(inputStack);//DEBUG
        while (!inputStack.isEmpty()) {
            Token current = inputStack.peek(); // Look at the top
            // System.out.println("Evaluating " + current.getItem());//DEBUG
            switch (current.getItem()) {
                case "let" : handleDeclaration(); break;
                case "IDENTIFIER" : handleAssignmentOrExpression(); break;
                case "show" : handleShow(); break;
                case "give" : handleGive(); break;
                case "repeat" : handleForLoop(); break;
                case "check": handleIfStatement(); break;
                case "orcheck": handleOrcheckStatement(); break;
                case "otherwise": handleOtherwiseStatement(check, orcheck); break;
                case "while": handleWhile(); break;
                case "task": handleFunctionDeclaration(); break;

                default:
                    throw new RuntimeException("Unexpected token: " + current.getLexeme() + " at line" + current.getLineNumber() + "and column" + current.getColumnNumber());
            }
        }
    }

    private void handleWhile() {
        inputStack.pop(); // pop 'while'
        inputStack.pop(); // pop '('

        List<Token> checkConditionTokens = readTokensUntil(")");// read condition
        Iterator<Token> checkConditionIterator = checkConditionTokens.iterator();
        Stack<Token> checkConditionStack = new Stack<>();
        while (checkConditionIterator.hasNext()) {
            checkConditionStack.push(checkConditionIterator.next());
        }
        checkConditionStack = reverseStack(checkConditionStack);
        inputStack.pop(); // pop '{'
        Stack<Token> tempStack = new Stack<>();
        tempStack.addAll(checkConditionStack);
        Stack<Token> whileBody = readBlockTokens();

        // Evaluate 'check' condition
        Stack <Token> conditionStack = new Stack<>();
        conditionStack.addAll(reverseStack(checkConditionStack));
        boolean checkCondition = toBoolean(evaluatePostfix(infixToPostfix(conditionStack)));
        Interpreter checkInterpreter = new Interpreter();
        checkInterpreter.symbolTable = this.symbolTable;
        whileBody = reverseStack(whileBody);
        while (checkCondition) {
            Stack<Token> tempStack2 = new Stack<>();
            tempStack2.addAll(whileBody);
            checkInterpreter.inputStack = tempStack2;
            checkInterpreter.evaluate();
            checkCondition = toBoolean(evaluatePostfix(infixToPostfix(conditionStack)));
        }

    }

    private void handleDeclaration() {
        // let int i;
        inputStack.pop(); // consume 'let'
        Token dataType = inputStack.pop(); // string, int, etc.
        Token identifier = inputStack.pop(); // variable name
        Token semicolon = inputStack.pop(); // ;
        String type;

//        if (symbolTable.contains(identifier.getLexeme()))
//        {
//            type = symbolTable.getType(identifier.getLexeme());
//        }
//        else if (dataType.getLexeme().equals("string"))
//            type = "String";
//        else if (dataType.getLexeme().equals("int"))
//            type = "Integer";
//        else if (dataType.getLexeme().equals("float"))
//            type = "Float";
//        else if (dataType.getLexeme().equals("bool"))
//            type = "Boolean";
//        else
//            throw new RuntimeException("Unknown data type: " + dataType.getLexeme() + " at line " + dataType.getLineNumber() + "and column " + dataType.getColumnNumber());
        if (semicolon.getType() != TokenType.PUNCTUATION)
            throw new RuntimeException("Expected ';' after declaration at " + semicolon.getLineNumber() + ":" + semicolon.getColumnNumber());

    }


    private void handleAssignmentOrExpression() {
        if(this.symbolTable != null )
        {
            if(symbolTable.isFunction(inputStack.peek().getLexeme())){
                handleFunctionCall();
                return;
            }
        }

        Token identifier = inputStack.pop(); // variable name
        Token be = inputStack.pop(); // "be"
        Object value = null;

        if (be.getType() != TokenType.ASSIGNMENT)
            throw new RuntimeException("Expected 'be' at line" + be.getLineNumber() + " and column" + be.getColumnNumber());
        if (symbolTable.getType(identifier.getLexeme()).matches("String")&& inputStack.peek().getLexeme().charAt(0)=='"')
        {
            value = inputStack.peek().getLexeme().substring(1, inputStack.pop().getLexeme().length() - 1);
        }
        else
        {
            if (symbolTable.contains(identifier.getLexeme())&&!symbolTable.contains(inputStack.peek().getLexeme()))
            {
                value = symbolTable.get(identifier.getLexeme());
                inputStack.pop();
            }
            else
                value = evaluateExpression(); // Evaluate expr until semicolon
        }
        Token semicolon = inputStack.pop();
        if (semicolon.getType() != TokenType.PUNCTUATION)
            throw new RuntimeException("Expected ';' after expression but get " + semicolon.getLexeme() + " at line " + semicolon.getLineNumber() + " and column " + semicolon.getColumnNumber());

        symbolTable.assign(identifier.getLexeme(), value); // Update value
    }

    private void handleShow() {
        inputStack.pop(); // show
        inputStack.pop(); // (
        Token var = inputStack.pop(); // IDENTIFIER
        inputStack.pop(); // )
        inputStack.pop(); // ;
        Object value;

        if (symbolTable.contains(var.getLexeme())) {
            value = symbolTable.get(var.getLexeme());
        } else value = var.getLexeme();

        System.out.println(value);
    }
    private void handleGive() {
        inputStack.pop(); // give
        inputStack.pop(); // (
        Token var = inputStack.pop(); // IDENTIFIER
        inputStack.pop(); // )
        inputStack.pop(); // ;

        System.out.print("Enter value for " + var.getLexeme() + ": ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        symbolTable.assign(var.getLexeme(), input); // assume string input
    }
    private Object evaluateExpression() {
        Stack <Token> arithmeticStack = new Stack<>();
        String expression = "";
        while (inputStack.peek().getLexeme().matches("[^;]+"))
            {
                arithmeticStack.push(inputStack.pop());
            }
        expression = evaluatePostfix(infixToPostfix(arithmeticStack));
        //EVALUATION
        return expression;
        //throw new RuntimeException("Invalid expression token: " + token.getLexeme()+" at line" + token.getLineNumber() + "and column" + token.getColumnNumber());
    }
    private void handleForLoop() {
        Stack<Token> conditionStack = new Stack<>();
        Stack<Token> updateStack = new Stack<>();
        Stack<Token> tempStack = new Stack<>();
        Stack<Token> tempStack2;

        inputStack.pop(); // pop 'repeat'
        inputStack.pop(); // pop '('
        inputStack.pop(); // pop 'let'
        Token dataType = inputStack.pop(); // string, int, etc.
        Token identifier = inputStack.pop(); // variable name
        inputStack.pop(); // pop 'be'
        Object initialValue = inputStack.pop().getLexeme(); // initial value
        inputStack.pop(); //pop ';'

        while(!inputStack.peek().getLexeme().equals(";"))
        {
            conditionStack.push(inputStack.pop());
        }
        inputStack.pop(); //pop ';'
        while(!inputStack.peek().getLexeme().equals(")"))
        {
            updateStack.push(inputStack.pop());
        }
        updateStack = reverseStack(updateStack);
        inputStack.pop(); //pop ')'
        inputStack.pop(); //pop '{'
        tempStack.addAll(conditionStack);

        //Get body
        Stack<Token> forBody = readBlockTokens();
        Stack<Token> tempStack3 = new Stack<>();
        tempStack3.addAll(reverseStack(forBody));


        // Evaluate 'check' condition

        Interpreter forInterpreter = new Interpreter();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        semanticAnalyzer.setSymbolTable(this.symbolTable);
        semanticAnalyzer.addToSymbolTable(identifier.getLexeme(), dataType.getLexeme(), initialValue.toString());
        semanticAnalyzer.printErrors();

        forInterpreter.symbolTable = semanticAnalyzer.getSymbolTable();

        Boolean checkCondition = forInterpreter.checkRepeatCondition(tempStack);
        while (checkCondition) {
            tempStack2 = new Stack<>();
            forInterpreter.putInputStack(tempStack3);
            forInterpreter.evaluate();
            tempStack2.addAll(updateStack);
//            forInterpreter.putInputStack(tempStack2);
//            forInterpreter.evaluate();
            forInterpreter.updateRepeatCondition(tempStack2);
            checkCondition = forInterpreter.checkRepeatCondition(tempStack);
        }
    }
    private void handleIfStatement() {
        check = false;
        // CHECK STATEMENT HANDLER
        inputStack.pop(); // pop 'check'
        inputStack.pop(); // pop '('

        List<Token> checkConditionTokens = readTokensUntil(")");// read condition
        Iterator<Token> checkConditionIterator = checkConditionTokens.iterator();
        Stack<Token> checkConditionStack = new Stack<>();
        while (checkConditionIterator.hasNext()) {
            checkConditionStack.push(checkConditionIterator.next());
        }
        checkConditionStack = reverseStack(checkConditionStack);
        inputStack.pop(); // pop '{'
        Stack<Token> checkBody = readBlockTokens();

        // Evaluate 'check' condition
        boolean checkCondition = toBoolean(evaluatePostfix(infixToPostfix(reverseStack(checkConditionStack))));
        if (checkCondition) {
            check = true;
            Interpreter checkInterpreter = new Interpreter();
            checkInterpreter.symbolTable = this.symbolTable;
            checkInterpreter.inputStack = reverseStack(checkBody);
            checkInterpreter.evaluate();
        }

        if (!checkCondition && inputStack.peek().getLexeme().equals("orcheck")) {
                handleOrcheckStatement();
        }
    }

    private void handleOrcheckStatement() {
        orcheck = false;

        inputStack.pop(); // pop 'orcheck'
        inputStack.pop(); // pop '('
        List<Token> orcheckConditionTokens = readTokensUntil(")");
        Iterator<Token> orcheckConditionIterator = orcheckConditionTokens.iterator();
        Stack<Token> orcheckConditionStack = new Stack<>();
        while (orcheckConditionIterator.hasNext()) {
            orcheckConditionStack.push(orcheckConditionIterator.next());
        }
        orcheckConditionStack = reverseStack(orcheckConditionStack);
        inputStack.pop(); // pop '{'
        Stack<Token> orcheckBody = readBlockTokens();

        // Evaluate 'orcheck' condition
        boolean orcheckCondition = toBoolean(evaluatePostfix(infixToPostfix(orcheckConditionStack)));
        if (orcheckCondition&&!check) {
            orcheck = true;
            Interpreter orcheckInterpreter = new Interpreter();
            orcheckInterpreter.symbolTable = this.symbolTable;
            orcheckInterpreter.inputStack = reverseStack(orcheckBody);
            orcheckInterpreter.evaluate();
        }
        if (!orcheckCondition && inputStack.peek().getLexeme().equals("otherwise")) handleOtherwiseStatement(check, orcheck);
    }

    private void handleOtherwiseStatement(boolean a, boolean b) {
        inputStack.pop(); // pop 'otherwise'
        inputStack.pop(); // pop '{'

        Stack<Token> otherwiseBody = readBlockTokens();
        if(!check && !orcheck)
        {
            Interpreter otherwiseInterpreter = new Interpreter();
            otherwiseInterpreter.symbolTable = this.symbolTable;
            otherwiseInterpreter.inputStack = reverseStack(otherwiseBody);
            otherwiseInterpreter.evaluate();
        }
    }
    private void handleFunctionCall() {
        Token functionName = inputStack.pop();// pop function name
        inputStack.pop(); // pop '('
        List<Token> functionParams = readTokensUntil(")");
        Iterator<Token> functionParamsIterator = functionParams.iterator();
        Stack<Token> functionParamsStack = new Stack<>();
        while (functionParamsIterator.hasNext()) {
            functionParamsStack.push(functionParamsIterator.next());
        }
        functionParamsStack = reverseStack(functionParamsStack);
        Interpreter functionInterpreter = new Interpreter();
        try {

            functionInterpreter.symbolTable = symbolTable.getFunctionParameters(functionName.getLexeme());
        } catch (Exception e) {
            System.err.println("Error: Function '" + functionName.getLexeme() + "' not found. at line " + functionName.getLineNumber() + ", column " + functionName.getColumnNumber());
        }
        int paramCounter = 0;
        SemanticAnalyzer functionSemanticAnalyzer = new SemanticAnalyzer();

        while(!functionParamsStack.isEmpty()) {
            if(functionParamsStack.peek().getType()==TokenType.IDENTIFIER) {
                String paramName = paramTable.get(paramCounter);
                Object paramValue = symbolTable.get(paramName);
                String type = symbolTable.getType(paramName);
                functionInterpreter.symbolTable.assign(paramName,paramValue);
                functionSemanticAnalyzer.addToSymbolTable(paramName, "int", paramValue.toString());
                functionParamsStack.pop();
                paramCounter++;
            }
            if(!functionParamsStack.isEmpty())
            {
                if(functionParamsStack.peek().getLexeme().equals(",")) {
                    functionParamsStack.pop();
                }
            }
        }
        Stack<Token> functionBody = symbolTable.getFunction(functionName.getLexeme());
        functionInterpreter.putInputStack(reverseStack(functionBody));
        functionSemanticAnalyzer.setSymbolTable(functionInterpreter.symbolTable);
        functionSemanticAnalyzer.handleFunctionCall(functionInterpreter.inputStack);
        functionSemanticAnalyzer.printErrors();
        functionInterpreter.putSymbolTable(functionSemanticAnalyzer.getSymbolTable());
        functionInterpreter.evaluate();
        inputStack.pop(); //pop ';'
    }

    private void handleFunctionDeclaration() {
        SymbolTable functionTable = new SymbolTable();

        inputStack.pop(); // pop 'task'
        String returnType = inputStack.pop().getLexeme();
        String functionName = inputStack.pop().getLexeme();// pop function name

        inputStack.pop(); // pop '('

        List<Token> functionParams = readTokensUntil(")");
        Iterator<Token> functionParamsIterator = functionParams.iterator();
        Stack<Token> functionParamsStack = new Stack<>();
        while (functionParamsIterator.hasNext()) {
            functionParamsStack.push(functionParamsIterator.next());
        }
        functionParamsStack = reverseStack(functionParamsStack);
        int paramCounter = 0;
        String dataType = "";
        while(!functionParamsStack.isEmpty()) {
            if(functionParamsStack.peek().getType()==TokenType.DATA_TYPE) {
                switch (functionParamsStack.peek().getLexeme()) {
                    case "int":
                        dataType = "Integer";
                        functionParamsStack.pop();
                        break;
                    case "float":
                        dataType = "Float";
                        functionParamsStack.pop();
                        break;
                    case "string":
                        dataType = "String";
                        functionParamsStack.pop();
                        break;
                    case "bool":
                        dataType = "Boolean";
                        functionParamsStack.pop();
                        break;
                }

                String paramName = functionParamsStack.pop().getLexeme(); // pop ID;
                //System.out.println("Parameter name: " + paramCounter + ", Data Type: " + dataType);
                paramTable.putIfAbsent(paramCounter, paramName);
                functionTable.define(paramTable.get(paramCounter),null, dataType);
                paramCounter++;
            }
            if(!functionParamsStack.isEmpty())
            {
                if(functionParamsStack.peek().getLexeme().equals(","))
                {
                    functionParamsStack.pop();
                }
            }
//            else
//            {
//                System.out.println(functionParamsStack);
//            }
        }

        switch (returnType) {
            case "int":
                returnType = "Integer";
                break;
            case "float":
                returnType = "Float";
                break;
            case "string":
                returnType = "String";
                break;
            case "bool":
                returnType = "Boolean";
                break;
        }

        inputStack.pop(); // pop '{'
        Stack <Token>functionStack = readBlockTokens();
        symbolTable.assignFunction(functionName,functionStack , returnType);
        symbolTable.addFunctionParameters(functionName, functionTable);
    }

    private String infixToPostfix(Stack<Token> infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<String> stack = new Stack<>();

        for (Token token : infix) {
            String lexeme = token.getLexeme();
            if (isFloat(lexeme) || isInteger(lexeme) ||isVariable(lexeme) ||isBoolean(lexeme)||isString(lexeme)) {
                postfix.append(lexeme).append(" ");
            } else if (lexeme.equals("(")) {
                stack.push(lexeme);
            } else if (lexeme.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.append(stack.pop()).append(" ");
                }
                if (!stack.isEmpty()) stack.pop(); // Pop '('
            } else if (isArithmeticOp(lexeme) || isComparisonOp(lexeme)) {
                while (!stack.isEmpty() && precedence(lexeme) <= precedence(stack.peek())) {
                    postfix.append(stack.pop()).append(" ");
                }
                stack.push(lexeme);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop()).append(" ");
        }
        return postfix.toString();
    }
    private boolean isVariable(String lexeme) {
        return symbolTable.contains(lexeme);
    }
    private List<Token> readTokensUntil(String delimiter) {
        //System.out.println("Reading tokens until " + delimiter);
        List<Token> tokens = new ArrayList<>();
        while (!inputStack.peek().getLexeme().equals(delimiter)) {
            //System.out.println("Popping: " + inputStack.peek().getLexeme());
            tokens.add(inputStack.pop());
        }
        inputStack.pop(); // pop delimiter
        return tokens;
    }
    private Stack<Token> readBlockTokens() {
        Stack<Token> body = new Stack<>();
        int openBraces = 1;
        while (openBraces > 0) {
            //System.out.println("Reading block tokens: " + inputStack.peek().getLexeme());
            Token token = inputStack.pop();
            if (token.getLexeme().equals("{"))
            {
                openBraces++;
            }
            else if (token.getLexeme().equals("}"))
            {
                openBraces--;
            }
            if (openBraces > 0) body.push(token);
        }
        return body;
    }
    public boolean toBoolean(String value) {
        if (value.equals("yes")) return true;
        else if (value.equals("no"))
            return false;
        else
            System.err.println("Error: Invalid value '" + value + "'" + "at line " + inputStack.peek().getLineNumber() +
                    ", column " + inputStack.peek().getColumnNumber());
        return false;
    }

    public Stack<Token> reverseStack(Stack<Token> originalStack) {
        Stack<Token> reversedStack = new Stack<>();
        while (!originalStack.isEmpty()) {
            reversedStack.push(originalStack.pop());
        }
        return reversedStack;
    }
    public String evaluatePostfix(String postfix) {
        Stack<String> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+"); // Split by spaces to handle multi-digit numbers
        //System.out.println("Evaluating postfix expression: " + postfix);
        String result="";

        for (String token : tokens) {
            if (isArithmeticOp(token)) {
                // Pop two operands
                String a = stack.pop();
                String b = stack.pop();

                if (isVariable(a)){
                    a = (String)symbolTable.get(a);
                }
                if (isVariable(b)){
                    b = (String)symbolTable.get(b);
                }
                // Apply the operator and push the result back
                if(isInteger(a) && isInteger(b)){
                    result = applyOperatorInteger(a, b, token);
                }
                else if(isFloat(a) || isFloat(b)){
                    result = applyOperatorFloat(a, b, token);
                }
                else {
                    throw new IllegalArgumentException("Type mismatch at line 10");
                }
                stack.push(result);
            } else if (isComparisonOp(token)) {
                String b = stack.pop();
                String a = stack.pop();
                if (isVariable(a)){
                    a = (String)symbolTable.get(a);
                    if (a == "yes")
                        a = "true";
                }
                if (isVariable(b)){
                    b = (String)symbolTable.get(b);
                    if (b == "no")
                        b = "false";
                }
                result = applyComparison(a, b, token);
                if(result.equals("true")) result="yes";
                else if(result.equals("false")) result="no";
                else throw new IllegalArgumentException("Type mismatch");
                stack.push(result); // Push result as string "true"/"false"

            } else {
                stack.push(token); // Operand
            }
        }

        // The final result will be the only element left in the stack
        return stack.pop();
    }
    private static String applyOperatorInteger(String b, String a, String operator) {
        switch (operator) {
            case "plus":
                return String.valueOf(Integer.parseInt(a) + Integer.parseInt(b));
            case "minus":
                return String.valueOf(Integer.parseInt(a) - Integer.parseInt(b));
            case "times":
                return String.valueOf(Integer.parseInt(a) * Integer.parseInt(b));
            case "over":
                return String.valueOf(Integer.parseInt(a) / Integer.parseInt(b));
            case "mod":
                return String.valueOf(Integer.parseInt(a) % Integer.parseInt(b));
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
    private static String applyOperatorFloat(String b, String a, String operator) {
        switch (operator) {
            case "plus":
                return String .valueOf(Float.parseFloat(a) + Float.parseFloat(b));
            case "minus":
                return String.valueOf(Float.parseFloat(a) - Float.parseFloat(b));
            case "times":
                return String.valueOf(Float.parseFloat(a) * Float.parseFloat(b));
            case "over":
                return String.valueOf(Float.parseFloat(a) / Float.parseFloat(b));
            case "mod":
                return String.valueOf(Float.parseFloat(a) % Float.parseFloat(b));
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
    public static String applyComparison(String a, String b, String op) {
        if (!a.matches("true|false")&&!b.matches("true|false"))
        {
            double left = Double.parseDouble(a);
            double right = Double.parseDouble(b);

            switch (op) {
                case "is":
                    return String.valueOf(left == right);
                case "isnt":
                    return String.valueOf(left != right);
                case "less":
                    return String.valueOf(left < right);
                case "more":
                    return String.valueOf(left > right);
                case "lesseq":
                    return String.valueOf(left <= right);
                case "moreeq":
                    return String.valueOf(left >= right);
                default:
                    throw new IllegalArgumentException("Unknown comparison operator: " + op);
            }
        }
        else
        {
            boolean left = Boolean.getBoolean(a);
            boolean right = Boolean.getBoolean(b);

            switch (op) {
                case "is":
                    return left == right ? "true" : "false";
                case "isnt":
                    return left == right ? "true" : "false";
                default:
                    throw new IllegalArgumentException("Unknown comparison operator: " + op);
            }
        }
    }
    public void printSymbolTable() {
        symbolTable.printSymbols();
    }
    public void putSymbolTable (SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    public void putInputStack (Stack<Token> inputStack) {
        Stack<Token> tempStack = new Stack<>();
        tempStack.addAll(inputStack);
        this.inputStack = tempStack;
    }
    public boolean checkRepeatCondition(Stack<Token> condition)
    {
        return toBoolean(evaluatePostfix(infixToPostfix(condition)));
    }
    public void updateRepeatCondition(Stack<Token> condition) {
        Stack<Token> tempStack = new Stack<>();
        tempStack.addAll(condition);
        String variable = tempStack.pop().getLexeme();
        tempStack.pop();

        String result = evaluatePostfix(infixToPostfix(tempStack));
        symbolTable.assign(variable, result);
    }
}
