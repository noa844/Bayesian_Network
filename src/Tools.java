import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Tools {

    public static List<Map<String, String>> generateCombinations(Collection<Variable> variables) {
        List<Map<String, String>> result = new ArrayList<>();
        result.add(new LinkedHashMap<>());

        for (Variable var : variables) {
            List<String> values = var.getOutcomes();
            List<Map<String, String>> newResult = new ArrayList<>();

            for (Map<String, String> partial : result) {
                for (String val : values) {
                    Map<String, String> newCombination = new LinkedHashMap<>(partial);
                    newCombination.put(var.getName(), val);
                    newResult.add(newCombination);
                }
            }

            result = newResult;
        }

        return result;
    }
    public static double roundTo5Decimals(double result) {
        return new BigDecimal(result)
                .setScale(5, RoundingMode.HALF_UP)
                .doubleValue();
    }


}
