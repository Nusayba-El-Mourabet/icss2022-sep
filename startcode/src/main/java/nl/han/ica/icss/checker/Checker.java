package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.LinkedList;
import java.util.HashMap;


public class Checker {

//    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;


    //hashmap elke keer als je de variable tegen komt bijv. ParWidth: = 10 px, slaat het de variable naam en de bijbehorende type
    // BIJV.: map.put(ParWidth, PIXEL) voor p {width: ParWidth)
    //voor iedere scope heb je een nieuwe hash map, bij ParWidth: = 28% p{width; 10px; ParWidth: = 5px, height: ParWidth; } p {} is hier een nieuwe scope
    // bij transformer heb je hetzelfde concept maar dan gaat het de waarde opslaan


    public Checker() {
        variableTypes = new LinkedList<>();
    }

    public void check(AST ast) {
        // variableTypes = new HANLinkedList<>();
        variableTypes.addFirst(new HashMap<>()); // Add global scope
        checkStylesheet(ast.root);
        variableTypes.removeFirst(); // Clean up after checking
    }

    private void checkStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
    }

    private void checkStylerule(Stylerule node) {
        variableTypes.addFirst(new HashMap<>()); // New scope for each Stylerule
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
        variableTypes.removeFirst(); // Remove scope after exiting Stylerule
    }

    private void checkDeclaration(Declaration node) {
        if (node.property.name.equals("width") || node.property.name.equals("height")) {
            if (!(node.expression instanceof PixelLiteral)) {
                node.setError("Property '" + node.property.name + "' expects a pixel value.");
            }
        }
        if (node.property.name.equals("color") || node.property.name.equals("background-color")) {
            if (!(node.expression instanceof ColorLiteral)) {
                node.setError("Property '" + node.property.name + "' expects a color value.");
            }
        }

    }
}