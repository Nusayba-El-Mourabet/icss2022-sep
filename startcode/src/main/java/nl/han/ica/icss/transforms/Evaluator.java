package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues.addFirst(new HashMap<>());
        applyStylesheet((Stylesheet) ast.root);
        variableValues.removeFirst();
    }

    private void applyStylesheet(Stylesheet stylesheet) {
        //bijv boom van width: 10px + 20px,  met ASTNODE AddOperation met nodes van beide 10px en 20px
        // wegwerken en tot 1 node maken
        for (ASTNode child : stylesheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                applyVariableassignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                applyStylerule((Stylerule) child);
            }
        }
    }

    private void applyVariableassignment(VariableAssignment variableAssignment) {
        Literal evaluatedValue = (Literal) evalExpression(variableAssignment.expression);
        variableValues.getFirst().put(variableAssignment.name.name, evaluatedValue);
    }

    private void applyStylerule(Stylerule stylerule) {
        ArrayList<ASTNode> transformedBody = new ArrayList<>();

        for (ASTNode child : stylerule.body) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
                transformedBody.add(child);
            } else if (child instanceof VariableAssignment) {
                applyVariableassignment((VariableAssignment) child);
                transformedBody.add(child);
            } else if (child instanceof IfClause) {
                applyIfClause((IfClause) child, transformedBody);
            }
        }
        //if clauses weghalen en alleen correcte nodes in body zetten
        stylerule.body.clear();
        stylerule.body.addAll(transformedBody);
    }

    private void applyDeclaration(Declaration declaration) {
        declaration.expression = evalExpression(declaration.expression);
    }
    private void applyIfClause(IfClause ifClause, List<ASTNode> transformedBody) {
        Literal conditionResult = (Literal) evalExpression(ifClause.conditionalExpression);

        if (conditionResult instanceof BoolLiteral) {
            boolean conditionValue = ((BoolLiteral) conditionResult).value;

            if (conditionValue) {
                for (ASTNode node : ifClause.body) {
                    if (node instanceof IfClause) {
                        applyIfClause((IfClause) node, transformedBody); //nested ifclauses ook behandelen
                    } else {
                        transformedBody.add(node);
                    }
                }
            } else if (ifClause.elseClause != null) {
                // if conditie = FALSE, check of er nog een else clause behandeld moet worden
                for (ASTNode node : ifClause.elseClause.body) {
                    if (node instanceof IfClause) {
                        applyIfClause((IfClause) node, transformedBody);  //nested ifclauses ook behandelen
                    } else {
                        transformedBody.add(node);
                    }
                }
            }
            //in het geval van eerste IFclause = FALSE en er is geen ELSE statement dan hoeft niks toegevoegd te worden
        }
    }

    private Expression evalExpression(Expression expression) {
        if (expression instanceof Literal) {
            return expression;
        } else if (expression instanceof VariableReference) {
            String varName = ((VariableReference) expression).name;
            Literal value = lookupVariableValue(varName);
            if (value != null) {
                return value;
            }
            return expression;
        } else if (expression instanceof Operation) {
            return applyOperation((Operation) expression);
        }
        return expression;
    }

    private Literal applyOperation(Operation operation) {
        Literal left = (Literal) evalExpression(operation.lhs);
        Literal right = (Literal) evalExpression(operation.rhs);

        if (operation instanceof AddOperation) {
            return applyAddOperation(left, right);
        } else if (operation instanceof SubtractOperation) {
            return applySubtractOperation(left, right);
        } else if (operation instanceof MultiplyOperation) {
            return applyMultiplyOperation(left, right);
        }
        return null;
    }

    private Literal applyAddOperation(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value + ((ScalarLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        }
        return null;
    }

    private Literal applySubtractOperation(Literal left, Literal right) {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value - ((ScalarLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        }
        return null;
    }

    private Literal applyMultiplyOperation(Literal left, Literal right) {
        if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
        } else if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((ScalarLiteral) left).value * ((PercentageLiteral) right).value);
        } else if (left instanceof PercentageLiteral && right instanceof ScalarLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
        } else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral) {
            return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
        }
        return null;
    }

    private Literal lookupVariableValue(String varName) {
        for (int i = variableValues.getSize() - 1; i >= 0; i--) {
            HashMap<String, Literal> scope = variableValues.get(i);
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        return null;
    }
}
