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

        BufferedReader file = new BufferedReader(new FileReader("input1"));
        String xmlName=file.readLine();
        Variable start=null;
        Variable end=null;
        ArrayList<Variable> evidence = new ArrayList<>();
        String firstLine=file.readLine();
        String line;
        while ((line = file.readLine()) != null){
            if(isBayesBall()){
                extract_for_bayesBall(start,end,evidence,variables,line);
            }
            else{

            }
        }
    }

    public static boolean isBayesBall()
    {
        return true;
    }

    public static void extract_for_bayesBall(Variable start, Variable end,ArrayList<Variable> evidence,ArrayList<Variable> variables, String line){
        boolean stillBehindFirst=true;
        boolean stillBehindSecond=true;
        for(int i=0;i<line.length();i++){
            if(line.charAt(i)==' ')
                continue;
            if(line.charAt(i)!='-'){
               stillBehindFirst=false;
               continue;
            }
            if(line.charAt(i)=='|'){
                stillBehindSecond=false;
                continue;
            }
            if(stillBehindFirst){
                char first=line.charAt(i);
                for(int j=0;j<variables.size();j++){
                    if(first==variables.get(i).name.charAt(0)){
                        start=variables.get(i);
                        break;
                    }
                }
                continue;
            }
            if(!stillBehindFirst && stillBehindSecond){
                char second=line.charAt(i);
                for(int j=0;j<variables.size();j++){
                    if(second==variables.get(i).name.charAt(0)){
                        end=variables.get(i);
                        break;
                    }
                }
                continue;
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