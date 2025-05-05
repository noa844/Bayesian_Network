import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Tools {
    // Generates all possible combinations of outcomes for a given set of variables.
    public static List<Map<String, String>> generateCombinations(Collection<Variable> variables) {
        List<Map<String, String>> result = new ArrayList<>();
        result.add(new LinkedHashMap<>());// start with an empty assignment

        for (Variable var : variables) {
            List<String> values = var.getOutcomes();// possible values for this variable
            List<Map<String, String>> newResult = new ArrayList<>();

            for (Map<String, String> partial : result) {
                for (String val : values) {
                    Map<String, String> newCombination = new LinkedHashMap<>(partial);
                    newCombination.put(var.getName(), val);// assign this value to the variable
                    newResult.add(newCombination);// add the new combination to the list
                }
            }

            result = newResult;// update the result list
        }

        return result;
    }
    // Rounds a double value to 5 decimal places using HALF_UP rounding mode.
    public static double roundTo5Decimals(double result) {
        return new BigDecimal(result)
                .setScale(5, RoundingMode.HALF_UP)
                .doubleValue();
    }
    // Converts a flat list (key1, value1, key2, value2, ...) into a map assignment.
    public static Map<String, String> listToAssignmentMap(List<String> list) {
        Map<String, String> assignment = new HashMap<>();
        // Check that the list contains an even number of elements
        if (list.size() % 2 != 0) {
            System.out.println("List size must be even (key-value pairs).");
            return assignment;// return an empty map if list size is invalid
        }
        // Fill the map with key-value pairs
        for (int i = 0; i < list.size(); i += 2) {
            String key = list.get(i);
            String value = list.get(i + 1);
            assignment.put(key, value);
        }

        return assignment;
    }


}
