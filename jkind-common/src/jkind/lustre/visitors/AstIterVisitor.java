package jkind.lustre.visitors;

import java.util.List;

import jkind.lustre.*;

public class AstIterVisitor extends ExprIterVisitor implements AstVisitor<Void, Void> {
	@Override
	public Void visit(Constant e) {
		e.expr.accept(this);
		return null;
	}

	@Override
	public Void visit(Equation e) {
		e.expr.accept(this);
		return null;
	}

	@Override
	public Void visit(Function e) {
		visitVarDecls(e.inputs);
		visitVarDecls(e.outputs);
		return null;
	}

	@Override
	public Void visit(Node e) {
		visitVarDecls(e.inputs);
		visitVarDecls(e.outputs);
		visitVarDecls(e.locals);
		visitEquations(e.equations);
		visitAssertions(e.assertions);
		return null;
	}

	@Override
	public Void visit(ImportedNode e) {
		visitVarDecls(e.inputs);
		visitVarDecls(e.outputs);
		return null;
	}

	protected void visitVarDecls(List<VarDecl> es) {
		for (VarDecl e : es) {
			visit(e);
		}
	}

	protected void visitEquations(List<Equation> es) {
		for (Equation e : es) {
			visit(e);
		}
	}

	protected void visitAssertions(List<Expr> es) {
		visitExprs(es);
	}

	@Override
	public Void visit(Program e) {
		visitTypeDefs(e.types);
		visitConstants(e.constants);
		// visitContracts(e.contracts);
		visitFunctions(e.functions);
		// visitImportedNodes(e.importedNodes);
		visitNodes(e.nodes);
		return null;
	}

	protected void visitTypeDefs(List<TypeDef> es) {
		for (TypeDef e : es) {
			visit(e);
		}
	}

	protected void visitConstants(List<Constant> es) {
		for (Constant e : es) {
			visit(e);
		}
	}

	protected void visitContracts(List<Contract> es) {
		for (Contract e : es) {
			visit(e);
		}
	}

	protected void visitFunctions(List<Function> es) {
		for (Function e : es) {
			visit(e);
		}
	}

	protected void visitImportedNodes(List<ImportedNode> es) {
		for (ImportedNode e : es) {
			visit(e);
		}
	}

	protected void visitNodes(List<Node> es) {
		for (Node e : es) {
			visit(e);
		}
	}

	protected void visitContractItems(List<ContractItem> es) {
		for (ContractItem e : es) {
			e.accept(this);
		}
	}

	@Override
	public Void visit(TypeDef e) {
		return null;
	}

	@Override
	public Void visit(VarDecl e) {
		return null;
	}

	@Override
	public Void visit(VarDef varDef) {
		varDef.expr.accept(this);
		return null;
	}

	@Override
	public Void visit(Assume assumption) {
		assumption.expr.accept(this);
		return null;
	}

	@Override
	public Void visit(Guarantee guarantee) {
		guarantee.expr.accept(this);
		return null;
	}

	@Override
	public Void visit(ContractImport contractImport) {
		visitExprs(contractImport.inputs);
		visitExprs(contractImport.outputs);
		return null;
	}

	@Override
	public Void visit(ContractBody contractBody) {
		visitContractItems(contractBody.items);
		return null;
	}

	@Override
	public Void visit(Mode mode) {
		visitExprs(mode.require);
		visitExprs(mode.ensure);
		return null;
	}

	@Override
	public Void visit(Contract contract) {
		visitVarDecls(contract.inputs);
		visitVarDecls(contract.outputs);
		visit(contract.contractBody);
		return null;
	}
}
