//noa.honigstein@gmail.com

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Variable {
    private final String _name;
    private final List<String> _outComes;
    private List<Variable> _parents;
    private List<Double> _probabilities;
    private Cpt _cpt;


    public Variable(String name, List<String> outComes){
        _name = name;
        _outComes = outComes;
        _parents = new ArrayList<>();
        _probabilities = new ArrayList<>();
        _cpt = new Cpt();
    }

    public void setParents(List<Variable> parents){
        _parents = parents;
    }
    public void setProbabilities(List<Double> probes){
        _probabilities = probes;
    }

    public String getName(){
        return _name;
    }
    public Cpt getCpt() {
        return _cpt;
    }
    public List<String> getOutcomes(){
        return _outComes;
    }
    public List<Variable> getParents(){
        return _parents;
    }
    public List<Double> getProb(){
        return _probabilities;
    }
    public int getOutcomesSize(){
        return _outComes.size();
    }
    public int getParentsSize(){
        return _parents.size();
    }
    public boolean hasParents(){
        return !_parents.isEmpty();
    }

    public void setCpt(){
        if(_cpt.isEmpty()){
        _cpt.loadCpt(this);
        }
        else System.out.println("cpt already loaded");
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Variable{name='").append(_name).append("', outcomes=").append(_outComes);

        if (!_parents.isEmpty()) {
            sb.append(", parents=[");
            for (Variable p : _parents) {
                sb.append(p.getName()).append(", ");
            }
            sb.setLength(sb.length() - 2); // remove last comma
            sb.append("]");
        } else {
            sb.append(", parents=[]");
        }

        sb.append("}");

        return sb.toString();
    }
    // Equality check based on variable name only
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Variable other = (Variable) obj;
        return _name.equals(other._name);
    }

    @Override
    public int hashCode() {

        return _name.hashCode();
    }




}
