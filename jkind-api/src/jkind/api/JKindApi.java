package jkind.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jkind.JKindException;
import jkind.SolverOption;
import jkind.api.results.JKindResult;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The primary interface to JKind.
 */
public class JKindApi extends KindApi {
	protected Integer n = null;

	protected boolean boundedModelChecking = true;
	protected boolean kInduction = true;
	protected boolean invariantGeneration = true;
	protected Integer pdrMax = null;
	protected boolean inductiveCounterexamples = false;
	protected boolean reduceInvariants = false;
	protected boolean smoothCounterexamples = false;
	protected boolean intervalGeneralization = false;

	protected SolverOption solver = null;

	/**
	 * Set the maximum depth for BMC and k-induction
	 * 
	 * @param n
	 *            A non-negative integer
	 */
	public void setN(int n) {
		if (n < 0) {
			throw new JKindException("n must be positive");
		}
		this.n = n;
	}

	public void disableBoundedModelChecking() {
		boundedModelChecking = false;
	}

	public void disableKInduction() {
		kInduction = false;
	}

	public void disableInvariantGeneration() {
		invariantGeneration = false;
	}

	/**
	 * Set the maximum number of PDR instances to run
	 * 
	 * @param pdrMax
	 *            A non-negative integer
	 */
	public void setPdrMax(int pdrMax) {
		if (pdrMax < 0) {
			throw new JKindException("pdrMax must be positive");
		}
		this.pdrMax = pdrMax;
	}

	/**
	 * Produce inductive counterexamples for 'unknown' properties
	 */
	public void setInductiveCounterexamples() {
		inductiveCounterexamples = true;
	}

	/**
	 * Set the solver to use (Yices, Z3, CVC4, ...)
	 */
	public void setSolver(SolverOption solver) {
		this.solver = solver;
	}

	/**
	 * Reduce and report the invariants used for a valid property
	 */
	public void setReduceInvariants() {
		reduceInvariants = true;
	}

	/**
	 * Post-process counterexamples to have minimal input value changes
	 */
	public void setSmoothCounterexamples() {
		smoothCounterexamples = true;
	}

	/**
	 * Post-process counterexamples using interval analysis to make them more
	 * general
	 */
	public void setIntervalGeneralization() {
		intervalGeneralization = true;
	}

	/**
	 * Run JKind on a Lustre program
	 * 
	 * @param lustreFile
	 *            File containing Lustre program
	 * @param result
	 *            Place to store results as they come in
	 * @param monitor
	 *            Used to check for cancellation
	 * @throws jkind.JKindException
	 */
	@Override
	public void execute(File lustreFile, JKindResult result, IProgressMonitor monitor) {
		ApiUtil.execute(this::getJKindProcessBuilder, lustreFile, result, monitor);
	}

	private ProcessBuilder getJKindProcessBuilder(File lustreFile) {
		List<String> args = new ArrayList<>();
		args.addAll(Arrays.asList(getJKindCommand()));
		args.add("-xml");
		if (timeout != null) {
			args.add("-timeout");
			args.add(timeout.toString());
		}
		if (n != null) {
			args.add("-n");
			args.add(n.toString());
		}
		if (!boundedModelChecking) {
			args.add("-no_bmc");
		}
		if (!kInduction) {
			args.add("-no_k_induction");
		}
		if (!invariantGeneration) {
			args.add("-no_inv_gen");
		}
		if (pdrMax != null) {
			args.add("-pdr_max");
			args.add(pdrMax.toString());
		}
		if (inductiveCounterexamples) {
			args.add("-induct_cex");
		}
		if (reduceInvariants) {
			args.add("-reduce_inv");
		}
		if (smoothCounterexamples) {
			args.add("-smooth");
		}
		if (intervalGeneralization) {
			args.add("-interval");
		}
		if (solver != null) {
			args.add("-solver");
			args.add(solver.toString());
		}

		args.add(lustreFile.toString());

		ProcessBuilder builder = new ProcessBuilder(args);
		builder.redirectErrorStream(true);
		return builder;
	}

	private static String[] getJKindCommand() {
		return new String[] { "java", "-jar", ApiUtil.findJKindJar().toString(), "-jkind" };
	}

	@Override
	public void checkAvailable() throws Exception {
		List<String> args = new ArrayList<>();
		args.addAll(Arrays.asList(getJKindCommand()));
		args.add("-version");

		ProcessBuilder builder = new ProcessBuilder(args);
		builder.redirectErrorStream(true);
		Process process = builder.start();

		String output = ApiUtil.readAll(process.getInputStream());
		if (process.exitValue() != 0) {
			throw new JKindException("Error running JKind: " + output);
		}
	}
}
