package jkind.lustre;

import jkind.Assert;
import jkind.lustre.visitors.AstVisitor;

/**
 * This class represents a contract assumption. An assumption over a node {@code n} is a constraint
 * one must respect in order to use {@code n} legally. It cannot mention the outputs of {@code n} in
 * the current state, but referring to outputs under a {@code pre} is fine.
 */
public class Assume extends ContractItem {
	public final Expr expr;

	/**
	 * Constructor
	 *
	 * @param location location of assumption in a Lustre file
	 * @param expr     an expression representing a constraint
	 */
	public Assume(Location location, Expr expr) {
		super(location);
		Assert.isNotNull(expr);
		this.expr = expr;
	}

	/**
	 * Constructor
	 *
	 * @param expr an expression representing a constraint
	 */
	public Assume(Expr expr) {
		this(Location.NULL, expr);
	}

	@Override
	public <T, S extends T> T accept(AstVisitor<T, S> visitor) {
		return visitor.visit(this);
	}
}
