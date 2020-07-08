import jkind.lustre.*;
import jkind.lustre.builders.ContractBodyBuilder;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.builders.ProgramBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		ProgramBuilder program = new ProgramBuilder();

		program.addContract(stopWatchSpec());
		program.addImportedNode(sqrt());
		program.addNode(even().build());
		program.addNode(toInt().build());
		program.addNode(count().build());
		program.addNode(sofar().build());
		program.addNode(since().build());
		program.addNode(sinceIncl().build());
		program.addNode(increased().build());
		program.addNode(stable().build());
		program.addNode(stopWatch().build());

		System.out.println(program.build().toString());
	}

	public static Contract stopWatchSpec() {
		ContractBodyBuilder c = new ContractBodyBuilder();

		IdExpr toggle = LustreUtil.id("toggle");
		IdExpr reset = LustreUtil.id("reset");
		IdExpr time = LustreUtil.id("time");
		IdExpr on = LustreUtil.id("on");

		c.createVarDef("on", NamedType.BOOL,
				LustreUtil.arrow(toggle,
						LustreUtil.or(LustreUtil.and(LustreUtil.pre(on), LustreUtil.not(toggle)),
								LustreUtil.and(LustreUtil.not(LustreUtil.pre(on)), toggle))));

		c.addAssumption(LustreUtil.not(LustreUtil.and(toggle, reset)));
		c.addGuarantee(LustreUtil.arrow(
				LustreUtil.implies(on, LustreUtil.equal(time, LustreUtil.integer(1))),
				LustreUtil.TRUE));
		c.addGuarantee(LustreUtil.arrow(LustreUtil.implies(LustreUtil.not(on),
				LustreUtil.equal(time, LustreUtil.integer(0))), LustreUtil.TRUE));
		c.addGuarantee(LustreUtil.greaterEqual(time, LustreUtil.integer(0)));

		c.addGuarantee(
				LustreUtil.implies(
						LustreUtil.and(LustreUtil.not(reset),
								new NodeCallExpr("Since",
										Arrays.asList(reset, new NodeCallExpr("Even",
												Arrays.asList(new NodeCallExpr("Count",
														Collections.singletonList(toggle))))))),
						new NodeCallExpr("Stable", Collections.singletonList(time))));


		c.addGuarantee(
				LustreUtil.implies(
						LustreUtil.and(LustreUtil.not(reset),
								new NodeCallExpr("Since",
										Arrays.asList(reset, LustreUtil.not(new NodeCallExpr("Even",
												Arrays.asList(new NodeCallExpr("Count",
														Collections.singletonList(toggle)))))))),
						new NodeCallExpr("Increased", Collections.singletonList(time))));


		c.addGuarantee(
				LustreUtil
						.arrow(LustreUtil.TRUE,
								LustreUtil.implies(
										LustreUtil.and(
												LustreUtil.not(new NodeCallExpr("Even",
														Arrays.asList(new NodeCallExpr("Count",
																Collections
																		.singletonList(toggle))))),
												LustreUtil.equal(
														new NodeCallExpr("Count",
																Collections.singletonList(reset)),
														LustreUtil.integer(0))),
										LustreUtil.greater(time, LustreUtil.pre(time)))));

		c.addMode("resetting", Collections.singletonList(reset),
				Collections.singletonList(LustreUtil.equal(time, LustreUtil.integer(0))));

		c.addMode("running", Arrays.asList(on, LustreUtil.not(reset)),
				Collections.singletonList(LustreUtil.arrow(LustreUtil.TRUE, LustreUtil.equal(time,
						LustreUtil.plus(LustreUtil.pre(time), LustreUtil.integer(1))))));

		c.addMode("stopped", Arrays.asList(LustreUtil.not(reset), LustreUtil.not(on)),
				Collections.singletonList(LustreUtil.arrow(LustreUtil.TRUE,
						LustreUtil.equal(time, LustreUtil.pre(time)))));

		List<VarDecl> inputs = Arrays.asList(new VarDecl("toggle", NamedType.BOOL),
				new VarDecl("reset", NamedType.BOOL));
		List<VarDecl> outputs = Collections.singletonList(new VarDecl("time", NamedType.INT));

		return new Contract("StopWatchSpec", inputs, outputs, c.build());
	}

	public static ImportedNode sqrt() {
		IdExpr n = new IdExpr("n");
		IdExpr r = new IdExpr("r");

		ContractBodyBuilder c = new ContractBodyBuilder();
		c.addAssumption(LustreUtil.greaterEqual(n, LustreUtil.real("0.0")));
		c.addGuarantee(LustreUtil.and(LustreUtil.greaterEqual(r, LustreUtil.real("0.0")),
				LustreUtil.equal(LustreUtil.multiply(r, r), n)));

		return new ImportedNode("sqrt", Collections.singletonList(new VarDecl("n", NamedType.REAL)),
				Collections.singletonList(new VarDecl("r", NamedType.REAL)), c.build());
	}

	public static NodeBuilder even() {
		NodeBuilder n = new NodeBuilder("Even");
		IdExpr N = n.createInput("N", NamedType.INT);
		IdExpr B = n.createOutput("B", NamedType.BOOL);
		n.addEquation(B,
				LustreUtil.equal(LustreUtil.mod(N, LustreUtil.integer(2)), LustreUtil.integer(0)));
		return n;
	}

	public static NodeBuilder toInt() {
		NodeBuilder n = new NodeBuilder("ToInt");
		IdExpr X = n.createInput("X", NamedType.BOOL);
		IdExpr N = n.createOutput("N", NamedType.INT);
		n.addEquation(N, LustreUtil.ite(X, LustreUtil.integer(1), LustreUtil.integer(0)));
		return n;
	}

	public static NodeBuilder count() {
		NodeBuilder n = new NodeBuilder("Count");
		IdExpr X = n.createInput("X", NamedType.BOOL);
		IdExpr N = n.createOutput("N", NamedType.INT);
		NodeCallExpr toIntX = new NodeCallExpr("ToInt", X);
		n.addEquation(N, LustreUtil.arrow(toIntX, LustreUtil.plus(toIntX, LustreUtil.pre(N))));
		return n;
	}

	public static NodeBuilder sofar() {
		NodeBuilder n = new NodeBuilder("Sofar");
		IdExpr X = n.createInput("X", NamedType.BOOL);
		IdExpr Y = n.createOutput("Y", NamedType.BOOL);
		n.addEquation(Y, LustreUtil.arrow(X, LustreUtil.and(X, LustreUtil.pre(Y))));
		return n;
	}

	public static NodeBuilder since() {
		NodeBuilder n = new NodeBuilder("Since");
		IdExpr X = n.createInput("X", NamedType.BOOL);
		IdExpr Y = n.createInput("Y", NamedType.BOOL);
		IdExpr Z = n.createOutput("Z", NamedType.BOOL);
		n.addEquation(Z, LustreUtil.or(X,
				LustreUtil.and(Y, LustreUtil.arrow(LustreUtil.FALSE, LustreUtil.pre(Z)))));
		return n;
	}

	public static NodeBuilder sinceIncl() {
		NodeBuilder n = new NodeBuilder("SinceIncl");
		IdExpr X = n.createInput("X", NamedType.BOOL);
		IdExpr Y = n.createInput("Y", NamedType.BOOL);
		IdExpr Z = n.createOutput("Z", NamedType.BOOL);
		n.addEquation(Z, LustreUtil.and(Y,
				LustreUtil.or(X, LustreUtil.arrow(LustreUtil.FALSE, LustreUtil.pre(Z)))));
		return n;
	}

	public static NodeBuilder increased() {
		NodeBuilder n = new NodeBuilder("Increased");
		IdExpr N = n.createInput("N", NamedType.INT);
		IdExpr B = n.createOutput("B", NamedType.BOOL);
		n.addEquation(B,
				LustreUtil.arrow(LustreUtil.TRUE, LustreUtil.greater(N, LustreUtil.pre(N))));
		return n;
	}

	public static NodeBuilder stable() {
		NodeBuilder n = new NodeBuilder("Stable");
		IdExpr N = n.createInput("N", NamedType.INT);
		IdExpr B = n.createOutput("B", NamedType.BOOL);
		n.addEquation(B, LustreUtil.arrow(LustreUtil.TRUE, LustreUtil.equal(N, LustreUtil.pre(N))));
		return n;
	}

	public static NodeBuilder stopWatch() {
		NodeBuilder n = new NodeBuilder("Stopwatch");
		IdExpr toggle = n.createInput("toggle", NamedType.BOOL);
		IdExpr reset = n.createInput("reset", NamedType.BOOL);
		IdExpr count = n.createOutput("count", NamedType.INT);

		ContractBodyBuilder c = new ContractBodyBuilder();
		c.importContract("StopWatchSpec", Arrays.asList(toggle, reset),
				Collections.singletonList(count));

		c.addGuarantee(
				LustreUtil.not(LustreUtil.and(LustreUtil.modeRef("StopWatchSpec", "resetting"),
						LustreUtil.modeRef("StopWatchSpec", "running"),
						LustreUtil.modeRef("StopWatchSpec", "stopped"))));

		n.setContractBody(c.build());

		IdExpr running = n.createLocal("running", NamedType.BOOL);

		n.addEquation(running, LustreUtil
				.notEqual(LustreUtil.arrow(LustreUtil.FALSE, LustreUtil.pre(running)), toggle));
		n.addEquation(count,
				LustreUtil.ite(reset, LustreUtil.integer(0), LustreUtil.ite(running,
						LustreUtil.arrow(LustreUtil.integer(1),
								LustreUtil.plus(LustreUtil.pre(count), LustreUtil.integer(1))),
						LustreUtil.arrow(LustreUtil.integer(0), LustreUtil.pre(count)))));

		return n;
	}
}
