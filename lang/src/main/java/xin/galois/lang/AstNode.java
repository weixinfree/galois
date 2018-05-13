package xin.galois.lang;

import java.util.ArrayList;
import java.util.List;

/**
 * Ast
 * Created by wangwei on 2018/5/11.
 */
@SuppressWarnings("WeakerAccess")
public class AstNode {

    public final Type type;
    public final String rawStr;
    public final List<AstNode> children;

    public AstNode(Type type, String rawStr) {
        this.type = type;
        this.rawStr = rawStr;
        this.children = new ArrayList<>();
    }

    public void addChild(AstNode node) {
        children.add(node);
    }

    private void toStr(StringBuilder sb, int level) {
        sb.append("\n");
        pad(sb, level);
        sb.append(type);

        if (this.type == Type.ATOM) {
            sb.append(", rawStr='").append(rawStr).append("'");
        }

        for (AstNode child : children) {
            child.toStr(sb, level + 1);
        }
        if (type == Type.SEXPR) {
            sb.append("\n");
            pad(sb, level);
            sb.append("END");
        }
    }

    private static void pad(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("    ");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStr(sb, 0);

        return sb.toString();
    }

    enum Type {
        ATOM,
        SEXPR
    }
}
