package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class Checker {

    //    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        // variableTypes = new HANLinkedList<>();
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
            }
        }
        variableTypes.removeFirst(); // Remove scope after exiting Stylerule
    }

    private void checkDeclaration(Declaration declaration) {
        String propertyName = declaration.property.name;
        Expression expression = declaration.expression;

        if (expression instanceof VariableReference) {
            //
            VariableReference varRef = (VariableReference) expression;
            ExpressionType varType = lookupVariableType(varRef.name);

            if (varType == null) {
                declaration.setError("Variable '" + varRef.name + "' is not declared.");
                return;
            }
                //verwachte type vergelijken met variabe datatype
            if ((propertyName.equals("width") || propertyName.equals("height")) && varType != ExpressionType.PIXEL) {
                declaration.setError("Property '" + propertyName + "' expects a pixel value.");
            } else if ((propertyName.equals("color") || propertyName.equals("background-color")) && varType != ExpressionType.COLOR) {
                declaration.setError("Property '" + propertyName + "' expects a color value.");
            }

        } else {
            //als er geen variabelen worden gebruikt dan check de gewone expressie :)
            if ((propertyName.equals("width") || propertyName.equals("height")) && !(expression instanceof PixelLiteral)) {
                declaration.setError("Property '" + propertyName + "' expects a pixel value.");
            } else if ((propertyName.equals("color") || propertyName.equals("background-color")) && !(expression instanceof ColorLiteral)) {
                declaration.setError("Property '" + propertyName + "' expects a color value.");
            }
        }
    }

    private ExpressionType lookupVariableType(String varName) {
        for (HashMap<String, ExpressionType> scope : variableTypes) {
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        return null;
    }

    private void checkVariableAssignment(VariableAssignment node) {
        ExpressionType type = determineExpressionType(node.expression);
        if (type == ExpressionType.UNDEFINED) {
            node.setError("Invalid expression type for variable assignment.");
        } else {
            variableTypes.getFirst().put(node.name.name, type);
        }
    }

    private ExpressionType determineExpressionType(Expression expression) {
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
        }
        return ExpressionType.UNDEFINED;
    }
}
