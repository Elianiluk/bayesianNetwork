import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class XMLReaderExample {
    public static void main(String[] args) {
        try {
            File inputFile = new File("C:\\Users\\elian\\IdeaProjects\\AIProject\\src\\alarm_net.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList variableList = doc.getElementsByTagName("VARIABLE");
            List<Variable> variables = new ArrayList<>();

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
//                        Variable var;
                        for (int l=0;i<variables.size();l++){
                            Variable var= variables.get(l);
                            definition.addGiven(var);
                            break;
                        }


                    }

                    String table = definitionElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    definition.setTable(table);

                    definitions.add(definition);
                }
            }

            // Print out variables and definitions for verification
            for (Variable variable : variables) {
                System.out.println(variable);
            }

            for (Definition definition : definitions) {
                System.out.println(definition);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}