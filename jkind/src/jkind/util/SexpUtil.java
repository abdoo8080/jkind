package jkind.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jkind.lustre.Expr;
import jkind.sexp.Cons;
import jkind.sexp.Sexp;
import jkind.sexp.Symbol;
import jkind.translation.Lustre2Sexp;

public class SexpUtil {
	public static Sexp and(List<? extends Sexp> conjuncts) {
		if (conjuncts.isEmpty()) {
			return new Symbol("true");
		} else if (conjuncts.size() == 1) {
			return conjuncts.get(0);
		} else {
			return new Cons("and", conjuncts);
		}
	}

	public static Sexp or(List<Sexp> disjuncts) {
		if (disjuncts.isEmpty()) {
			return new Symbol("false");
		} else if (disjuncts.size() == 1) {
			return disjuncts.get(0);
		} else {
			return new Cons("or", disjuncts);
		}
	}

	public static Sexp conjoinInvariants(Collection<Expr> invariants, int k) {
		List<Sexp> conjuncts = new ArrayList<>();
		for (Expr invariant : invariants) {
			conjuncts.add(invariant.accept(new Lustre2Sexp(k)));
		}
		return SexpUtil.and(conjuncts);
	}

	public static Sexp not(Sexp sexp) {
		return new Cons("not", sexp);
	}

	public static Sexp and(Sexp sexp1, Sexp sexp2) {
		return new Cons("and", sexp1, sexp2);
	}
}
