package jkind.lustre.visitors;

import jkind.lustre.*;

public interface AstVisitor<T, S extends T> extends ExprVisitor<S> {
	public T visit(Assume assumption);

	public T visit(Contract contract);

	public T visit(ContractBody contractBody);

	public T visit(ContractImport contractImport);

	public T visit(Constant constant);

	public T visit(Equation equation);

	public T visit(Function function);

	public T visit(Guarantee guarantee);

	public T visit(ImportedFunction importedFunction);

	public T visit(ImportedNode importedNode);

	public T visit(Kind2Function kind2Function);

	public T visit(Mode mode);

	public T visit(Node node);

	public T visit(Program program);

	public T visit(TypeDef typeDef);

	public T visit(VarDecl varDecl);

	public T visit(VarDef varDef);
}
