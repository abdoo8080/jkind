package jkind.pdr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.Util;

public abstract class ScriptUser {
	protected final Script script;

	public ScriptUser(Script script) {
		this.script = script;
	}

	protected Term term(String funcname, Term... params) {
		return script.term(funcname, params);
	}

	protected Term not(Term term) {
		return Util.not(script, term);
	}

	protected Term and(List<Term> conjuncts) {
		return Util.and(script, conjuncts.toArray(new Term[conjuncts.size()]));
	}

	protected Term and(Term... conjuncts) {
		return Util.and(script, conjuncts);
	}

	protected Term or(List<Term> disjuncts) {
		return Util.or(script, disjuncts.toArray(new Term[disjuncts.size()]));
	}

	protected Term or(Term... disjuncts) {
		return Util.or(script, disjuncts);
	}

	protected Term ite(Term cond, Term thenPart, Term elsePart) {
		return Util.ite(script, cond, thenPart, elsePart);
	}

	protected Term implies(Term... terms) {
		return Util.implies(script, terms);
	}

	protected Term numeral(BigInteger value) {
		return script.numeral(value);
	}

	protected Term decimal(BigDecimal value) {
		return script.decimal(value);
	}
}