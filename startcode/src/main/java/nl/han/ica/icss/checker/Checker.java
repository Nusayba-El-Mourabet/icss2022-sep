package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public Checker() {
        variableTypes = new HANLinkedList<>();
    }

    public void check(AST ast) {
        variableTypes.clear();
        variableTypes.addFirst(new HashMap<>()); // Add global scope
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet stylesheet) {
        for (ASTNode child : stylesheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
        }

        for (ASTNode child : stylesheet.getChildren()) {
            if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
    }

    private void checkStylerule(Stylerule stylerule) {
        variableTypes.addFirst(new HashMap<>()); //Een nieuwe scope per stylerule
        for (ASTNode child : stylerule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
        }
        variableTypes.removeFirst();
    }

    private void checkIfClause(IfClause ifClause) {
        ExpressionType conditionType = evaluateExpressionType(ifClause.conditionalExpression, ifClause);
        if (conditionType != ExpressionType.BOOL) {
            ifClause.setError("Condition in 'if' statement must be a boolean expression.");
        }

        variableTypes.addFirst(new HashMap<>());
        for (ASTNode child : ifClause.getChildren()) { // wat binnen de ifclase staat ook checken inclusief een nested ifclause
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
        }
        variableTypes.removeFirst();

        if (ifClause.elseClause != null) {
            checkElseClause(ifClause.elseClause);
        }
    }
    private void checkElseClause(ElseClause elseClause) {
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode child : elseClause.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
        }
        variableTypes.removeFirst();
    }

    private void checkDeclaration(Declaration declaration) {
        String propertyName = declaration.property.name;
        Expression expression = declaration.expression;
        ExpressionType expressionType = evaluateExpressionType(expression, declaration);

        // verwachte expressiontype per property
        if (propertyName.equals("width") || propertyName.equals("height")) {
            if (expressionType != ExpressionType.PIXEL) {
                declaration.setError("Property '" + propertyName + "' expects a pixel value.");
            }
        } else if (propertyName.equals("color") || propertyName.equals("background-color")) {
            if (expressionType != ExpressionType.COLOR) {
                declaration.setError("Property '" + propertyName + "' expects a color value.");
            }
        }
    }

    private ExpressionType evaluateExpressionType(Expression expression, ASTNode parent) {
        if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof VariableReference) {
            ExpressionType varType = lookupVariableType(((VariableReference) expression).name);
            if (varType == null) {
                expression.setError("Variable '" + ((VariableReference) expression).name + "' is not declared in this scope.");
                return ExpressionType.UNDEFINED;
            }
            return varType;
        } else if (expression instanceof Operation) {
            return evaluateOperationType((Operation) expression, (Operation) expression);
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType evaluateOperationType(Operation operation, ASTNode parent) {
        ExpressionType leftType = evaluateExpressionType(operation.lhs, parent);
        ExpressionType rightType = evaluateExpressionType(operation.rhs, parent);
        if (leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR) { //geen kleur als input mogelijk
            operation.setError("Cannot use color literals in any operation.");
            return ExpressionType.UNDEFINED;
        }
        if (operation instanceof AddOperation || operation instanceof SubtractOperation) { //verplicht beide operands van hetzelfde type
            if (leftType != rightType) {
                operation.setError("Operands of '" + operation.getNodeLabel() + "' must be of the same type.");
                return ExpressionType.UNDEFINED;
            }
            return leftType;
        } else if (operation instanceof MultiplyOperation) { //verplicht 1 scalar aanwezig
            if (leftType == ExpressionType.SCALAR) {
                return rightType;
            } else if (rightType == ExpressionType.SCALAR) {
                return leftType;
            } else {
                operation.setError("At least one operand of 'multiply' operation must be a scalar.");
                return ExpressionType.UNDEFINED;
            }
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType lookupVariableType(String varName) {
        for (int i = variableTypes.getSize() - 1; i >= 0; i--) {  // Look from innermost to outermost scope
            HashMap<String, ExpressionType> scope = variableTypes.get(i);
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        return null;
    }

    private void checkVariableAssignment(VariableAssignment node) {
        ExpressionType type = evaluateExpressionType(node.expression, node);
        if (type == ExpressionType.UNDEFINED) {
            node.setError("Invalid expression type for variable assignment.");
        } else {
            variableTypes.getFirst().put(node.name.name, type);
        }
    }
}
