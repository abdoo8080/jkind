package jkind.lustre;

import jkind.Assert;
import jkind.lustre.visitors.AstVisitor;
import jkind.util.Util;

import java.util.List;

public class ContractImport extends ContractItem
{
    public final String id;
    public final List<Expr> inputs;
    public final List<Expr> outputs;

    public ContractImport(Location loc, String id, List<Expr> inputs, List<Expr> outputs)
    {
        super(loc);
        Assert.isNotNull(id);
        this.id = id;
        this.inputs = Util.safeList(inputs);
        this.outputs = Util.safeList(outputs);
    }

    public ContractImport(String id, List<Expr> inputs, List<Expr> outputs)
    {
        this(Location.NULL, id, inputs, outputs);
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor)
    {
        return visitor.visit(this);
    }
}
