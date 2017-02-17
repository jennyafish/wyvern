package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.Type;

public class TaggedTypeMember extends DeclType {
	private Type typeDefn;
	// TODO: add metadata, just as with a ConcreteTypeMember

	public TaggedTypeMember(String name, Type typeDefn) {
		super(name);
		this.typeDefn = typeDefn;
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) {
		if (!(dt instanceof TaggedTypeMember))
			return false;
		Type otherTypeDefn = ((TaggedTypeMember)dt).typeDefn;
		return typeDefn.equals(otherTypeDefn);
	}

	public Type getTypeDefinition(View view) {
		return typeDefn.adapt(view);
	}
	
	@Override
	public DeclType adapt(View v) {
		return new TaggedTypeMember(getName(), this.getTypeDefinition(v));
	}

	@Override
	public void checkWellFormed(TypeContext ctx) {
		typeDefn.checkWellFormed(ctx);
	}

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(indent).append("tagged type ").append(getName()).append(" is ");
		typeDefn.doPrettyPrint(dest, indent);
		dest.append('\n');
	}

	@Override
	public DeclType doAvoid(String varName, TypeContext ctx, int count) {
		Type t = typeDefn.doAvoid(varName, ctx, count);
		if (t.equals(typeDefn)) {
			return this;
		} else {
			return new TaggedTypeMember(this.getName(),t);
		}
	}

	@Override
	public boolean isTypeDecl() {
		return true;
	}

}
