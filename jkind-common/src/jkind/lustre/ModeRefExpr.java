package jkind.lustre;

import jkind.Assert;
import jkind.lustre.visitors.ExprVisitor;

import java.util.Arrays;
import java.util.List;

public class ModeRefExpr extends Expr
{
    public final List<String> path;

    public ModeRefExpr(Location location, String... path)
    {
        super(location);
        Assert.isFalse(path.length == 0);
        for (String id : path)
        {
            Assert.isNotNull(id);
        }
        this.path = Arrays.asList(path);
    }

    public ModeRefExpr(String... path)
    {
        this(Location.NULL, path);
    }

    @Override
    public <T> T accept(ExprVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
