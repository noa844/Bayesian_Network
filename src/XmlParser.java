//noa.honigstein@gmail.com

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

public class XmlParser {
    /// Loads a Bayesian Network from an XML file and fills it into the given BNetwork object.///

    public void loadBnFromXml(String path, BNetwork bn)throws Exception{
        File xmlFile = new File(path);
        //Initialize the XML parser
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile); // object that parses and reads the XML file

        doc.getDocumentElement().normalize(); // cleans and normalizes the DOM structure

        List<Variable> variablesList = new ArrayList<>(); //temporary list to hold all variables

        NodeList variables = doc.getElementsByTagName("VARIABLE");
        //step 1: Loop to create each Variable (with name and outcomes only)
        for (int i = 0; i < variables.getLength(); i++) {
            Element varElem = (Element) variables.item(i);

            // Get variable name
            String name = varElem.getElementsByTagName("NAME").item(0).getTextContent().trim();
            // Get list of outcomes
            NodeList VarOutcome= varElem.getElementsByTagName("OUTCOME");
            List<String> outcomes = new ArrayList<>();
            for (int j = 0; j < VarOutcome.getLength(); j++) {
                outcomes.add(VarOutcome.item(j).getTextContent().trim());
            }
            // Create the Variable and add it to the network
            Variable networkVar = new Variable(name, outcomes);
            variablesList.add(networkVar); // for later lookup by name
            bn.addVariable(networkVar);   // store in the actual network

        }

        //step 2: Loop through each <DEFINITION> to set parents, probabilities, and CPT
        NodeList definitions = doc.getElementsByTagName("DEFINITION");

        for (int i = 0; i < definitions.getLength(); i++) {
            Element defElem = (Element) definitions.item(i);

            // === Parse <GIVEN> (parents) ===
            NodeList givenNodes = defElem.getElementsByTagName("GIVEN");
            List<Variable> parents = new ArrayList<>();
            for (int j = 0; j < givenNodes.getLength(); j++) {
                String parentName = givenNodes.item(j).getTextContent().trim();
                parents.add(bn.getVariable(parentName));
            }
            // === Parse <TABLE> (list of probabilities) ===
            String tableStr = defElem.getElementsByTagName("TABLE").item(0).getTextContent();
            String[] tokens = tableStr.trim().split("\\s+");
            List<Double> probabilities = new ArrayList<>();
            for (String t : tokens) {
                probabilities.add(Double.parseDouble(t));
            }

            //Get the variable name this CPT belongs to
            String varName = defElem.getElementsByTagName("FOR").item(0).getTextContent().trim();

            //Match this name with an existing Variable and set its parents, probabilities and CPT
            for (Variable var : variablesList) {
                if (var.getName().equals(varName)) {
                    var.setParents(parents);
                    var.setProbabilities(probabilities);
                    var.setCpt();
                    break;
                }
            }
        }
    }

}
