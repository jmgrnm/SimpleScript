package lib.src.interpreterUtil;

import lib.src.tokenutil.Token;
import lib.src.tokenutil.TokenType;

import java.util.*;

public class SymbolTable {
    private Map<String, Object> variables = new HashMap<>();
    private Map<String, String> types = new HashMap<>();
    private Map<String, Stack<Token>> functionsStack = new HashMap<>();
    private Map<String, SymbolTable> functionParameters = new HashMap<>();

    public void define(String name, Object value, String type) {
        variables.put(name, value);
        types.put(name, type);
    }

    public void assign(String name, Object value) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not declared: " + name);
        }
        variables.put(name, value);
    }
    public void assignFunction(String name, Stack<Token> body, String type) {
        variables.put(name, "function");
        types.put(name, type);
        functionsStack.put(name, body);
    }

    public Object get(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not declared: " + name);
        }
        return variables.get(name);
    }
    public String getType(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not declared: " + name);
        }
        return types.get(name);
    }
    public boolean contains(String name) {
        return variables.containsKey(name);
    }

    public void printSymbols() {
        System.out.println("=======Symbol Table=======");
        for (String key : variables.keySet()) {
            System.out.println(key + " = " + variables.get(key) + " (" + types.get(key) + ")");
        }
    }
    public boolean isFunction(String name) {
        return functionsStack.containsKey(name);
    }
    public Stack<Token> getFunction(String name) {
        if (!functionsStack.containsKey(name)) {
            throw new RuntimeException("Function not declared: " + name);
        }
        return functionsStack.get(name);
    }
    public SymbolTable getFunctionParameters(String name) {
        return functionParameters.get(name);
    }
    public void addFunctionParameters(String name, SymbolTable parameters) {
        functionParameters.put(name, parameters);
    }
}