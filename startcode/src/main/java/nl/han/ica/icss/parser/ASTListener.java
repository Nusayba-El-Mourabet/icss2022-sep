package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
//    private IHANStack<ASTNode> currentContainer;
    private Stack<ASTNode> currentContainer;


    public ASTListener() {
        ast = new AST();
        //currentContainer = new HANStack<>();
        currentContainer = new Stack<>();

    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        currentContainer.push(stylesheet); //dit voegt een node aan de stack
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet sheet = (Stylesheet) currentContainer.pop();
        ast.root = sheet;
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule rule = new Stylerule();
        currentContainer.push(rule); //dit voegt een node aan de stack
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule rule = (Stylerule) currentContainer.pop();
        currentContainer.peek().addChild(rule);
    }

    @Override
    public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
        TagSelector selector = new TagSelector(ctx.getText());
        currentContainer.push(selector);
    }

    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        TagSelector selector = (TagSelector) currentContainer.pop();
        currentContainer.peek().addChild(selector);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration();
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = (Declaration) currentContainer.pop();
        currentContainer.peek().addChild(declaration);
    }

    @Override
    public void enterProperty(ICSSParser.PropertyContext ctx) {
        PropertyName property = new PropertyName(ctx.getText());
        currentContainer.push(property);
    }

    @Override
    public void exitProperty(ICSSParser.PropertyContext ctx) {
        PropertyName property = (PropertyName) currentContainer.pop();
        currentContainer.peek().addChild(property);
    }


    @Override
    public void enterPixelSize(ICSSParser.PixelSizeContext ctx) {
        PixelLiteral pixelLiteral = new PixelLiteral(Integer.parseInt(ctx.getText().replace("px", "")));
        currentContainer.push(pixelLiteral);
    }

    @Override
    public void exitPixelSize(ICSSParser.PixelSizeContext ctx) {
        PixelLiteral pixelLiteral = (PixelLiteral) currentContainer.pop();
        currentContainer.peek().addChild(pixelLiteral);
    }

    @Override
    public void enterTrue(ICSSParser.TrueContext ctx) {
        BoolLiteral trueLiteral = new BoolLiteral(true);
        currentContainer.push(trueLiteral);
    }

    @Override
    public void exitTrue(ICSSParser.TrueContext ctx) {
        BoolLiteral trueLiteral = (BoolLiteral) currentContainer.pop();
        currentContainer.peek().addChild(trueLiteral);
    }

    @Override
    public void enterFalse(ICSSParser.FalseContext ctx) {
        BoolLiteral falseLiteral = new BoolLiteral(false);
        currentContainer.push(falseLiteral);
    }

    @Override
    public void exitFalse(ICSSParser.FalseContext ctx) {
        BoolLiteral falseLiteral = (BoolLiteral) currentContainer.pop();
        currentContainer.peek().addChild(falseLiteral);
    }

    @Override
    public void enterPercentage(ICSSParser.PercentageContext ctx) {
        int percentageValue = Integer.parseInt(ctx.getText().replace("%", ""));
        PercentageLiteral percentageLiteral = new PercentageLiteral(percentageValue);
        currentContainer.push(percentageLiteral);
    }

    @Override
    public void exitPercentage(ICSSParser.PercentageContext ctx) {
        PercentageLiteral percentageLiteral = (PercentageLiteral) currentContainer.pop();
        currentContainer.peek().addChild(percentageLiteral);
    }

    @Override
    public void enterScalar(ICSSParser.ScalarContext ctx) {
        int scalarValue = Integer.parseInt(ctx.getText());
        ScalarLiteral scalarLiteral = new ScalarLiteral(scalarValue);
        currentContainer.push(scalarLiteral);
    }

    @Override
    public void exitScalar(ICSSParser.ScalarContext ctx) {
        ScalarLiteral scalarLiteral = (ScalarLiteral) currentContainer.pop();
        currentContainer.peek().addChild(scalarLiteral);
    }

    @Override
    public void enterColor(ICSSParser.ColorContext ctx) {
        String colorCode = ctx.getText();
        ColorLiteral colorLiteral = new ColorLiteral(colorCode);
        currentContainer.push(colorLiteral);
    }

    @Override
    public void exitColor(ICSSParser.ColorContext ctx) {
        ColorLiteral colorLiteral = (ColorLiteral) currentContainer.pop();
        currentContainer.peek().addChild(colorLiteral);
    }
    @Override
    public void enterVariableassignment(ICSSParser.VariableassignmentContext ctx) {
        VariableAssignment variableAssignment = new VariableAssignment();
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariableassignment(ICSSParser.VariableassignmentContext ctx) {
        VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
        currentContainer.peek().addChild(variableAssignment);
    }
    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference variableReference = new VariableReference(ctx.getText());
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference variableReference = (VariableReference) currentContainer.pop();
        currentContainer.peek().addChild(variableReference);
    }



}