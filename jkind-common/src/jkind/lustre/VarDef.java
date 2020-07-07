
package jkind.lustre;

import jkind.Assert;
import jkind.lustre.visitors.AstVisitor;

public class VarDef extends ContractItem
{
    public final VarDecl varDecl;
    public final Expr expr;

    public VarDef(Location location, VarDecl varDecl, Expr expr)
    {
        super(location);
        this.varDecl = varDecl;
        Assert.isNotNull(expr);
        this.expr = expr;
    }

    public VarDef(Location location, String id, Type type, Expr expr)
    {
        this(location, new VarDecl(id, type), expr);
    }

    public VarDef(String id, Type type, Expr expr)
    {
        this(Location.NULL, id, type, expr);
    }

    @Override
    public <T, S extends T> T accept(AstVisitor<T, S> visitor)
    {
        return visitor.visit(this);
    }
}
