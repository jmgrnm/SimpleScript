package lib.src.generators;

import java.io.*;
import java.util.*;

public class FirstFollowSetGenerator {

    private Map<String, List<List>> grammar = new HashMap<>();
    private Set<String> items = new HashSet<>();
    private Map<String, Set<String>> firstSets = new HashMap<>();
    private Map<String, Set<String>> followSets = new HashMap<>();
    private Set<String> terminals = new HashSet<>();
    private Set<String> nonTerminals = new HashSet<>();


    public FirstFollowSetGenerator(String fileName) {
        readGrammarFromFile(fileName);
        calculateFirstSets();
        removeBracketsFirst();
        calculateFollowSets();
        removeBracketsFollow();

//                    for (String key : grammar.keySet()) //DEBUG
//            {
//                System.out.println("Key: " + key + " -> Values: " + grammar.get(key));
//            }
//        Iterator<String> iterator = items.iterator();
//        while (iterator.hasNext())
//        {
//            String item = iterator.next();
//            System.out.println("FIRST("+ item + ") = " + firstSets.get(item));//DEBUG
//        }
//        Iterator<String> iterator2 = nonTerminals.iterator();
//        while (iterator2.hasNext())
//        {
//            String item = iterator2.next();
//            System.out.println("Follow("+ item + ") = " + followSets.get(item));//DEBUG
//        }
    }

