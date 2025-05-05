import java.util.*;

public class Factor implements Comparable {
    private List<Variable> _variables;
    private Map<Map<String, String>, Double> _factor;

    public Factor(){
    }
    public Factor(Collection<Variable> variables) {
        this._variables = new ArrayList<>(variables);
        this._factor = new LinkedHashMap<>();
    }

    public Factor(Collection<Variable> variables, Map<Map<String, String>, Double> table) {
        this._variables = new ArrayList<>(variables);
        this._factor = new LinkedHashMap<>(table);
    }


    public void setFactor(Map<Map<String, String>, Double> table) {
        _factor.clear();
        _factor.putAll(table);
    }

    public void addRow(Map<String, String> assignment, double prob) {
        _factor.put((new LinkedHashMap<>(assignment)), prob);
    }


    public List<Variable> getVariables() {
        return _variables;
    }

    public Map<Map<String, String>, Double> getfactor() {
        return _factor;
    }

    public double getProb(Map<String, String> assignment) {
        Double prob = _factor.get(assignment);
        if (prob == null) {
            throw new NullPointerException("prob value doesn't exist for this key");
        }
        return prob;
    }


    public int getSize() {

        return _factor.size();
    }

    @Override
    public int compareTo(Object o) {
        Factor f2 = (Factor) o;
        int f1Size = this.getSize();
        int f2Size = f2.getSize();

        if (f1Size > f2Size) {
            return 1;
        } else if (f1Size < f2Size) {
            return -1;

        } else {
            int f1Ascii = this.getVarsAscii(_variables);
            int f2Ascii = f2.getVarsAscii(f2.getVariables());
            return Integer.compare(f1Ascii, f2Ascii);
        }
    }

    private int getVarsAscii(List<Variable> vars) {
        int sum = 0;
        for (Variable var : vars) {
            for (char c : var.getName().toCharArray()) {
                sum += c;
            }
        }
        return sum;
    }

    public Factor eliminateVar(String var) {
        List<Variable> newVars = new ArrayList<>();//filter variables
        int i = 0;
        while (i < _variables.size()) {
            Variable v = _variables.get(i);
            if (!v.getName().equals(var)) {
                newVars.add(v);
            }
            i++;
        }
        List<Map<String, String>> newKeys = Tools.generateCombinations(newVars);
        Factor newFactor = new Factor(newVars);

        for (Map<String, String> newFactorKey : newKeys) {//pour chaque combinaison de la nouvelle table
            double sum = 0.0;
            for (Map<String, String> oldFactorKey : _factor.keySet()) { //parcour chaque ligne de l'ancienne table avec les cles correspondente au cles de la nouvelle table
                if (oldFactorKey.entrySet().containsAll(newFactorKey.entrySet())) {
                    sum += _factor.get(oldFactorKey);
                }
            }
            newFactor.addRow(newFactorKey, sum);
        }

        return newFactor;

    }

    public void normalize() {
        double alpha = 0.0;
        // Calculer la somme totale des probabilités
        for (double prob : _factor.values()) {
            alpha += prob;
        }
        // Éviter la division par zéro
        if (alpha != 0.0) {
            // Normaliser chaque probabilité
            for (Map<String, String> key : _factor.keySet()) {
                _factor.put(key, _factor.get(key) / alpha);

            }
        }
    }


    //pas oublier d'utiliser juste apres setTable
    public Map<Map<String, String>, Double> reduceEvidence(Variable evidence, String out) {

        Map<Map<String, String>, Double> newTable = new LinkedHashMap<>();//nouvelle table

        Iterator<Map<String, String>> iterator = _factor.keySet().iterator();//iteration sur la table actuel

        while (iterator.hasNext()) {
            Map<String, String> key = iterator.next();
            String varOut = key.get(evidence.getName());//get le outcome de la ligne actuel de la table actuel

            if (varOut.equals(out)) {//si dan la ligne se trouve evidence avec le outcome rechercher on cree une nouvelle cle avec les meme valeur sans l'evidence
                Map<String, String> newKey = new LinkedHashMap<>();
                for (String var : key.keySet()) {
                    if (!var.equals(evidence.getName())) {
                        newKey.put(var, key.get(var));
                    }
                }
                newTable.put(newKey, _factor.get(key));

            }
        }

        _variables.remove(evidence);
        return newTable;


    }

    public Factor join(Factor f2) {
        //יוצרים את הרשימה של variables שיכיל הפקטור
        Set<Variable> factorVars = new LinkedHashSet<>();
        factorVars.addAll(this.getVariables());
        factorVars.addAll(f2.getVariables());

        //הפקטור המאוחד
        Factor FactorResult = new Factor(factorVars);

        List<Map<String, String>> combinations = Tools.generateCombinations(factorVars);


        for (Map<String, String> assignment : combinations) {
            Map<String, String> keyF1 = filterKey(assignment, this.getVariables());
            double p1 = this.getProb(keyF1);
            Map<String, String> keyF2 = filterKey(assignment, f2.getVariables());
            double p2 = f2.getProb(keyF2);
            FactorResult.addRow(assignment, p1 * p2);

        }

        return FactorResult;

    }

    //מפלטר
    public static Map<String, String> filterKey(Map<String, String> combination, List<Variable> variables) {

        Map<String, String> result = new LinkedHashMap<>();
        for (Variable var : variables) {
            result.put(var.getName(), combination.get(var.getName()));
        }

        return result;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Factor:\n");
        for (Map.Entry<Map<String, String>, Double> entry : _factor.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" => ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
