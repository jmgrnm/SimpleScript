package lib.src.parseutil;

import java.util.Map;
import java.util.Stack;

import lib.src.interpreterUtil.Interpreter;
import lib.src.tokenutil.Token;
import lib.src.tokenutil.TokenType;

public class Parser {
    private Map<String, Map<String, String>> parsingTable;
    private Stack<String> parseStack;
    private Stack<Token> input;
    private Stack<Token> newInput = new Stack<>();
    private Stack<Token> stringInputStack4 = new Stack<>(); //FOR PHASE 4
    private ASTNode root;
    private Stack<ASTNode> astNodeStack = new Stack<>();

    public Parser(Map<String, Map<String, String>> parsingTable, Stack<Token> input) {
        this.parsingTable = parsingTable;
        this.input = input;
        this.parseStack = new Stack<>();
    }

    public void parse() {
        // Push the start symbol onto the stack
        parseStack.push("START_PRIME");
        for (Token token : input) {
            newInput.push(token);
        }
        stringInputStack4 = reverseStack(newInput);
        Stack<Token> interpreterStack = new Stack<>();
        interpreterStack.addAll(stringInputStack4);

        newInput = reverseStack(stringInputStack4);
        newInput.push(new Token(TokenType.EOF, "$", 0, 0));
        newInput = reverseStack(newInput);
        // Process input string
        while (!parseStack.isEmpty()) {
            String top = parseStack.peek();
            String currentInput = newInput.peek().getItem();
            Stack<String> stringInputStack = new Stack<>();
            for (Token input : newInput) {
                stringInputStack.push(input.getItem());
            }
//            System.out.println("Parsing Stack: " + parseStack);
//            System.out.println("Input Stack: " + stringInputStack);
//            System.out.println("Top of the Parsing Stack: " + top);
//            System.out.println("Top of the Input Stack: " + currentInput);
            if (isTerminal(top)) {
                if (currentInput.equals("$") && top.equals("START_PRIME")) {
                    parseStack.pop();
                    newInput.pop();
                } else if (top.equals("ε")) {
                    //System.out.println("Removing ε : " + top);
                    parseStack.pop();
                } else if (top.equals(currentInput)) {
                    //System.out.println("Match: " + top);
                    parseStack.pop();
                    newInput.pop();
                } else if(currentInput.equals("(")&&top.equals("be"))
                {
                    while (!newInput.peek().getLexeme().equals(";"))
                        newInput.pop();
                    parseStack.pop();// be
                    parseStack.pop();// LITERAL
                }else{
                    System.out.println("Syntax Error: Expected " + top + " but found " + currentInput + " at line " + newInput.peek().getLineNumber() + " and " +
                            "column " + newInput.peek().getColumnNumber());
                    return;
                }
            } else {
                Map<String, String> row = parsingTable.get(top);
                if (row != null && row.containsKey(currentInput)) {
                    String[] tempProd = row.get(currentInput).split("->");
                    String prod = tempProd[1].trim();
                    String[] rhs = prod.equals("ε") ? new String[0] : prod.split(" ");

                    //System.out.println("Applying rule: " + top + " -> " + prod);

                    ASTNode currentNode = new ASTNode(top);
                    if (root == null) {
                        root = currentNode;
                    } else if (!astNodeStack.isEmpty()) {
                        astNodeStack.peek().addChild(currentNode);
                    }

                    parseStack.pop();
                    astNodeStack.push(currentNode);

                    for (int i = rhs.length - 1; i >= 0; i--) {
                        parseStack.push(rhs[i]);
                    }

                    for (String symbol : rhs) {
                        ASTNode child = new ASTNode(symbol);
                        currentNode.addChild(child);

                        if (!isTerminal(symbol) && !symbol.equals("ε")) {
                            astNodeStack.push(child);
                        }
                    }

                    astNodeStack.pop(); // done with this rule
                } else {
                    System.out.println("Syntax Error: No rule for " + top + " with input " + currentInput + " at line " + newInput.peek().getLineNumber() + " and " +
                            "column " + newInput.peek().getColumnNumber());
                    return;
                }
            }
        }
        if (newInput.size() == 1 && newInput.peek().getItem().equals("$")) {
            System.out.println("Input parsed successfully!");
        } else {
            System.out.println("Syntax Error: Extra input remaining." + " at line " + newInput.peek().getLineNumber() + " and " +
                    "column " + newInput.peek().getColumnNumber());
        }
    }

    private boolean isTerminal(String symbol) {
        return !parsingTable.containsKey(symbol);
    }

    public static Stack<Token> reverseStack(Stack<Token> originalStack) {
        Stack<Token> reversedStack = new Stack<>();
        while (!originalStack.isEmpty()) {
            reversedStack.push(originalStack.pop());
        }
        return reversedStack;
    }

    public void visualizeAST() {
        ASTVisualizer.visualizeAST(root);
    }
}