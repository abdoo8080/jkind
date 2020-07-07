package jkind.lustre;

import jkind.Assert;
import jkind.lustre.visitors.AstVisitor;
import jkind.util.Util;

import java.util.List;

public class ImportedNode extends Ast
{
    public final String id;
    public final List<VarDecl> inputs;
    public final List<VarDecl> outputs;
    public final ContractBody contractBody; // Nullable

    public ImportedNode(Location location, String id, List<VarDecl> inputs, List<VarDecl> outputs, ContractBody contractBody)
    {
        super(location);
        Assert.isNotNull(id);
        this.id = id;
        this.inputs = Util.safeList(inputs);
        this.outputs = Util.safeList(outputs);
        this.contractBody = contractBody;
    }

    public ImportedNode(String id, List<VarDecl> inputs, List<VarDecl> outputs, ContractBody contractBody)
    {
        this(Location.NULL, id, inputs, outputs, contractBody);
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor)
    {
        return visitor.visit(this);
    }
}
