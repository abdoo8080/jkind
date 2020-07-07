package jkind.lustre.builders;

import jkind.lustre.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ContractBodyBuilder
{
    private final List<ContractItem> items;

    public ContractBodyBuilder()
    {
        this.items = new ArrayList<>();
    }

    public ContractBodyBuilder(ContractBody contractBody)
    {
        this.items = new ArrayList<>(contractBody.items);
    }

    public ContractBodyBuilder addConstant(Constant constant)
    {
        this.items.add(constant);
        return this;
    }

    public ContractBodyBuilder addConstants(Collection<Constant> constants)
    {
        this.items.addAll(constants);
        return this;
    }

    public IdExpr createConstant(String name, Type type, Expr expr)
    {
        this.items.add(new Constant(name, type, expr));
        return new IdExpr(name);
    }

    public ContractBodyBuilder addVarDef(VarDef varDef)
    {
        this.items.add(varDef);
        return this;
    }

    public ContractBodyBuilder addVarDef(Collection<VarDef> varDefs)
    {
        this.items.addAll(varDefs);
        return this;
    }

    public IdExpr createVarDef(String name, Type type, Expr expr)
    {
        this.items.add(new VarDef(name, type, expr));
        return new IdExpr(name);
    }

    public ContractBodyBuilder addAssumption(Expr expr)
    {
        this.items.add(new Assume(expr));
        return this;
    }

    public ContractBodyBuilder addAssumption(Assume assumption)
    {
        this.items.add(assumption);
        return this;
    }

    public ContractBodyBuilder addAssumptions(Collection<Assume> assumptions)
    {
        this.items.addAll(assumptions);
        return this;
    }

    public ContractBodyBuilder addGuarantee(Expr expr)
    {
        this.items.add(new Guarantee(expr));
        return this;
    }

    public ContractBodyBuilder addGuarantee(Guarantee guarantee)
    {
        this.items.add(guarantee);
        return this;
    }

    public ContractBodyBuilder addGuarantees(Collection<Guarantee> guarantees)
    {
        this.items.addAll(guarantees);
        return this;
    }

    public ContractBodyBuilder addMode(String id, List<Expr> require, List<Expr> ensure)
    {
        this.items.add(new Mode(id, require, ensure));
        return this;
    }

    public ContractBodyBuilder addMode(Mode mode)
    {
        this.items.add(mode);
        return this;
    }

    public ContractBodyBuilder addModes(Collection<Mode> modes)
    {
        this.items.addAll(modes);
        return this;
    }

    public ContractBodyBuilder addImport(String name, List<Expr> inputs, List<Expr> outputs)
    {
        this.items.add(new ContractImport(name, inputs, outputs));
        return this;
    }

    public ContractBodyBuilder addImport(ContractImport contract)
    {
        this.items.add(contract);
        return this;
    }

    public ContractBodyBuilder addImports(Collection<ContractImport> contracts)
    {
        this.items.addAll(contracts);
        return this;
    }

    public ContractBody build()
    {
        return new ContractBody(Location.NULL, items);
    }
}
