package jkind.lustre;

import jkind.Assert;
import jkind.lustre.visitors.AstVisitor;
import jkind.util.Util;

import java.util.List;

public class ContractBody extends Ast
{
    public final List<ContractItem> items;

    public ContractBody(Location location, List<ContractItem> items)
    {
        super(location);
        Assert.isFalse(items.isEmpty());
        this.items = Util.safeList(items);
    }

    public ContractBody(List<ContractItem> items)
    {
        this(Location.NULL, items);
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor)
    {
        return visitor.visit(this);
    }
}
