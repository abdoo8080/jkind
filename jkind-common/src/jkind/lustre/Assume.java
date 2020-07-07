package jkind.lustre;

import jkind.Assert;
import jkind.lustre.visitors.AstVisitor;

public class Assume extends ContractItem
{
    public final Expr expr;

    public Assume(Location location, Expr expr)
    {
        super(location);
        Assert.isNotNull(expr);
        this.expr = expr;
    }

    public Assume(Expr expr)
    {
        this(Location.NULL, expr);
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor) {
        return visitor.visit(this);
    }
}
