package jkind.lustre.parsing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jkind.ExitCodes;
import jkind.JKindException;
import jkind.Output;
import jkind.lustre.ArrayAccessExpr;
import jkind.lustre.ArrayExpr;
import jkind.lustre.ArrayType;
import jkind.lustre.ArrayUpdateExpr;
import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.CastExpr;
import jkind.lustre.CondactExpr;
import jkind.lustre.Constant;
import jkind.lustre.Contract;
import jkind.lustre.EnumType;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.InductDataExpr;
import jkind.lustre.InductType;
import jkind.lustre.InductTypeElement;
import jkind.lustre.IntExpr;
import jkind.lustre.Location;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.Program;
import jkind.lustre.QuantExpr;
import jkind.lustre.QuantOp;
import jkind.lustre.RealExpr;
import jkind.lustre.RecordAccessExpr;
import jkind.lustre.RecordExpr;
import jkind.lustre.RecordType;
import jkind.lustre.RecordUpdateExpr;
import jkind.lustre.RecursiveFunction;
import jkind.lustre.SubrangeIntType;
import jkind.lustre.TupleExpr;
import jkind.lustre.Type;
import jkind.lustre.TypeConstructor;
import jkind.lustre.TypeDef;
import jkind.lustre.UnaryExpr;
import jkind.lustre.UnaryOp;
import jkind.lustre.VarDecl;
import jkind.lustre.parsing.LustreParser.ArrayAccessExprContext;
import jkind.lustre.parsing.LustreParser.ArrayEIDContext;
import jkind.lustre.parsing.LustreParser.ArrayExprContext;
import jkind.lustre.parsing.LustreParser.ArrayTypeContext;
import jkind.lustre.parsing.LustreParser.ArrayUpdateExprContext;
import jkind.lustre.parsing.LustreParser.AssertionContext;
import jkind.lustre.parsing.LustreParser.BaseEIDContext;
import jkind.lustre.parsing.LustreParser.BinaryExprContext;
import jkind.lustre.parsing.LustreParser.BoolExprContext;
import jkind.lustre.parsing.LustreParser.BoolTypeContext;
import jkind.lustre.parsing.LustreParser.CastExprContext;
import jkind.lustre.parsing.LustreParser.CondactExprContext;
import jkind.lustre.parsing.LustreParser.ConstantContext;
import jkind.lustre.parsing.LustreParser.ContractContext;
import jkind.lustre.parsing.LustreParser.EIDContext;
import jkind.lustre.parsing.LustreParser.EnsureContext;
import jkind.lustre.parsing.LustreParser.EnumTypeContext;
import jkind.lustre.parsing.LustreParser.EquationContext;
import jkind.lustre.parsing.LustreParser.ExprContext;
import jkind.lustre.parsing.LustreParser.IdExprContext;
import jkind.lustre.parsing.LustreParser.IfThenElseExprContext;
import jkind.lustre.parsing.LustreParser.InductDataExprContext;
import jkind.lustre.parsing.LustreParser.InductTermContext;
import jkind.lustre.parsing.LustreParser.InductTypeContext;
import jkind.lustre.parsing.LustreParser.IntExprContext;
import jkind.lustre.parsing.LustreParser.IntTypeContext;
import jkind.lustre.parsing.LustreParser.LhsContext;
import jkind.lustre.parsing.LustreParser.NegateExprContext;
import jkind.lustre.parsing.LustreParser.NodeCallExprContext;
import jkind.lustre.parsing.LustreParser.NodeContext;
import jkind.lustre.parsing.LustreParser.NotExprContext;
import jkind.lustre.parsing.LustreParser.PlainTypeContext;
import jkind.lustre.parsing.LustreParser.PreExprContext;
import jkind.lustre.parsing.LustreParser.ProgramContext;
import jkind.lustre.parsing.LustreParser.PropertyContext;
import jkind.lustre.parsing.LustreParser.QuantExprContext;
import jkind.lustre.parsing.LustreParser.RealExprContext;
import jkind.lustre.parsing.LustreParser.RealTypeContext;
import jkind.lustre.parsing.LustreParser.RealizabilityInputsContext;
import jkind.lustre.parsing.LustreParser.RecordAccessExprContext;
import jkind.lustre.parsing.LustreParser.RecordEIDContext;
import jkind.lustre.parsing.LustreParser.RecordExprContext;
import jkind.lustre.parsing.LustreParser.RecordTypeContext;
import jkind.lustre.parsing.LustreParser.RecordUpdateExprContext;
import jkind.lustre.parsing.LustreParser.RecursiveContext;
import jkind.lustre.parsing.LustreParser.RequireContext;
import jkind.lustre.parsing.LustreParser.SubrangeTypeContext;
import jkind.lustre.parsing.LustreParser.TopLevelTypeContext;
import jkind.lustre.parsing.LustreParser.TupleExprContext;
import jkind.lustre.parsing.LustreParser.TypeContext;
import jkind.lustre.parsing.LustreParser.TypedefContext;
import jkind.lustre.parsing.LustreParser.UserTypeContext;
import jkind.lustre.parsing.LustreParser.VarDeclGroupContext;
import jkind.lustre.parsing.LustreParser.VarDeclListContext;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class LustreToAstVisitor extends LustreBaseVisitor<Object> {
	private String main;

	public Program program(ProgramContext ctx) {
		List<TypeDef> types = types(ctx.typedef());
		List<Constant> constants = constants(ctx.constant());
		List<Node> nodes = nodes(ctx.node());
		List<RecursiveFunction> recFuns = recursives(ctx.recursive()); 
		return new Program(loc(ctx), types, constants, nodes, main, recFuns);
	}

	private List<TypeDef> types(List<TypedefContext> ctxs) {
		List<TypeDef> types = new ArrayList<>();
		for (TypedefContext ctx : ctxs) {
			String id = ctx.ID().getText();
			Type type = topLevelType(id, ctx.topLevelType());
			types.add(new TypeDef(loc(ctx), id, type));
		}
		return types;
	}

	private List<Constant> constants(List<ConstantContext> ctxs) {
		List<Constant> constants = new ArrayList<>();
		for (ConstantContext ctx : ctxs) {
			String id = ctx.ID().getText();
			Type type = ctx.type() == null ? null : type(ctx.type());
			Expr expr = expr(ctx.expr());
			constants.add(new Constant(loc(ctx), id, type, expr));
		}
		return constants;
	}

	private List<Node> nodes(List<NodeContext> ctxs) {
		List<Node> nodes = new ArrayList<>();
		for (NodeContext ctx : ctxs) {
			nodes.add(node(ctx));
		}
		return nodes;
	}

	private Node node(NodeContext ctx) {
		String id = ctx.ID().getText();
		List<VarDecl> inputs = varDecls(ctx.input);
		List<VarDecl> outputs = varDecls(ctx.output);
		List<VarDecl> locals = varDecls(ctx.local);
		List<Equation> equations = equations(ctx.equation());
		List<String> properties = properties(ctx.property());
		List<Expr> assertions = assertions(ctx.assertion());
		Optional<List<String>> realizabilityInputs = realizabilityInputs(ctx.realizabilityInputs());
		Optional<List<Contract>> contracts = contracts(ctx.contract());
		if (!ctx.main().isEmpty()) {
			main = id;
		}
		return new Node(loc(ctx), id, inputs, outputs, locals, equations, properties, assertions,
				realizabilityInputs, contracts);
    }

    private List<RecursiveFunction> recursives(List<RecursiveContext> ctxs) {
        List<RecursiveFunction> recFuns = new ArrayList<>();
        for (RecursiveContext ctx : ctxs) {
            recFuns.add(recursive(ctx));
        }
        return recFuns;
    }

    private RecursiveFunction recursive(RecursiveContext ctx) {
        String id = ctx.ID().getText();
        List<VarDecl> inputs = varDecls(ctx.input);
        List<VarDecl> outputs = varDecls(ctx.output);
        List<VarDecl> locals = varDecls(ctx.local);
        List<Equation> equations = equations(ctx.equation());
        if (outputs.size() != 1) {
            fatal(ctx.output, "Recursive functions must have a single output");
        }
        return new RecursiveFunction(loc(ctx), id, inputs, locals, outputs.get(0), equations);
    }

	private List<VarDecl> varDecls(VarDeclListContext listCtx) {
		List<VarDecl> decls = new ArrayList<>();
		if (listCtx == null) {
			return decls;
		}

		for (VarDeclGroupContext groupCtx : listCtx.varDeclGroup()) {
			Type type = type(groupCtx.type());
			for (EIDContext id : groupCtx.eID()) {
				decls.add(new VarDecl(loc(id), eid(id), type));
			}
		}
		return decls;
	}

	private List<Equation> equations(List<EquationContext> ctxs) {
		List<Equation> equations = new ArrayList<>();
		for (EquationContext ctx : ctxs) {
			List<IdExpr> lhs = lhs(ctx.lhs());
			Expr expr = expr(ctx.expr());
			equations.add(new Equation(loc(ctx), lhs, expr));
		}
		return equations;
	}

	private List<IdExpr> lhs(LhsContext ctx) {
		List<IdExpr> lhs = new ArrayList<>();
		if (ctx != null) {
			for (EIDContext id : ctx.eID()) {
				lhs.add(new IdExpr(loc(id), eid(id)));
			}
		}
		return lhs;
	}

	private String eid(EIDContext id) {
		return (String) visit(id);
	}

	private List<String> properties(List<PropertyContext> ctxs) {
		List<String> props = new ArrayList<>();
		for (PropertyContext ctx : ctxs) {
			props.add(eid(ctx.eID()));
		}
		return props;
	}
	
	private Optional<List<Contract>> contracts(List<ContractContext> ctxs) {
		List<Contract> contracts = new ArrayList<>();
		for (ContractContext ctx : ctxs) {
			String name = ctx.contract_id().ID().getText();
			Contract contract = new Contract(name, requires(ctx.require()), ensures(ctx.ensure()));
			contracts.add(contract);
		}
		
		if(contracts.isEmpty()){
			return Optional.empty();
		}
		return Optional.of(contracts);
	}
	
	private List<Expr> requires(List<RequireContext> ctxs){
		List<Expr> requires = new ArrayList<>();
		for(RequireContext ctx : ctxs){
			requires.add(expr(ctx.expr()));
		}
		return requires;
	}
	
	private List<Expr> ensures(List<EnsureContext> ctxs){
		List<Expr> ensures = new ArrayList<>();
		for(EnsureContext ctx : ctxs){
			ensures.add(expr(ctx.expr()));
		}
		return ensures;
	}

	private List<Expr> assertions(List<AssertionContext> ctxs) {
		List<Expr> assertions = new ArrayList<>();
		for (AssertionContext ctx : ctxs) {
			assertions.add(expr(ctx.expr()));
		}
		return assertions;
	}


	
	private Optional<List<String>> realizabilityInputs(List<RealizabilityInputsContext> ctxs) {
		if (ctxs.size() > 1) {
			fatal(ctxs.get(1), "at most one realizability statement allowed");
		}

		for (RealizabilityInputsContext ctx : ctxs) {
			List<String> ids = new ArrayList<>();
			for (TerminalNode ictx : ctx.ID()) {
				ids.add(ictx.getText());
			}
			return Optional.of(ids);
		}

		return Optional.empty();
	}

	private Type topLevelType(String id, TopLevelTypeContext ctx) {
		if (ctx instanceof PlainTypeContext) {
			PlainTypeContext pctx = (PlainTypeContext) ctx;
			return type(pctx.type());
		} else if (ctx instanceof RecordTypeContext) {
			RecordTypeContext rctx = (RecordTypeContext) ctx;
			Map<String, Type> fields = new HashMap<>();
			for (int i = 0; i < rctx.ID().size(); i++) {
				String field = rctx.ID(i).getText();
				if (fields.containsKey(field)) {
					fatal(ctx, "field declared multiple times: " + field);
				}
				fields.put(field, type(rctx.type(i)));
			}
			return new RecordType(loc(ctx), id, fields);
		} else if (ctx instanceof EnumTypeContext) {
			EnumTypeContext ectx = (EnumTypeContext) ctx;
			List<String> values = new ArrayList<>();
			for (TerminalNode node : ectx.ID()) {
				values.add(node.getText());
			}
			return new EnumType(loc(ctx), id, values);
		} else if (ctx instanceof InductTypeContext){
			InductTypeContext ictx = (InductTypeContext) ctx;
			List<TypeConstructor> typeConstructors = new ArrayList<>();
			for (InductTermContext term : ictx.inductTerm()){
				String constrName = term.ID(0).getText();
				List<InductTypeElement> elements = new ArrayList<>();
				for(int i = 1; i < term.ID().size(); i++){
					String elName = term.ID(i).getText();
					String elTypeName = term.type(i-1).getText();
					NamedType elType;
					
					switch(elTypeName){
					case "int":
						elType = NamedType.INT;
						break;
					case "real":
						elType = NamedType.REAL;
						break;
					case "bool":
						elType = NamedType.BOOL;
						break;
					default:
						elType = new NamedType(elTypeName);
					}
					
					elements.add(new InductTypeElement(elName, elType));
				}
				typeConstructors.add(new TypeConstructor(constrName, elements));
			}
			return new InductType(loc(ctx), id, typeConstructors);
		} else {
			throw new IllegalArgumentException(ctx.getClass().getSimpleName());
		}
	}

	private Type type(TypeContext ctx) {
		return (Type) ctx.accept(this);
	}

	@Override
	public Type visitIntType(IntTypeContext ctx) {
		return NamedType.INT;
	}

	@Override
	public Type visitSubrangeType(SubrangeTypeContext ctx) {
		BigInteger low = new BigInteger(ctx.bound(0).getText());
		BigInteger high = new BigInteger(ctx.bound(1).getText());
		return new SubrangeIntType(loc(ctx), low, high);
	}

	@Override
	public Type visitBoolType(BoolTypeContext ctx) {
		return NamedType.BOOL;
	}

	@Override
	public Type visitRealType(RealTypeContext ctx) {
		return NamedType.REAL;
	}

	@Override
	public Type visitArrayType(ArrayTypeContext ctx) {
		try {
			int index = Integer.parseInt(ctx.INT().getText());
			if (index == 0) {
				fatal(ctx, "array size must be non-zero");
			}
			return new ArrayType(loc(ctx), type(ctx.type()), index);
		} catch (NumberFormatException nfe) {
			fatal(ctx, "array size too large: " + ctx.INT().getText());
			return null;
		}
	}

	@Override
	public Type visitUserType(UserTypeContext ctx) {
		return new NamedType(loc(ctx), ctx.ID().getText());
	}

	private Expr expr(ExprContext ctx) {
		return (Expr) ctx.accept(this);
	}

	@Override
	public Expr visitIdExpr(IdExprContext ctx) {
		return new IdExpr(loc(ctx), ctx.ID().getText());
	}

	@Override
	public Expr visitIntExpr(IntExprContext ctx) {
		return new IntExpr(loc(ctx), new BigInteger(ctx.INT().getText()));
	}

	@Override
	public Expr visitRealExpr(RealExprContext ctx) {
		return new RealExpr(loc(ctx), new BigDecimal(ctx.REAL().getText()));
	}

	@Override
	public Expr visitBoolExpr(BoolExprContext ctx) {
		return new BoolExpr(loc(ctx), ctx.BOOL().getText().equals("true"));
	}

	@Override
	public Expr visitCastExpr(CastExprContext ctx) {
		return new CastExpr(loc(ctx), getCastType(ctx.op.getText()), expr(ctx.expr()));
	}

	private Type getCastType(String cast) {
		switch (cast) {
		case "real":
			return NamedType.REAL;
		case "floor":
			return NamedType.INT;
		default:
			throw new IllegalArgumentException("Unknown cast: " + cast);
		}
	}

	@Override
	public NodeCallExpr visitNodeCallExpr(NodeCallExprContext ctx) {
		String node = ctx.ID().getText();
		List<Expr> args = new ArrayList<>();
		for (ExprContext arg : ctx.expr()) {
			args.add(expr(arg));
		}
		return new NodeCallExpr(loc(ctx), node, args);
	}

	@Override
	public Expr visitCondactExpr(CondactExprContext ctx) {
		Expr clock = expr(ctx.expr(0));
		if (ctx.expr(1) instanceof NodeCallExprContext) {
			NodeCallExprContext callCtx = (NodeCallExprContext) ctx.expr(1);
			NodeCallExpr call = visitNodeCallExpr(callCtx);
			List<Expr> args = new ArrayList<>();
			for (int i = 2; i < ctx.expr().size(); i++) {
				args.add(expr(ctx.expr(i)));
			}
			return new CondactExpr(loc(ctx), clock, call, args);
		} else {
			fatal(ctx, "second argument to condact must be a node call");
			return null;
		}
	}

	@Override
	public Expr visitRecordAccessExpr(RecordAccessExprContext ctx) {
		return new RecordAccessExpr(loc(ctx), expr(ctx.expr()), ctx.ID().getText());
	}

	@Override
	public Expr visitRecordUpdateExpr(RecordUpdateExprContext ctx) {
		return new RecordUpdateExpr(loc(ctx), expr(ctx.expr(0)), ctx.ID().getText(),
				expr(ctx.expr(1)));
	}
	
	@Override
	public Expr visitQuantExpr(QuantExprContext ctx){
		ParseTree quant = ctx.getChild(0);
		QuantOp quantOp;
		switch(quant.getText()){
		case "forall":
			quantOp = QuantOp.FORALL;
			break;
		case "exists":
			quantOp = QuantOp.EXISTS;
			break;
		default:
			throw new JKindException("unkown quantifier '"+quant.getText()+"'");
		}
		
		List<VarDecl> vars = varDecls(ctx.vars);
		Expr expr = expr(ctx.expr());
		
		return new QuantExpr(loc(ctx), quantOp, vars, expr);
	}

	@Override
	public Expr visitArrayAccessExpr(ArrayAccessExprContext ctx) {
		return new ArrayAccessExpr(loc(ctx), expr(ctx.expr(0)), expr(ctx.expr(1)));
	}

	@Override
	public Expr visitArrayUpdateExpr(ArrayUpdateExprContext ctx) {
		return new ArrayUpdateExpr(loc(ctx), expr(ctx.expr(0)), expr(ctx.expr(1)),
				expr(ctx.expr(2)));
	}

	@Override
	public Expr visitPreExpr(PreExprContext ctx) {
		return new UnaryExpr(loc(ctx), UnaryOp.PRE, expr(ctx.expr()));
	}

	@Override
	public Expr visitNotExpr(NotExprContext ctx) {
		return new UnaryExpr(loc(ctx), UnaryOp.NOT, expr(ctx.expr()));
	}

	@Override
	public Expr visitNegateExpr(NegateExprContext ctx) {
		return new UnaryExpr(loc(ctx), UnaryOp.NEGATIVE, expr(ctx.expr()));
	}

	@Override
	public Expr visitBinaryExpr(BinaryExprContext ctx) {
		String op = ctx.op.getText();
		Expr left = expr(ctx.expr(0));
		Expr right = expr(ctx.expr(1));
		return new BinaryExpr(loc(ctx.op), left, BinaryOp.fromString(op), right);
	}

	@Override
	public Expr visitIfThenElseExpr(IfThenElseExprContext ctx) {
		return new IfThenElseExpr(loc(ctx), expr(ctx.expr(0)), expr(ctx.expr(1)), expr(ctx.expr(2)));
	}

	@Override
	public Expr visitRecordExpr(RecordExprContext ctx) {
		Map<String, Expr> fields = new HashMap<>();
		for (int i = 0; i < ctx.expr().size(); i++) {
			String field = ctx.ID(i + 1).getText();
			if (fields.containsKey(field)) {
				fatal(ctx, "field assigned multiple times: " + field);
			}
			fields.put(field, expr(ctx.expr(i)));
		}
		return new RecordExpr(loc(ctx), ctx.ID(0).getText(), fields);
	}

	@Override
	public Expr visitArrayExpr(ArrayExprContext ctx) {
		List<Expr> elements = new ArrayList<>();
		for (int i = 0; i < ctx.expr().size(); i++) {
			elements.add(expr(ctx.expr(i)));
		}
		return new ArrayExpr(loc(ctx), elements);
	}

	@Override
	public Expr visitTupleExpr(TupleExprContext ctx) {
		// Treat singleton tuples as simply parentheses. This increases parsing
		// performance by not having to decide between parenExpr and tupleExpr.
		if (ctx.expr().size() == 1) {
			return expr(ctx.expr(0));
		}

		List<Expr> elements = new ArrayList<>();
		for (int i = 0; i < ctx.expr().size(); i++) {
			elements.add(expr(ctx.expr(i)));
		}
		return new TupleExpr(loc(ctx), elements);
	}

	@Override
	public String visitBaseEID(BaseEIDContext ctx) {
		return ctx.ID().getText();
	}

	@Override
	public String visitArrayEID(ArrayEIDContext ctx) {
		return visit(ctx.eID()) + "[" + ctx.INT().getText() + "]";
	}

	@Override
	public String visitRecordEID(RecordEIDContext ctx) {
		return visit(ctx.eID()) + "." + ctx.ID().getText();
	}
	
	@Override
	public Expr visitInductDataExpr(InductDataExprContext ctx){
		List<Expr> args = new ArrayList<>();
		for(ExprContext expCtx : ctx.expr()){
			args.add(expr(expCtx));
		}
		return new InductDataExpr(loc(ctx), ctx.ID().getText(), args);
	}

	private static Location loc(ParserRuleContext ctx) {
		return loc(ctx.getStart());
	}

	private static Location loc(Token token) {
		return new Location(token.getLine(), token.getCharPositionInLine());
	}

	private static void fatal(ParserRuleContext ctx, String text) {
		Output.error(loc(ctx), text);
		System.exit(ExitCodes.PARSE_ERROR);
	}
}
