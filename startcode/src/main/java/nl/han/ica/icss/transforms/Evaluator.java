package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    //    private IHANLinkedList<HashMap<String, Literal>> variableValues;
    private LinkedList<HashMap<String, Literal>> variableValues;

    //
    public Evaluator() {
        //variableValues = new HANLinkedList<>();
        variableValues = new LinkedList<>();

    }

    @Override
    public void apply(AST ast) {
        //variableValues = new HANLinkedList<>();
        variableValues.addFirst(new HashMap<>());
        applyStylesheet((Stylesheet) ast.root);

    }

    private void applyStylesheet(Stylesheet stylesheet) {
        //bijv boom van width: 10px + 20px,  met ASTNODE AddOperation met nodes van beide 10px en 20px
        // wegwerken en tot 1 node maken
        for (ASTNode child : stylesheet.getChildren()) {
             if (child instanceof VariableAssignment){
                applyVariableassignment((VariableAssignment) child);
            }else if (child instanceof Stylerule) {
                applyStylerule((Stylerule) child);
            }
        }
        variableValues.removeFirst();
    }

    private void applyVariableassignment (VariableAssignment variableAssignment){
        Literal evaluatedValue = (Literal) evalExpression(variableAssignment.expression);
        variableValues.getFirst().put(variableAssignment.name.name, evaluatedValue);
    }

    private void applyStylerule(Stylerule stylerule) {
        for (ASTNode child : stylerule.getChildren()) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            }
        }
    }

    private void applyDeclaration(Declaration declaration) {
        //in Declarartion class hebben we een Property en Expression
        declaration.expression = evalExpression(declaration.expression);// om een expressie uit te rekenen
    }

    private Expression evalExpression(Expression expression) {
        if (expression instanceof Literal) {
            return expression;
        }else if (expression instanceof VariableReference){
            String varName = ((VariableReference) expression).name;
            Literal value = lookupVariableValue(varName);
            if (value != null) {
                return value;
            }
            return expression;
        }
        return expression;
    }

    private Literal lookupVariableValue(String varName) {
        for (HashMap<String, Literal> scope : variableValues) {
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        return null;
    }
}
