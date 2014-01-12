package wyvern.tools.typedAST.interfaces;

import wyvern.tools.errors.HasLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWritable;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public interface TypedAST extends TreeWritable, HasLocation {

	/** should call typecheck() before getType() -- except maybe for declarations */
	Type getType();
	Type typecheck(Environment env);
	
	/** an interpreter */
	Value evaluate(Environment env);
	
	/** may return null */
	LineParser getLineParser();

	/** may return null */
	LineSequenceParser getLineSequenceParser();

	/**
	 * Gets the children of a composite node
	 * @return The children of the node
	 */
	Map<String, TypedAST> getChildren();
	/**
	 * Clones the current AST node with the given set of children
	 * @param newChildren The children to create
	 * @return The deep-copied AST node
	 */
	TypedAST cloneWithChildren(Map<String, TypedAST> newChildren);
}
