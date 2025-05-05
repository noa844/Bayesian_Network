// A helper class used to associate a variable with its corresponding product size
// (i.e., the size of the factor that would be created if we eliminate this variable).
public class eliminateProduct implements Comparable<eliminateProduct> {

    private Variable _var;
    private int _productSize;

    public eliminateProduct(Variable var, int productSize) {
        this._var = var;
        this._productSize = productSize;
    }

    public Variable getVar() {
        return _var;
    }

    public int getProductSize() {
        return _productSize;
    }
    // Compare two eliminateProduct instances by their product sizes
    // Used for sorting in ascending order of elimination cost
    @Override
    public int compareTo(eliminateProduct other) {
        return Integer.compare(this._productSize, other._productSize);
    }

}
