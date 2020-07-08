package jkind.lustre.visitors;

import jkind.lustre.*;

import java.util.List;

public class AstMapVisitor extends ExprMapVisitor implements AstVisitor<Ast, Expr> {
	@Override
	public Constant visit(Constant e) {
		return new Constant(e.location, e.id, e.type, e.expr.accept(this));
	}

	@Override
	public Equation visit(Equation e) {
		// Do not traverse e.lhs since they do not really act like Exprs
		return new Equation(e.location, e.lhs, e.expr.accept(this));
	}

	@Override
	public Function visit(Function e) {
		List<VarDecl> inputs = visitVarDecls(e.inputs);
		List<VarDecl> outputs = visitVarDecls(e.outputs);
		return new Function(e.location, e.id, inputs, outputs);
	}

	@Override
	public Node visit(Node e) {
		List<VarDecl> inputs = visitVarDecls(e.inputs);
		List<VarDecl> outputs = visitVarDecls(e.outputs);
		List<VarDecl> locals = visitVarDecls(e.locals);
		List<Equation> equations = visitEquations(e.equations);
		List<Expr> assertions = visitAssertions(e.assertions);
		List<String> properties = visitProperties(e.properties);
		List<String> ivc = visitIvc(e.ivc);
		List<String> realizabilityInputs = visitRealizabilityInputs(e.realizabilityInputs);
		ContractBody contractBody = null;
		if (e.contractBody != null) {
			contractBody = visit(e.contractBody);
		}
		return new Node(e.location, e.id, inputs, outputs, locals, equations, properties,
				assertions, realizabilityInputs, contractBody, ivc);
	}

	public ImportedNode visit(ImportedNode e) {
		List<VarDecl> inputs = visitVarDecls(e.inputs);
		List<VarDecl> outputs = visitVarDecls(e.outputs);
		ContractBody contractBody = null;
		if (e.contractBody != null) {
			contractBody = visit(e.contractBody);
		}
		return new ImportedNode(e.location, e.id, inputs, outputs, contractBody);
	}

	protected List<VarDecl> visitVarDecls(List<VarDecl> es) {
		return map(this::visit, es);
	}

	protected List<Equation> visitEquations(List<Equation> es) {
		return map(this::visit, es);
	}

	protected List<Expr> visitAssertions(List<Expr> es) {
		return visitExprs(es);
	}

	protected List<String> visitProperties(List<String> es) {
		return map(this::visitProperty, es);
	}

	protected String visitProperty(String e) {
		return e;
	}

	protected List<String> visitIvc(List<String> es) {
		return map(this::visitIvc, es);
	}

	protected String visitIvc(String e) {
		return e;
	}

	protected List<String> visitRealizabilityInputs(List<String> es) {
		if (es == null) {
			return null;
		}
		return map(this::visitRealizabilityInput, es);
	}

	protected String visitRealizabilityInput(String e) {
		return e;
	}

	@Override
	public Program visit(Program e) {
		List<TypeDef> types = visitTypeDefs(e.types);
		List<Constant> constants = visitConstants(e.constants);
		// List<Contract> contracts = visitContracts(e.contracts);
		List<Function> functions = visitFunctions(e.functions);
		// List<ImportedNode> importedNodes = visitImportedNodes(e.importedNodes);
		List<Node> nodes = visitNodes(e.nodes);
		return new Program(e.location, types, constants, e.contracts, functions, e.importedNodes,
				nodes, e.main);
	}

	protected List<TypeDef> visitTypeDefs(List<TypeDef> es) {
		return map(this::visit, es);
	}

	protected List<Constant> visitConstants(List<Constant> es) {
		return map(this::visit, es);
	}

	protected List<Contract> visitContracts(List<Contract> es) {
		return map(this::visit, es);
	}

	protected List<ImportedNode> visitImportedNodes(List<ImportedNode> es) {
		return map(this::visit, es);
	}

	protected List<Node> visitNodes(List<Node> es) {
		return map(this::visit, es);
	}

	protected List<Function> visitFunctions(List<Function> es) {
		return map(this::visit, es);
	}

	protected List<ContractItem> visitContractItems(List<? extends ContractItem> is) {
		is.get(0).accept(this);
		return map(this::visitContractItem, is);
	}

	protected ContractItem visitContractItem(ContractItem i) {
		if (i instanceof Assume) {
			return visit((Assume) i);
		} else if (i instanceof Constant) {
			return visit((Constant) i);
		} else if (i instanceof ContractImport) {
			return visit((ContractImport) i);
		} else if (i instanceof Guarantee) {
			return visit((Guarantee) i);
		} else if (i instanceof Mode) {
			return visit((Mode) i);
		} else {
			return visit((VarDef) i);
		}
	}

	@Override
	public TypeDef visit(TypeDef e) {
		return e;
	}

	@Override
	public VarDecl visit(VarDecl e) {
		return e;
	}

	@Override
	public VarDef visit(VarDef varDef) {
		return new VarDef(varDef.location, visit(varDef.varDecl), varDef.expr.accept(this));
	}

	@Override
	public Assume visit(Assume assumption) {
		return new Assume(assumption.location, assumption.expr.accept(this));
	}

	@Override
	public Guarantee visit(Guarantee guarantee) {
		return new Guarantee(guarantee.location, guarantee.expr.accept(this));
	}

	@Override
	public ContractImport visit(ContractImport contractImport) {
		return new ContractImport(contractImport.location, contractImport.id,
				visitExprs(contractImport.inputs), visitExprs(contractImport.outputs));
	}

	@Override
	public Mode visit(Mode mode) {
		return new Mode(mode.id, visitExprs(mode.require), visitExprs(mode.ensure));
	}

	@Override
	public ContractBody visit(ContractBody contractBody) {
		return new ContractBody(visitContractItems(contractBody.items));
	}

	@Override
	public Contract visit(Contract contract) {
		List<VarDecl> inputs = visitVarDecls(contract.inputs);
		List<VarDecl> outputs = visitVarDecls(contract.outputs);
		ContractBody contractBody = visit(contract.contractBody);

		return new Contract(contract.id, inputs, outputs, contractBody);
	}
}
