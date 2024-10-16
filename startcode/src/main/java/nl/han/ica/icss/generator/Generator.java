package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;

public class Generator {

	public String generate(AST ast) {
        return generateStylesheet((Stylesheet)ast.root);


	}

    private String generateStylesheet(Stylesheet root) {
        return generateStylerule((Stylerule)root.getChildren().get(0));
    }

    private String generateStylerule(Stylerule stylerule) {
        //class Stylerule heeft een selectors en een body
        String result = stylerule.selectors.get(0) + "{\n";
        result += "\t" + generateDeclaration(stylerule.body.get(0));
        result += "\n}";

        return result;
    }

    private String generateDeclaration(ASTNode astNode) {
        return "Declaration";
    }


}
