import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AImain {
    public static void main(String[] args) throws IOException {
        ArrayList<Variable> variables=readFromXml();
        for(int i=0;i<variables.size();i++)
            System.out.println(variables.get(i));

        BufferedReader file = new BufferedReader(new FileReader("C:\\Users\\elian\\IdeaProjects\\AIProject\\src\\input1.txt"));
        String xmlName=file.readLine();
        String line;
        int i=0;
        while ((line = file.readLine()) != null && i<2){
            if(isBayesBall()){
                ArrayList<Variable> evidence = new ArrayList<>();
                ArrayList<Variable> isIn = new ArrayList<>();
                extract_for_bayesBall(isIn,evidence,variables,line);
                System.out.println("Start: " + isIn.get(1));
                System.out.println("End: " + isIn.get(0));
                System.out.println("Evidence: " + evidence);
                bayesBall bayesBallInstance = new bayesBall();
                if(bayesBallInstance.bayesBall(variables,isIn.get(1),isIn.get(0),evidence))
                    System.out.println("independent");
                else
                    System.out.println("not independent");
            }
            else{

            }
            i++;
        }
    }

    public static boolean isBayesBall()
    {
        return true;
    }

//    public static void extract_for_bayesBall(Variable start, Variable end,ArrayList<Variable> evidence,ArrayList<Variable> variables, String line){
//        boolean stillBehindFirst=true;
//        boolean stillBehindSecond=true;
//        for(int i=0;i<line.length();i++){
//            System.out.println("lol");
//            if(line.charAt(i)==' ')
//                continue;
//            if(line.charAt(i)!='-'){
//               stillBehindFirst=false;
//               continue;
//            }
//            if(line.charAt(i)=='|'){
//                stillBehindSecond=false;
//                continue;
//            }
//            if(stillBehindFirst){
//                char first=line.charAt(i);
//                for(int j=0;j<variables.size();j++){
//                    if(first==variables.get(i).name.charAt(0)){
//                        start=variables.get(i);
//                        System.out.println(start);
//                        break;
//                    }
//                }
//                continue;
//            }
//            if(!stillBehindFirst && stillBehindSecond){
//                char second=line.charAt(i);
//                for(int j=0;j<variables.size();j++){
//                    if(second==variables.get(i).name.charAt(0)){
//                        end=variables.get(i);
//                        System.out.println(end);
//                        break;
//                    }
//                }
//                continue;
//            }
//            if(!stillBehindFirst && !stillBehindSecond){
//                if(line.charAt(i+1)=='='){
//                    char eviden=line.charAt(i);
//                    for(int j=0;j<variables.size();j++){
//                        if(eviden==variables.get(i).name.charAt(0)){
//                            evidence.add(variables.get(i));
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println(start+" "+end+" "+" "+evidence);
//    }

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
        }
    }
    public static ArrayList<Variable> readFromXml() {
        ArrayList<Variable> variables = null;
        try {
            File inputFile = new File("C:\\Users\\elian\\IdeaProjects\\AIProject\\src\\alarm_net.xml");
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