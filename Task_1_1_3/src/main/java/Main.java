import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String expr = "(((1+(7*x))*y)-((52+(7*x))*x))";
        System.out.println(expr);
        System.out.println(Expression.parse(expr).diff("x").simplify());
        // System.out.println(Expression.parse(expr).substitute("x", new Number(67)).simplify());
        System.out.println(Expression.parse(expr).diff("x").eval("x = 67; y = 1"));
    }
}