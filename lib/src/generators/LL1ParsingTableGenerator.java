package lib.src.generators;

import java.io.*;
import com.opencsv.*;
import java.util.*;

public class LL1ParsingTableGenerator {

    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, List<List>> grammar;
    private Map<String, Map<String, String>> parsingTable;  // To store the LL(1) parsing table

    public LL1ParsingTableGenerator(Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets, Map<String, List<List>> grammar) {
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.grammar = grammar;
        this.parsingTable = new HashMap<>();
    }

    public void generateTable() {
        // Step 1: Initialize the table
        // Initialize the parsing table where keys are non-terminals and values are maps of terminals to production rules
        for (String nonTerminal : grammar.keySet()) {
            parsingTable.put(nonTerminal, new HashMap<>());
        }

        // Step 2: Fill the parsing table using FIRST and FOLLOW sets
        for (String nonTerminal : grammar.keySet()) {
            List<List> productions = grammar.get(nonTerminal);
            for (List<String> production : productions) {
                Set<String> firstSet = getFirstSet(production);
                for (String terminal : firstSet) {
                    if(terminal.charAt(0) == '[')
                    {
                        terminal = terminal.substring(1, terminal.length() - 1);
                    }
                    if (!terminal.equals("ε")) {  // We don't add "ε" directly
                        parsingTable.get(nonTerminal).put(terminal, nonTerminal + " -> " + String.join(" ", production));
                        //System.out.println("Production Created for "+ nonTerminal + ": "+parsingTable.get(nonTerminal).get(terminal));//DEBUG
                    }
                }

                // If the production can derive ε, add FOLLOW(nonTerminal) to the table
                if (firstSet.contains("ε")) {
                    //System.out.println("Production of"+ nonTerminal + "Contains epsilon" + firstSet);//DEBUG
                    Set<String> followSet = followSets.get(nonTerminal);
                    for (String terminal : followSet) {
                        //System.out.println(parsingTable.entrySet());//DEBUG
                        parsingTable.get(nonTerminal).put(terminal, nonTerminal + " -> " + String.join(" ", production));
                        //System.out.println("Production Created for "+ nonTerminal + ": "+parsingTable.get(nonTerminal).get(terminal));//DEBUG
                    }
                }
            }
        }

        // Step 3: Write the parsing table to a CSV file
        //printParsingTable();
    }

    private Set<String> getFirstSet(List<String> production) {
        Set<String> firstSet = new HashSet<>();
        if (production.size() == 0) {
            firstSet.add("ε");
            return firstSet;
        }

        String symbol = production.get(0);
        if (firstSets.containsKey(symbol)) {
            firstSet.addAll(firstSets.get(symbol));
        } else {
            firstSet.add(symbol);  // It's a terminal symbol
        }

        return firstSet;
    }

    private void printParsingTable() {
        // Iterate through each non-terminal in the parsing table
        for (String nonTerminal : parsingTable.keySet()) {
            // Retrieve the map for the current non-terminal
            Map<String, String> terminalMap = parsingTable.get(nonTerminal);

            // Print the non-terminal
            System.out.println("Non-Terminal: " + nonTerminal);

            // Iterate through each terminal and its corresponding production rule
            for (Map.Entry<String, String> entry : terminalMap.entrySet()) {
                String terminal = entry.getKey();
                String production = entry.getValue();

                // Print the terminal and its corresponding production rule
                //System.out.println("  Terminal: " + terminal + " -> Production: " + production);
            }
            System.out.println();  // New line for better readability between non-terminals
        }
    }

    public Map<String, Map<String, String>> getTable() {
        return parsingTable;
    }

}