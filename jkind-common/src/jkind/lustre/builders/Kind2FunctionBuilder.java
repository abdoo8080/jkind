package jkind.lustre.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jkind.lustre.ContractBody;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.Kind2Function;
import jkind.lustre.Location;
import jkind.lustre.Type;
import jkind.lustre.VarDecl;

public class Kind2FunctionBuilder {
	private String id;
	private List<VarDecl> inputs = new ArrayList<>();
	private List<VarDecl> outputs = new ArrayList<>();
	private ContractBody contractBody = null;
	private List<VarDecl> locals = new ArrayList<>();
	private List<Equation> equations = new ArrayList<>();
	private List<String> properties = new ArrayList<>();
	private List<Expr> assertions = new ArrayList<>();

	public Kind2FunctionBuilder(String id) {
		this.id = id;
	}

	public Kind2FunctionBuilder(Kind2Function Kind2Function) {
		this.id = Kind2Function.id;
		this.inputs = new ArrayList<>(Kind2Function.inputs);
		this.outputs = new ArrayList<>(Kind2Function.outputs);
		this.contractBody = Kind2Function.contractBody;
		this.locals = new ArrayList<>(Kind2Function.locals);
		this.equations = new ArrayList<>(Kind2Function.equations);
		this.properties = new ArrayList<>(Kind2Function.properties);
		this.assertions = new ArrayList<>(Kind2Function.assertions);
	}

	public Kind2FunctionBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public Kind2FunctionBuilder addInput(VarDecl input) {
		this.inputs.add(input);
		return this;
	}

	public Kind2FunctionBuilder addInputs(Collection<VarDecl> inputs) {
		this.inputs.addAll(inputs);
		return this;
	}

	public IdExpr createInput(String name, Type type) {
		this.inputs.add(new VarDecl(name, type));
		return new IdExpr(name);
	}

	public Kind2FunctionBuilder clearInputs() {
		this.inputs.clear();
		return this;
	}

	public Kind2FunctionBuilder addOutput(VarDecl output) {
		this.outputs.add(output);
		return this;
	}

	public Kind2FunctionBuilder addOutputs(Collection<VarDecl> outputs) {
		this.outputs.addAll(outputs);
		return this;
	}

	public IdExpr createOutput(String name, Type type) {
		this.outputs.add(new VarDecl(name, type));
		return new IdExpr(name);
	}

	public Kind2FunctionBuilder clearOutputs() {
		this.outputs.clear();
		return this;
	}

	public Kind2FunctionBuilder setContractBody(ContractBody contractBody) {
		this.contractBody = contractBody;
		return this;
	}

	public Kind2FunctionBuilder addLocal(VarDecl local) {
		this.locals.add(local);
		return this;
	}

	public Kind2FunctionBuilder addLocals(Collection<VarDecl> locals) {
		this.locals.addAll(locals);
		return this;
	}

	public IdExpr createLocal(String name, Type type) {
		this.locals.add(new VarDecl(name, type));
		return new IdExpr(name);
	}

	public Kind2FunctionBuilder clearLocals() {
		this.locals.clear();
		return this;
	}

	public Kind2FunctionBuilder addEquation(Equation equation) {
		this.equations.add(equation);
		return this;
	}

	public Kind2FunctionBuilder addEquation(IdExpr var, Expr expr) {
		this.equations.add(new Equation(var, expr));
		return this;
	}

	public Kind2FunctionBuilder addEquations(Collection<Equation> equations) {
		this.equations.addAll(equations);
		return this;
	}

	public Kind2FunctionBuilder clearEquations() {
		this.equations.clear();
		return this;
	}

	public Kind2FunctionBuilder addAssertion(Expr assertion) {
		this.assertions.add(assertion);
		return this;
	}

	public Kind2FunctionBuilder addAssertions(Collection<Expr> assertions) {
		this.assertions.addAll(assertions);
		return this;
	}

	public Kind2FunctionBuilder clearAssertions() {
		this.assertions.clear();
		return this;
	}

	public Kind2FunctionBuilder addProperty(String property) {
		this.properties.add(property);
		return this;
	}

	public Kind2FunctionBuilder addProperty(IdExpr property) {
		this.properties.add(property.id);
		return this;
	}

	public Kind2FunctionBuilder addProperties(Collection<String> properties) {
		this.properties.addAll(properties);
		return this;
	}

	public Kind2FunctionBuilder clearProperties() {
		this.properties.clear();
		return this;
	}

	public Kind2Function build() {
		return new Kind2Function(Location.NULL, id, inputs, outputs, contractBody, locals,
				equations, assertions, properties);
	}
}
