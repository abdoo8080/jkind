package jkind.lustre.visitors;

import jkind.JKindException;
import jkind.lustre.*;
import jkind.lustre.builders.ContractBodyBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrettyPrintVisitorTest {
	String removeWhiteSpace(String s) {
		return s.codePoints().filter(c -> !Character.isWhitespace(c))
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	@Test
	void assumeTest() {
		String expected = "assume true;";

		Assertions.assertThrows(JKindException.class, () -> new Assume(null));

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(new Assume(LustreUtil.TRUE));

		assertEquals(visitor.toString(), expected);
	}

	@Test
	void guaranteeTest() {
		String expected = "guarantee true;";

		Assertions.assertThrows(JKindException.class, () -> new Guarantee(null));

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(new Guarantee(LustreUtil.TRUE));

		assertEquals(visitor.toString(), expected);
	}

	@Test
	void modeRefExprTest() {
		String expected = "::a::b::c";

		Assertions.assertThrows(JKindException.class, ModeRefExpr::new);
		Assertions.assertThrows(JKindException.class, () -> new ModeRefExpr((String) null));

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(LustreUtil.modeRef("a", "b", "c"));

		assertEquals(visitor.toString(), expected);
	}

	@Test
	void constantTest() {
		String expected = "const c = true;";

		Assertions.assertThrows(JKindException.class,
				() -> new Constant(null, null, LustreUtil.TRUE));
		Assertions.assertThrows(JKindException.class, () -> new Constant("c", null, null));

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(new Constant("c", null, LustreUtil.TRUE));

		assertEquals(visitor.toString(), expected);
	}

	@Test
	void varDefTest() {
		String expected = "var x : bool = true;";

		Assertions.assertThrows(JKindException.class,
				() -> new VarDef(null, NamedType.BOOL, LustreUtil.TRUE));
		Assertions.assertThrows(JKindException.class, () -> new VarDef("x", null, LustreUtil.TRUE));
		Assertions.assertThrows(JKindException.class, () -> new VarDef("x", NamedType.BOOL, null));

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(new VarDef("x", NamedType.BOOL, LustreUtil.TRUE));

		assertEquals(visitor.toString(), expected);
	}

	@Test
	void contractImportTest() {
		Assertions.assertThrows(JKindException.class, () -> new ContractImport(null, null, null));

		String expected = "import Spec() returns ();";

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(new ContractImport("Spec", null, null));

		assertEquals(visitor.toString(), expected);

		expected = "import Spec(x) returns (y);";

		visitor = new PrettyPrintVisitor();
		visitor.visit(new ContractImport("Spec", Collections.singletonList(LustreUtil.id("x")),
				Collections.singletonList(LustreUtil.id("y"))));

		assertEquals(visitor.toString(), expected);
	}

	@Test
	void modeTest() {
		String expected = "mode m1 ();";

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(new Mode("m1", null, null));

		assertEquals(removeWhiteSpace(visitor.toString()), removeWhiteSpace(expected));

		expected = "mode m2 (require true;);";

		visitor = new PrettyPrintVisitor();
		visitor.visit(new Mode("m2", Collections.singletonList(LustreUtil.TRUE), null));

		assertEquals(removeWhiteSpace(visitor.toString()), removeWhiteSpace(expected));

		expected = "mode m3 (ensure true;);";

		visitor = new PrettyPrintVisitor();
		visitor.visit(new Mode("m3", null, Collections.singletonList(LustreUtil.TRUE)));

		assertEquals(removeWhiteSpace(visitor.toString()), removeWhiteSpace(expected));

		expected = "mode m4 (require true; ensure true;);";

		visitor = new PrettyPrintVisitor();
		visitor.visit(new Mode("m4", Collections.singletonList(LustreUtil.TRUE),
				Collections.singletonList(LustreUtil.TRUE)));

		assertEquals(removeWhiteSpace(visitor.toString()), removeWhiteSpace(expected));
	}

	@Test
	void ContractBodyTest() {
		// raw strings require Java 12+
		String expected = "guarantee true; assume true; import Spec() returns ();"
				+ "mode m(); const c = true; var x : bool = true;";

		ContractBodyBuilder c = new ContractBodyBuilder();

		Assertions.assertThrows(JKindException.class, c::build);

		c.addGuarantee(LustreUtil.TRUE);
		c.addAssumption(LustreUtil.TRUE);
		c.importContract("Spec", null, null);
		c.addMode("m", null, null);
		c.createConstant("c", null, LustreUtil.TRUE);
		c.createVarDef("x", NamedType.BOOL, LustreUtil.TRUE);

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(c.build());

		assertEquals(removeWhiteSpace(visitor.toString()), removeWhiteSpace(expected));
	}

	@Test
	void ContractTest() {
		// raw strings require Java 12+
		String expected = "contract c() returns (); let guarantee true; tel;";

		ContractBodyBuilder c = new ContractBodyBuilder();
		c.addGuarantee(LustreUtil.TRUE);

		Assertions.assertThrows(JKindException.class,
				() -> new Contract(null, null, null, c.build()));

		Assertions.assertThrows(JKindException.class, () -> new Contract("c", null, null, null));


		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(new Contract("c", null, null, c.build()));

		assertEquals(removeWhiteSpace(visitor.toString()), removeWhiteSpace(expected));
	}

	@Test
	void ImportedNodeTest() {
		Assertions.assertThrows(JKindException.class,
				() -> new ImportedNode(null, null, null, null));

		String expected = "node imported n() returns ();";

		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
		visitor.visit(new ImportedNode("n", null, null, null));

		assertEquals(removeWhiteSpace(visitor.toString()), removeWhiteSpace(expected));

		// raw strings require Java 12+
		expected = "node imported n() returns (); (*@contract guarantee true; *)";

		ContractBodyBuilder c = new ContractBodyBuilder();
		c.addGuarantee(LustreUtil.TRUE);

		visitor = new PrettyPrintVisitor();
		visitor.visit(new ImportedNode("n", null, null, c.build()));

		assertEquals(removeWhiteSpace(visitor.toString()), removeWhiteSpace(expected));
	}
}
