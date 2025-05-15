//noa.honigstein@gmail.com

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Ex1 {
    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

        String xmlFile = reader.readLine().trim();
        BNetwork bn = new BNetwork();
        XmlParser parser = new XmlParser();
        parser.loadBnFromXml(xmlFile, bn);

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            double result = 0.0;
            int adds = 0;
            int muls = 0;

            if (line.contains("|") && line.contains("),")) { //if request contain algo number
                int closingIndex = line.indexOf(")");
                String fullQuery = line.substring(2, closingIndex); // the part without "p(" and ")"
                int algoNumber = Integer.parseInt(line.substring(closingIndex + 2));//after "),"
                String[] mainSplit = fullQuery.split("\\|");
                String query = mainSplit[0];  // ex: "B=T"
                String evidences = mainSplit[1];// ex: "J=T,M=T"
                //query part
                String[] querySplit = query.split("="); // [ "B", "T" ]
                String queryVar = querySplit[0].trim();// "B"
                String queryVal = querySplit[1].trim();// "T"

                //evidence part
                Map<String, String> evidenceMap = new LinkedHashMap<>();

                String[] evidencePairs = evidences.split(",");
                for (String pair : evidencePairs) {
                    String[] keyVal = pair.split("=");
                    String var = keyVal[0].trim();
                    String val = keyVal[1].trim();
                    evidenceMap.put(var, val);
                }

                //algo calls
                bn.resetCounters();

                if (algoNumber == 1) {
                    result = bn.naiveAlgo(queryVar, queryVal, evidenceMap);
                } else if (algoNumber == 2) {
                    result = bn.variableElimination(queryVar, queryVal, evidenceMap, EliminationStrategy.LEX);
                } else { //(algoNumber == 3)
                    result = bn.variableElimination(queryVar, queryVal, evidenceMap, EliminationStrategy.MIN_FACTOR_SIZE);
                }
                //counters
                adds = bn.getAdditionCount();
                muls = bn.getMultiplicationCount();
            } else {
                // request without "|", ex: P(B=F,E=T,A=T,M=T,J=F)
                int closingIndex = line.indexOf(")");
                String fullQuery = line.substring(2, closingIndex);

                Map<String, String> assignment = new LinkedHashMap<>();
                String[] pairs = fullQuery.split(",");
                for (String pair : pairs) {
                    String[] keyVal = pair.split("=");
                    String var = keyVal[0].trim();
                    String val = keyVal[1].trim();
                    assignment.put(var, val);
                }

                bn.resetCounters();
                result = bn.fullJointProb(assignment);
                adds = bn.getAdditionCount();
                muls = bn.getMultiplicationCount();
            }
            Tools.roundTo5Decimals(result);

            writer.write(String.format("%.5f,%d,%d", result, adds, muls));
            writer.newLine();

        }
        reader.close();
        writer.close();


    }
}