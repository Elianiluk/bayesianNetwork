import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AImain {
    public static void main(String[] args) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader("/home/elian/IdeaProjects/AIProject/src/input1.txt"));
        String xmlName=file.readLine();
        ArrayList<Variable> variables=readFromXml(xmlName);

        FileWriter myWriter = new FileWriter("/home/elian/IdeaProjects/AIProject/src/output1.txt");

        for(int i=0;i<variables.size();i++)
            System.out.println(variables.get(i));
        String line;
        while ((line = file.readLine()) != null){
            if(isBayesBall(line)){
                System.out.println("bayes ball:");
                ArrayList<Variable> evidence = new ArrayList<>();
                ArrayList<Variable> isIn = new ArrayList<>();
                extract_for_bayesBall(isIn,evidence,variables,line);
                System.out.println("Start: " + isIn.get(1));
                System.out.println("End: " + isIn.get(0));
                System.out.println("Evidence: " + evidence);
                bayesBall bayesBallInstance = new bayesBall();
                if(bayesBallInstance.bayesBall(variables,isIn.get(1),isIn.get(0),evidence)) {
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
                extract_for_elimination(isIn,evidence,order,variables,line,evidenceOutcome);
                System.out.println("Start: " + isIn.get(0).name);
                System.out.println("evidence: " + evidence);
                System.out.println("evidenceOutcome: " + evidenceOutcome);
                System.out.println("order: " + order);
                variableElimination variableEliminationInstance = new variableElimination();
                variableEliminationInstance.variableElimination(isIn.get(0),variables,order,evidence);
            }
        }
        String line2;
        myWriter.close();
        BufferedReader file2 = new BufferedReader(new FileReader("/home/elian/IdeaProjects/AIProject/src/output1.txt"));
        while ((line2 = file2.readLine()) != null){
            System.out.println(line2);
        }
    }

    private static void extract_for_elimination(ArrayList<Variable> isIn, ArrayList<Variable> evidence, ArrayList<Variable> order, ArrayList<Variable> variables, String line,ArrayList<String> evidenceOutcome) {
        String[] parts = line.split(" ");
        String probabilityPart = parts[0];
        String orderPart = parts[1];

        // Extract the variables inside the P()
        int startIndex = probabilityPart.indexOf('(') + 1;
        int endIndex = probabilityPart.indexOf(')');
        String insideP = probabilityPart.substring(startIndex, endIndex);

        // Split the insideP part around the "|"
        String[] conditionalParts = insideP.split("\\|");
        String leftOfPipe = conditionalParts[0];
        String rightOfPipe = conditionalParts[1];

        // Add variables to isIn
        String[] leftVariables = leftOfPipe.split(",");
        for (String var : leftVariables) {
            String[] nameValue = var.split("=");
//            isIn.add(new Variable(nameValue[0], nameValue[1]));
            for(Variable variable : variables){
                if(variable.name.equals(nameValue[0])){
                    isIn.add(variable);
                    break;
                }
            }
        }


        // Add variables to evidence
        String[] rightVariables = rightOfPipe.split(",");
        for (String var : rightVariables) {
            String[] nameValue = var.split("=");
//            evidence.add(new Variable(nameValue[0], nameValue[1]));
            for(Variable variable : variables){
                if(variable.name.equals(nameValue[0])){
                    evidence.add(variable);
                    evidenceOutcome.add(nameValue[1]);
                    break;
                }
            }
        }

        // Add variables to order
        String[] orderVariables = orderPart.split("-");
        for (String var : orderVariables) {
//            order.add(new Variable(var, null));  // Assuming variables in order have no values
            for(Variable variable : variables){
                if(variable.name.equals(var)){
                    order.add(variable);
                    break;
                }
            }
        }
    }

    public static boolean isBayesBall(String line){
        int i=0;
        while(line.charAt(i)==' '){
            i++;
        }
        return line.charAt(i) != 'P' && line.charAt(i + 1) != '(';
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

        char startChar = leftVariables[0].charAt(0); // B
        char endChar = leftVariables[1].charAt(0); // E

        // Find the start and end variables in the list
        for (Variable variable : variables) {
            if (variable.name.charAt(0) == startChar) {
                isIn.add(variable);
            }
            if (variable.name.charAt(0) == endChar) {
                isIn.add(variable);
            }
        }

        // Extract evidence from the right part if it exists
        if (!rightPart.isEmpty()) {
            String[] evidencePairs = rightPart.split("=");
            if (evidencePairs.length == 2) {
                char evidenceChar = evidencePairs[0].charAt(0); // J
                for (Variable variable : variables) {
                    if (variable.name.charAt(0) == evidenceChar) {
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
        ArrayList<Variable> variables = null;
        try {
            File inputFile = new File("/home/elian/IdeaProjects/AIProject/src/"+line);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList variableList = doc.getElementsByTagName("VARIABLE");
            variables = new ArrayList<>();

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
                    variable.numberOfOutcomes=outcomeList.getLength();
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
//                    for(int i=0;i<variableList.getLength();i++)

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

                    for (Variable v : variables) {
                        if (v.name.equals(forVar)) {
                            v.setTable(table); // Set the probability table for the variable
                            break;
                        }
                    }

                    definitions.add(definition);
                }
            }

            // Print out variables and definitions for verification
//            for (Variable variable : variables) {
//                System.out.println(variable);
//            }

//            for (Definition definition : definitions) {
//                System.out.println(definition);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return variables;
    }
}