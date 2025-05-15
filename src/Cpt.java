//noa.honigstein@gmail.com
import java.util.*;

public class Cpt {
    // Conditional Probability Table, each entry is a mapping from assignment (variable -> value) to its probability
    private final Map<Map<String,String>, Double> _CPT = new LinkedHashMap<>();
    // Add a row (assignment -> probability) to the CPT
    public void addRow(Map<String,String> keys, double prob) {
        _CPT.put((new LinkedHashMap<>(keys)), prob);
    }
    // Return the entire CPT table
    public Map<Map<String, String>, Double> getTable() {

        return _CPT;
    }
    // Return the probability associated with a given assignment (map version)
    public double getProbFromMap(Map<String,String> assignment){
        Double prob = _CPT.get(assignment);
        if(prob==null){
            throw new NullPointerException("prob value doesn't exist for this key");
        }
        return prob;
    }

    // Return the probability from a list representing alternating variable-value pairs
    // If the list is incorrect (not found), return 1.0 and print a warning
    public double getProbFromList(List<String> list) { //if list incorrect , return 1;
        Map<String, String> assignment = Tools.listToAssignmentMap(list);
        Double prob = _CPT.get(assignment);
        if(prob==null){
            System.out.println("prob value doesn't exist for this key");
            return 1.0;
        }else {
            return prob;
        }
    }
    // Return the number of rows in the CPT
    public int getSize() {

        return _CPT.size();
    }

    public boolean isEmpty(){
      return   _CPT.isEmpty();
    }


    // Load the CPT for a given variable v
    // This method constructs all combinations of outcomes for v and its parents,
    // and fills the CPT using the probabilities associated with these combinations.
    public void loadCpt(Variable v){
        List<Variable> cptVars = new ArrayList<>(); //list of variables for generating combinations.
        List<Variable> vParents = v.getParents();
        cptVars.addAll(vParents);
        cptVars.add(v); // Variable itself added at the end
        List<Map<String,String>> keysList = Tools.generateCombinations(cptVars);//tool to generate combinations
        List<Double> probes = v.getProb();
        Iterator<Double> iterator = probes.iterator();
        for(Map<String,String> key : keysList){
            addRow(key,iterator.next());// Insert each combination with its corresponding probability
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CPT:\n");
        for (Map.Entry<Map<String, String>, Double> entry : _CPT.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" => ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }



}
