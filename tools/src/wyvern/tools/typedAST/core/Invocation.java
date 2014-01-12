package wyvern.tools.typedAST.core;

import static wyvern.tools.errors.ErrorMessage.CANNOT_INVOKE;
import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ToolError.reportError;
import static wyvern.tools.errors.ToolError.reportEvalError;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.core.values.VarValue;
import wyvern.tools.typedAST.interfaces.Assignable;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.CoreASTVisitor;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.OperatableType;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class Invocation extends CachingTypedAST implements CoreAST, Assignable {
	private String operationName;
	private TypedAST receiver;
	private TypedAST argument;

	public Invocation(TypedAST op1, String operatorName, TypedAST op2, FileLocation fileLocation) {
		this.receiver = op1;
		this.argument = op2;
		this.operationName = operatorName;
		this.location = fileLocation;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(receiver, operationName, argument);
	}

	@Override
	protected Type doTypecheck(Environment env) {
		Type receiverType = receiver.typecheck(env);
		
		if (argument != null)
			argument.typecheck(env);
		
		if (receiverType instanceof OperatableType) {
			return ((OperatableType)receiverType).checkOperator(this,env);
		} else {
			ToolError.reportError(ErrorMessage.OPERATOR_DOES_NOT_APPLY, this, "Trying to call a function on non OperatableType!", receiverType.toString());
			return null;
		}
	}

	public TypedAST getArgument() {
		return argument;
	}
	public TypedAST getReceiver() {
		return receiver;
	}
	public String getOperationName() {
		return operationName;
	}
	
	@Override
	public Value evaluate(Environment env) {
		Value lhs = receiver.evaluate(env);
		if (!(lhs instanceof InvokableValue))
			reportEvalError(CANNOT_INVOKE, lhs.toString(), this);
		InvokableValue receiverValue = (InvokableValue) lhs;
		Value out = receiverValue.evaluateInvocation(this, env);
		
		//TODO: bit of a hack
		if (out instanceof VarValue)
			out = ((VarValue)out).getValue();
		return out;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Value evaluateAssignment(Assignment ass, Environment env) {
		Value lhs = receiver.evaluate(env);
		if (!(lhs instanceof Assignable))
			reportEvalError(CANNOT_INVOKE, lhs.toString(), this);
		
		return ((Assignable)lhs).evaluateAssignment(ass, env);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("receiver", receiver);
		children.put("argument", argument);
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new Invocation(nc.get("receiver"), operationName, nc.get("argument"), location);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}