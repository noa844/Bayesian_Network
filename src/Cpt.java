import java.util.*;

public class Cpt {

    private final Map<Map<String,String>, Double> _CPT = new LinkedHashMap<>();

    public void addRow(Map<String,String> keys, double prob) {
        _CPT.put((new LinkedHashMap<>(keys)), prob);  //put as key a new map to keep this key immutable.
    }

    public Map<Map<String, String>, Double> getTable() {

        return _CPT;
    }


    public double getProb(List<String> list) {
        Map<String, String> assignment = BNetwork.listToAssignmentMap(list);
        Double prob = _CPT.get(assignment);
        if(prob==null){
            throw new NullPointerException("prob value doesn't exist for this key");
        }
        return prob;
    }

    public int getSize() {

        return _CPT.size();
    }

    public boolean isEmpty(){
      return   _CPT.isEmpty();
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


    // Function to load the CPT table for a given variable.
    // Returns true if successful (all probabilities used), false otherwise.
    public boolean loadCpt(Variable v){
        List<Variable> parents = v.getParents();
        List<Double> probes = v.getProb();
        Iterator<Double> iterator = probes.iterator();
        Map<String,String> key = new LinkedHashMap<>();

        fillTable(0,parents,iterator,key,v);

        return !iterator.hasNext();
    }


    // Recursive function to generate all CPT combinations corresponding to each probability.
    // The recursion iterates over the outcomes of the variable's parents.
    // For each parent (starting from the first in the list), we loop over all its outcomes,
    // and for each such outcome we continue the recursion deeper.
    // Once all parent combinations are handled (i.e., base case reached),
    // we then iterate over the outcomes of the variable itself (which changes the fastest),
    // and insert a row into the CPT for each outcome.
    private void fillTable(int p, List<Variable> parents, Iterator<Double> iterator, Map<String,String> temp, Variable v ) {

        if (p == parents.size()) {
            // Base case: all parent combinations handled.
            // Now handle all outcomes for the variable itself.
            for (String out : v.getOutcomes()) {
                temp.put(v.getName(), out);
                addRow(temp, iterator.next());        // Add a row to the CPT table.
            }
            temp.remove(v.getName()); // Clean up after iteration to avoid incorrect states.
            return;
        }

        // Recursive step: go through the current parent's outcomes
        // and for each, continue deeper in the recursion.
        Variable parent = parents.get(p);

        for (String out : parent.getOutcomes()) {
            temp.put(parent.getName(), out);
            fillTable(p + 1, parents, iterator, temp, v);
        }
        temp.remove(parent.getName());  // Clean up after backtracking
    }


}