    // Read the grammar from a file
    private void readGrammarFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.trim().isEmpty()) continue;

                String[] rule = line.split("->");
                String lhs = rule[0].trim();
                items.add(lhs);
                List<List> rhsStack = new ArrayList<>();
                List<String> rhs = new ArrayList<>();
                for (String symbol : rule[1].trim().split("\\s+")) {
                    rhs.add(symbol);
                    items.add(symbol);
                }
                if (!grammar.containsKey(lhs))
                {
                    rhsStack.add(rhs);
                    grammar.put(lhs,rhsStack);
                }
                else
                {
                    for(List<List> list : grammar.get(lhs))
                    {
                        rhsStack.add(list);
                    }
                    rhsStack.add(rhs);
                    grammar.put(lhs,rhsStack);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Calculate the First set for each non-terminal
    private void calculateFirstSets() {
        Iterator<String> iterator = items.iterator();
        for(SetTerminals setTerminal : SetTerminals.values()) //Load Terminal Items
        {
            terminals.add(setTerminal.getStringValue());
        }
        while (iterator.hasNext()) {
            String item = iterator.next();
            if(terminals.contains(item)) //Will Check if Terminal, if not continue
            {
                Set<String> terminal = new HashSet<>();
                terminal.add(item);
                firstSets.put(item, terminal);
                followSets.putIfAbsent(item, new HashSet<>());
            }
            else
            {
                followSets.putIfAbsent(item, new HashSet<>());
                nonTerminals.add(item);
            }
        }

        for (String nonTerminal : nonTerminals) {
            firstSets.putIfAbsent(nonTerminal, new HashSet<>());
            firstSets.put(nonTerminal, calculateFirst(nonTerminal));
        }
        int iterationCount = 0;
        do {
            iterationCount++;
            for (String nonTerminal : nonTerminals) {
                reCheckFirst(nonTerminal);
            }
        }while (iterationCount <= 10);
    }

    // Helper method to calculate First set
    private Set<String> calculateFirst(String symbol) {
        Set<String> firstSet = new HashSet<>();
        if (!grammar.containsKey(symbol)) {
            firstSet.add(symbol); // Terminal
            return firstSet;
        }
        int stackCounter = 0;
        for (List <String> production : grammar.get(symbol)) {
            Set <String> newProduction = new HashSet<>();
            if(grammar.get(symbol).size()>1)
            {
                if(firstSets.get(symbol).contains(grammar.get(symbol).get(stackCounter).get(0).toString()))
                    continue;
                else
                {
                    newProduction.add(grammar.get(symbol).get(stackCounter++).get(0).toString());
                    firstSet.add(newProduction.toString());
                }
            }
            else
            {
                newProduction.add(grammar.get(symbol).get(0).get(0).toString());
                firstSet.add(newProduction.toString());
            }

            if ((stackCounter) == grammar.get(symbol).size())
                break;
        }
        return firstSet;
    }

    private void reCheckFirst(String symbol)
    {
        Set<String> firstSet = new HashSet<>();
        Iterator<String> iterator = firstSets.get(symbol).iterator();

        while (iterator.hasNext())
        {
            String terminal = iterator.next();
            if (terminal.charAt(0) == '[')
            {
                terminal = terminal.substring(1, terminal.length()-1);
            }
            if (grammar.containsKey(terminal) && !terminal.equals("ε"))
            {
                for (String terminals : firstSets.get(terminal))
                {
                    if (terminals.charAt(0) == '[')
                    {
                        terminals = terminals.substring(1, terminals.length()-1);
                    }
                    firstSet.add(terminals);
                }
                firstSets.put(symbol, firstSet);
            }
        }
    }
    private void removeBracketsFirst()
    {
        for(String symbol : firstSets.keySet())
        {
            Iterator<String> iterator = firstSets.get(symbol).iterator();
            Set<String> newSet = new HashSet<>();
            while (iterator.hasNext())
            {
                String terminal = iterator.next();
                if (terminal.charAt(0) == '[')
                {
                    terminal = terminal.substring(1, terminal.length()-1);
                }
                //System.out.println(terminal);//DEBUG
                newSet.add(terminal);
            }
            firstSets.put(symbol, newSet);
        }
    }
    private void removeBracketsFollow()
            {for(String symbol : followSets.keySet())
        {
            Set<String> newSet = new HashSet<>();
            Iterator<String> iterator = followSets.get(symbol).iterator();
            while (iterator.hasNext())
            {
                String terminal = iterator.next();
                if (terminal.charAt(0) == '[')
                {
                    terminal = terminal.substring(1, terminal.length()-1);
                }
                //System.out.println(terminal);//DEBUG
                if(!terminal.equals("ε"))
                {
                    newSet.add(terminal);
                }
            }
            followSets.put(symbol, newSet);
        }
        followSets.remove("ε");
    }

    // Calculate the Follow set for each non-terminal
    private void calculateFollowSets() {
        //System.out.println("Calculating Follow Sets...");
        Set<String> startSymbol = new HashSet<>();
        startSymbol.add("$");
        followSets.put("START_PRIME", startSymbol);
        for (int i=0; i<5; i++) {
            calculateFollow();
            calculateFollow();
        }
        removeEpsilonFollow();
    }

    private void removeEpsilonFollow()
    {

    }

    // Helper method to calculate Follow set
    private void calculateFollow() {

        boolean changed = true;
        do {
            changed = false;
            // System.out.println("Running....");
            for (Map.Entry<String, List<List>> entry : grammar.entrySet()) {
                String nonTerminal = entry.getKey();
                List<List> productions = entry.getValue();
                //System.out.println(nonTerminal + " -> " + productions);//Debug

                for (List<String> production : productions) {
                    for (int i = 0; i < production.size(); i++) { // ITERATE EACH ITEM IN PRODUCTION
                        String symbol = production.get(i);
                        //System.out.println("For Symbol : " + symbol + "of" + production);//Debug
                        if (nonTerminals.contains(symbol)) { // IF IT IS A NON-TERMINAL
                            // Case 1: A -> αBβ
                            if (i + 1 < production.size()) {
                                String nextSymbol = production.get(i + 1);
                                // Case 1.1: if β is non-terminal
                                if (grammar.containsKey(nextSymbol))
                                {
                                    Set<String> firstSetNextSymbol = firstSets.get(nextSymbol);
                                    if (firstSetNextSymbol != null && !firstSetNextSymbol.isEmpty())
                                    { // Case 1.2: β -> ε (add FOLLOW(A) to FOLLOW(B))
                                        if (firstSetNextSymbol.contains("ε")) {
                                            Set<String> followSetA = followSets.get(nonTerminal);
                                            followSets.get(symbol).addAll(followSetA);
                                        }
                                        //System.out.println("First Symbol is " + firstSetNextSymbol );//Debug
                                        changed = followSets.get(symbol).addAll(firstSetNextSymbol);
                                    }
                                }
                                else // Case 1.3: β is terminal
                                {
                                    changed = followSets.get(symbol).add(nextSymbol);
                                }
                            }
                            // Case 2: A -> αB (add FOLLOW(A) to FOLLOW(B))
                            if (i == production.size() - 1) {
                                Set<String> followSetA = followSets.get(nonTerminal);
                                changed = followSets.get(symbol).addAll(followSetA);
                            }
                        }
                    }
                }
            }
        } while (changed); // Continue until no changes
    }
    public Map<String, Set<String>> getFirstSets() {
        return firstSets;
    }

    public Map<String, Set<String>> getFollowSets() {
        return followSets;
    }
    public Map<String, List<List>> getGrammar() {
        return grammar;
    }
}
