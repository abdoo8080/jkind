package jkind.lustre;

import jkind.Assert;
import jkind.lustre.visitors.AstVisitor;
import jkind.util.Util;

import java.util.List;

public class Mode extends ContractItem
{
    public final String id;
    public final List<Expr> require;
    public final List<Expr> ensure;

    public Mode(Location location, String id, List<Expr> require, List<Expr> ensure)
    {
        super(location);
        Assert.isNotNull(id);
        this.id = id;
        this.require = Util.safeList(require);
        this.ensure = Util.safeList(ensure);
    }

    public Mode(String id, List<Expr> require, List<Expr> ensure)
    {
        this(Location.NULL, id, require, ensure);
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor)
    {
        return visitor.visit(this);
    }
}
