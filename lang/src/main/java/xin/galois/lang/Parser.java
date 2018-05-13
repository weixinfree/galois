package xin.galois.lang;

import static java.lang.Character.isWhitespace;

/**
 * parse s表达式到ast
 * Created by wangwei on 2018/5/11.
 */

@SuppressWarnings("WeakerAccess")
public class Parser {

    private String se;
    private int index;

    public Parser(String se) {
        this.se = se;
        this.index = 0;
    }

    public synchronized AstNode parse() {

        AstNode result;
        if (isSExprStartMark(peek())) {
            result = read_sexpr();
        } else {
            result = read_word();
        }

        if (index != se.length()) {
            throw new ParseException("finish parse, but not cost all characters");
        }

        return result;
    }

    private boolean isSExprStartMark(char c) {
        return c == '<' || c == '(' || c == '[' || c == '{';
    }

    private boolean isSExprEndMark(char c) {
        return c == '>' || c == ')' || c == ']' || c == '}';
    }

    private char peek() {
        return se.charAt(index);
    }

    private void consume(char c) {
        assertTrue(se.charAt(index) == c);
        index++;
    }

    private void consume_whitespace() {
        while (index < se.length() && isWhitespace(peek())) {
            index++;
        }
    }

    private boolean is_str_mark(char c) {
        return c == '\'' || c == '"' || c == '`';
    }

    private AstNode read_str() {
        final char strMark = peek();
        assertTrue(is_str_mark(strMark));

        int start = this.index;

        consume(strMark);
        char last = ' ';
        while (!is_str_mark(peek()) && (is_str_mark(peek()) && last != '\\')) {
            last = se.charAt(this.index);
            this.index++;
        }

        consume(strMark);

        return new AstNode(AstNode.Type.ATOM, se.substring(start, index));
    }

    private AstNode read_word() {

        if (is_str_mark(peek())) {
            return read_str();
        }

        final int start = this.index;
        while (!isEnd(peek())) {
            this.index++;
        }

        final String word = se.substring(start, index);
        return new AstNode(AstNode.Type.ATOM, word);
    }

    private boolean isEnd(char c) {
        return isWhitespace(c) || isSExprEndMark(c);
    }

    private AstNode read_sexpr() {
        assertTrue(isSExprStartMark(peek()));

        final char startMark = peek();
        consume(startMark);

        final AstNode astNode = new AstNode(AstNode.Type.SEXPR, null);

        while (!isSExprEndMark(peek())) {
            if (isSExprStartMark(peek())) {
                astNode.addChild(read_sexpr());
                consume_whitespace();
            } else {
                astNode.addChild(read_word());
                consume_whitespace();
            }
        }
        consume(getSExprEndMark(startMark));

        return astNode;
    }

    private char getSExprEndMark(char c) {
        switch (c) {

            case '(':
                return ')';
            case '<':
                return '>';
            case '[':
                return ']';
            case '{':
                return '}';
            default:
                throw new AssertionError("impossible");
        }
    }

    private static void assertTrue(boolean expr) {
        if (!expr) {
            throw new ParseException("internal error");
        }
    }
}
