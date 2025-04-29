import java.util.*;

public class BNetwork {
    private int multiplicationCount = 0;
    private int additionCount = 0;
    private final Map<String, Variable> _variables = new LinkedHashMap<>();
    private final List<Factor> _factors = new ArrayList<>();


    public void addVariable(Variable v) {
        _variables.put(v.getName(), v);
    }

    public Variable getVariable(String name) {
        return _variables.get(name);
    }

    public int getMultiplicationCount() {
        return multiplicationCount;
    }

    public int getAdditionCount() {
        return additionCount;
    }

    public void resetCounters() {
        multiplicationCount = 0;
        additionCount = 0;
    }

    public static Map<String, String> listToAssignmentMap(List<String> list) {
        Map<String, String> assignment = new HashMap<>();
        if (list.size() % 2 != 0) {
            throw new IllegalArgumentException("List size must be even (key-value pairs).");
        }

        for (int i = 0; i < list.size(); i += 2) {
            String key = list.get(i);
            String value = list.get(i + 1);
            assignment.put(key, value);
        }

        return assignment;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bayesian Network:\n");

        for (Map.Entry<String, Variable> entry : _variables.entrySet()) {
            sb.append("- ").append(entry.getValue().toString()).append("\n");
            sb.append(entry.getValue().getCpt().toString()).append("\n");
        }

        return sb.toString();
    }

    public Double calFullJointProb(Map<String, String> elements) {
        List<Double> probes = new ArrayList<>();
        for (String key : elements.keySet()) {
            Variable v = getVariable(key);
            List<String> assignment = new ArrayList<>();
            if (v.hasParents()) {
                List<Variable> parents = v.getParents();
                for (Variable p : parents) {
                    assignment.add(p.getName());
                    String out = elements.get(p.getName());
                    assignment.add(out);
                }
            }
            assignment.add(key);
            String out = elements.get(key);
            assignment.add(out);
            Double prob = v.getCpt().getProb(assignment);
            probes.add(prob);

        }
        double result = probes.get(0);
        for (int i = 1; i < probes.size(); i++) {
            result *= probes.get(i);
            multiplicationCount++;
        }

        return result;


    }

    public Double naiveAlgo(String targetVar, String targetValue, Map<String, String> evidence) {
        return 0.0;

    }

    //helpers pour algo VE
    public Factor createFactor(Variable var) {
        Cpt varCpt = var.getCpt();
        Map<Map<String, String>, Double> table = varCpt.getTable();
        List<Variable> varList = new ArrayList<>();
        varList.add(var);
        if (var.hasParents()) {
            varList.addAll(var.getParents());
        }
        Factor varFactor = new Factor(varList, table);

        return varFactor;

    }

    public void findAncestors(Variable var, List<Variable> ancestors) {
        for (Variable parent : var.getParents()) {
            if (!ancestors.contains(parent)) {
                ancestors.add(parent);
                findAncestors(parent, ancestors);
            }
        }
    }

    public List<Factor> variableElimination(Variable queryVar, String queryVal, Map<Variable, String> evidence, String eliminationStrategy) {
        List<Variable> queryVars = new ArrayList<>();
        queryVars.add(queryVar);
        queryVars.addAll(evidence.keySet());
        List<Variable> factorTocreate = new ArrayList<>();
        //הכנה של רשימת המשתנים שצריכים ליצור עבורם פקטור
        for (Variable var : queryVars) {
            if (!factorTocreate.contains(var)) {
                factorTocreate.add(var);
            }
            findAncestors(var, factorTocreate);
        }

        //יצירת הפקטורים
        List<Factor> factors = new ArrayList<>();
        for (Variable var : factorTocreate) {
            Factor fac = createFactor(var);
            //אלימינציה של שורות ועמודות לא רלוונטיות של פקטורים המכיליםoutcome שונה של המשתני evidence
            for (Variable evi : evidence.keySet()) {
                if (fac.getVariables().contains(evi)) {
                    Map<Map<String, String>, Double> reduced = fac.reduceEvidence(evi, evidence.get(evi));
                    fac.setFactor(reduced);
                }
            }
            factors.add(fac);
        }
        return factors;


    }


}
