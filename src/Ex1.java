import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class Ex1 {
    public static void main(String[] args) throws IOException {
        String in= "input.txt";
        BufferedReader file = new BufferedReader(new FileReader("input.txt"));
        String xmlName=file.readLine();
        ArrayList<Variable> variables=readFromXml(xmlName);

        FileWriter myWriter = new FileWriter("output.txt");

//        for(int i=0;i<variables.size();i++)
//            System.out.println(variables.get(i));
        String line;
        while ((line = file.readLine()) != null){
            if(isBayesBall(line)){
                System.out.println("bayes ball:");
                ArrayList<Variable> evidence = new ArrayList<>();
                ArrayList<Variable> isIn = new ArrayList<>();
                extract_for_bayesBall(isIn,evidence,variables,line);
//                System.out.println("Start: " + isIn.get(1));
//                System.out.println("End: " + isIn.get(0));
//                System.out.println("Evidence: " + evidence);
                bayesBall bayesBallInstance = new bayesBall();
                System.out.printf("The target is: "+isIn.get(0).name);
                System.out.println("The start is: "+isIn.get(1).name);
                if(bayesBallInstance.bayesBall(isIn.get(1),isIn.get(0),evidence) && bayesBallInstance.bayesBall(isIn.get(0),isIn.get(1),evidence)) {
                    System.out.println(isIn.get(1).name + " and " + isIn.get(0).name + " are independent");
                    myWriter.write("yes\n");
                }
                else {
                    System.out.println(isIn.get(1).name + " and " + isIn.get(0).name + " are dependent");
                    myWriter.write("no\n");
                }
            }
            else{
                System.out.println("Variable Elimination:");
                ArrayList<Variable> evidence = new ArrayList<>();
                ArrayList<String> evidenceOutcome = new ArrayList<>();
                ArrayList<Variable> isIn = new ArrayList<>();
                ArrayList<Variable> order = new ArrayList<>();
                ArrayList<String> queryOutcome = new ArrayList<>();
                extract_for_elimination(isIn,evidence,order,variables,line,evidenceOutcome,queryOutcome);
//                Collections.reverse(order);
//                System.out.println("Start: " + isIn.get(0).name);
//                System.out.println("evidence: " + evidence);
//                System.out.println("evidenceOutcome: " + evidenceOutcome);
//                System.out.println("order: " + order.get(0).name);
//                System.out.println("query outcome "+ queryOutcome.get(0));
                variableElimination variableEliminationInstance = new variableElimination();
                variableEliminationInstance.variableElimination(isIn.get(0),variables,order,evidence,evidenceOutcome,myWriter,queryOutcome);
            }
        }
        String line2;
        myWriter.close();
        BufferedReader file2 = new BufferedReader(new FileReader("output.txt"));
        while ((line2 = file2.readLine()) != null){
            System.out.println(line2);
        }
    }

    private static void extract_for_elimination(ArrayList<Variable> isIn, ArrayList<Variable> evidence, ArrayList<Variable> order, ArrayList<Variable> variables, String line, ArrayList<String> evidenceOutcome, ArrayList<String> queryOutcome) {
        String[] parts = line.split(" ");
        String probabilityPart = parts[0];
        String orderPart = parts.length > 1 ? parts[1] : "";

        // Extract the variables inside the P()
        int startIndex = probabilityPart.indexOf('(') + 1;
        int endIndex = probabilityPart.indexOf(')');
        String insideP = probabilityPart.substring(startIndex, endIndex);

        // Split the insideP part around the "|" if it exists
        String[] conditionalParts = insideP.split("\\|");
        String leftOfPipe = conditionalParts[0];
        String rightOfPipe = conditionalParts.length > 1 ? conditionalParts[1] : "";

        // Add variables to isIn
        String[] leftVariables = leftOfPipe.split(",");
        for (String var : leftVariables) {
            String[] nameValue = var.split("=");
            for (Variable variable : variables) {
                if (variable.name.equals(nameValue[0])) {
                    isIn.add(variable);
                    queryOutcome.add(nameValue[1]);
                    break;
                }
            }
        }

        // Add variables to evidence
        if (!rightOfPipe.isEmpty()) {
            String[] rightVariables = rightOfPipe.split(",");
            for (String var : rightVariables) {
                String[] nameValue = var.split("=");
                for (Variable variable : variables) {
                    if (variable.name.equals(nameValue[0])) {
                        evidence.add(variable);
                        evidenceOutcome.add(nameValue[1]);
                        break;
                    }
                }
            }
        }

        // Add variables to order
        if (!orderPart.isEmpty()) {
            String[] orderVariables = orderPart.split("-");
            for (String var : orderVariables) {
                for (Variable variable : variables) {
                    if (variable.name.equals(var)) {
                        order.add(variable);
                        break;
                    }
                }
            }
        }
    }


    public static boolean isBayesBall(String line){
        int i=0;
        while(line.charAt(i)==' '){
            i++;
        }
        return !line.contains("(") && !line.contains(")");
    }

    public static void extract_for_bayesBall(ArrayList<Variable> isIn, ArrayList<Variable> evidence, ArrayList<Variable> variables, String line) {
        // Clear previous evidence
        evidence.clear();
        isIn.clear();
        // Split the line into the left and right parts
        String[] parts = line.split("\\|");
        if (parts.length < 1) {
            System.err.println("Invalid line format: " + line);
            return;
        }

        String leftPart = parts[0]; // B-E
        String rightPart = parts.length > 1 ? parts[1] : ""; // J=T

        // Extract start and end variables from the left part
        String[] leftVariables = leftPart.split("-");
        if (leftVariables.length < 2) {
            System.err.println("Invalid left part format: " + leftPart);
            return;
        }

        String startChar = leftVariables[0]; // B
        String endChar = leftVariables[1]; // E

        // Find the start and end variables in the list
        for (Variable variable : variables) {
            if (variable.name.equals(startChar)) {
                isIn.add(variable);
            }
            if (variable.name.equals(endChar)) {
                isIn.add(variable);
            }
        }

        // Extract evidence from the right part if it exists
        if (!rightPart.isEmpty()) {
            String[] evidencePairs = rightPart.split("=");
            if (evidencePairs.length == 2) {
                String evidenceChar = evidencePairs[0]; // J
                for (Variable variable : variables) {
                    if (variable.name.equals(evidenceChar) ) {
                        evidence.add(variable);
                    }
                }
            }
            else{
                for(int i=0;i<rightPart.length()-1;i++){
                    if(rightPart.charAt(i+1)=='='){
                        for (Variable variable : variables) {
                            if (variable.name.charAt(0) == rightPart.charAt(i)) {
                                evidence.add(variable);
                            }
                        }
                    }
                }
            }
        }
    }
    public static ArrayList<Variable> readFromXml(String line) {
        ArrayList<Variable> variables = new ArrayList<>();
        try {
            File inputFile = new File(line);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList variableList = doc.getElementsByTagName("VARIABLE");
            for (int i = 0; i < variableList.getLength(); i++) {
                Node variableNode = variableList.item(i);
                if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element variableElement = (Element) variableNode;
                    String name = variableElement.getElementsByTagName("NAME").item(0).getTextContent();
                    Variable variable = new Variable(name);

                    NodeList outcomeList = variableElement.getElementsByTagName("OUTCOME");
                    for (int j = 0; j < outcomeList.getLength(); j++) {
                        variable.addOutcome(outcomeList.item(j).getTextContent());
                    }
                    variable.numberOfOutcomes = outcomeList.getLength();
                    variables.add(variable);
                }
            }

            NodeList definitionList = doc.getElementsByTagName("DEFINITION");
            List<Definition> definitions = new ArrayList<>();

            for (int i = 0; i < definitionList.getLength(); i++) {
                Node definitionNode = definitionList.item(i);
                if (definitionNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element definitionElement = (Element) definitionNode;
                    String forVar = definitionElement.getElementsByTagName("FOR").item(0).getTextContent();
                    Definition definition = new Definition(forVar);

                    NodeList givenList = definitionElement.getElementsByTagName("GIVEN");
                    for (int j = 0; j < givenList.getLength(); j++) {
                        Variable var1 = null;
                        Variable var2 = null;

                        for (Variable v : variables) {
                            if (v.name.equals(forVar)) {
                                var1 = v;
                                break;
                            }
                        }
                        for (Variable v2 : variables) {
                            if (v2.name.equals(givenList.item(j).getTextContent())) {
                                var2 = v2;
                                break;
                            }
                        }
                        definition.addGiven(givenList.item(j).getTextContent(), var1, var2);
                    }

                    String table = definitionElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    definition.setTable(table);

                    definitions.add(definition);
                }
            }

            Map<Variable, String[][]> tablesMap = new HashMap<>();

            // Process each definition and store the CPT in the map
            for (Definition def : definitions) {
                Variable var = null;
                for (Variable v : variables) {
                    if (v.name.equals(def.forVar)) {
                        var = v;
                        break;
                    }
                }
                String[][] cptTable = createTable(def, var);
                assert var != null;
                var.setTable(cptTable);
                if (cptTable == null) {
                    System.err.println("Error: CPT table for variable " + def.forVar + " is null.");
                } else {
                    tablesMap.put(var, cptTable);
                }
            }

            // Example: print out the CPT table for each variable (for debugging purposes)
//            for (Variable variable : variables) {
//                String[][] cptTable = tablesMap.get(variable);
//                if (cptTable != null) {
//                    variable.setTable(cptTable);
//                    System.out.println("Table of posibilities for Variable: " + variable.name);
//                    printTable(variable.table);
////                    System.out.println(variable.table);
//                    System.out.println();
//                } else {
//                    System.err.println("No CPT table found for variable: " + variable.name);
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return variables;
    }

    private static String[][] createTable(Definition definition, Variable var) {
        if (var == null) {
            System.err.println("Error: Variable is null.");
            return null;
        }

        if(definition==null){
            System.err.println("Error: Definition is null.");
            return null;
        }

        if (definition.probabilities == null || definition.givens == null) {
            System.err.println("Error: Definition probabilities or givens are null.");
            return null;
        }

        int numOutcomes = var.outcomes.size();
        int numGivenCombinations = definition.probabilities.size() / numOutcomes;

        // Create the table with appropriate size, including header row
        String[][] table = new String[numGivenCombinations * numOutcomes + 1][definition.givens.size() + 2];

        // Fill in the header row
        for (int j = 0; j < definition.givens.size(); j++) {
            table[0][j] = definition.givens.get(j);
        }

        table[0][definition.givens.size()] = definition.forVar;
        table[0][definition.givens.size() + 1] = "pro";

        // Fill the table rows with the given combinations, outcomes, and probabilities
        int counter = var.outcomes.size();

        // Set the variable values on the last column
        for (int i = 1; i < numGivenCombinations * numOutcomes + 1; i++) {
            table[i][definition.givens.size()] = var.outcomes.get((i - 1) % counter);
        }

        // Set the parents values
        for (int j = definition.givens.size() - 1; j >= 0; j--) {
            for (int i = 1; i < numGivenCombinations * numOutcomes + 1; i++) {
                table[i][j] = var.parents.get(j).outcomes.get(((i - 1) / counter) % var.parents.get(j).outcomes.size());
            }
            counter *= var.parents.get(j).outcomes.size();
        }

        // Set the probability
        for (int i = 1; i < numGivenCombinations * numOutcomes + 1; i++) {
            double prob = definition.probabilities.get(i - 1);
            table[i][definition.givens.size() + 1] = String.format("%.5f", prob);
//            table[i][definition.givens.size() + 1]=String.format(String.valueOf(prob));
        }

        return table;
    }

    private static void printTable(String[][] table) {
        if (table == null) {
            System.err.println("Error: Cannot print a null table.");
            return;
        }
        // Print the header
        for (String[] row : table) {
            for (String cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }
}