package lib.src;

import lib.src.generators.LL1ParsingTableGenerator;
import lib.src.interpreterUtil.Interpreter;
import lib.src.interpreterUtil.SymbolTable;
import lib.src.parseutil.ASTNode;
import lib.src.parseutil.Parser;
import lib.src.generators.FirstFollowSetGenerator;
import lib.src.semanticutil.SemanticAnalyzer;
import lib.src.tokenutil.*;
import java.io.*;
import java.util.*;

import static lib.src.parseutil.Parser.reverseStack;

public class  Tester {

    public static void main(String[] args) throws IOException {
        // Specify the filename directly
        String filePath = "lib/src/source_file_03.simp";  // Replace this with your desired .simp file path
        System.out.println("Running for file : " + filePath);
        //Parsing Table

        //Generate First and Follow Sets
        FirstFollowSetGenerator firstFollowSetGenerator = new FirstFollowSetGenerator("lib/src/parseutil/newgrammar.txt");


        //Generate Parsing Table
        Map<String, Set<String>> firstSets = firstFollowSetGenerator.getFirstSets();
        Map<String, Set<String>> followSets = firstFollowSetGenerator.getFollowSets();
        Map<String, List<List>> grammar = firstFollowSetGenerator.getGrammar();
        LL1ParsingTableGenerator table = new LL1ParsingTableGenerator(firstSets, followSets, grammar);
        table.generateTable();



        // Initializing and Declaration
        SimpleScanner scanner = new SimpleScanner(filePath);
        Stack<Token> tokens = new Stack<>();
        Stack<Token> semanticTokens = new Stack<>();
        Stack<Token> interpreterStack = new Stack<>();

        // Print the tokens for debugging
        //System.out.println("Tokens:");
        Token token;
        int counter = 0;

        while ((token = scanner.getNextToken()) != null) {
            //System.out.println(token.toString() + "Token #"+counter++);
            tokens.add(token);

        }

        semanticTokens.addAll(tokens);
        semanticTokens = reverseStack(semanticTokens);
        interpreterStack.addAll(tokens);
        interpreterStack = reverseStack(interpreterStack);

        Parser parser = new Parser(table.getTable(),tokens);
        parser.parse();
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        try {
            boolean semanticSuccess = semanticAnalyzer.analyze(semanticTokens);

        }catch (Exception e) {
            System.out.println("Error during semantic analysis: " + e.getMessage());
        }
        System.out.println("\nGenerating AST Visualization...");
        parser.visualizeAST();

        semanticAnalyzer.printErrors();
        boolean isContinue = false;
        while(!isContinue){
            Scanner input = new Scanner(System.in);
            System.out.println("Continue Runtime? (y/n)");
            String response = input.nextLine();
            if(response.equals("y"))
            {
                System.out.println("==============OUTPUT=============");
                Interpreter interpreter = new Interpreter();
                interpreter.putSymbolTable(semanticAnalyzer.getSymbolTable());
                interpreter.putInputStack(interpreterStack);
                interpreter.evaluate();
                interpreter.printSymbolTable();
                isContinue = true;
            }
            else if(response.equals("n"))
                isContinue = true;
        }
        System.out.println("Program Complete");
    }
}


