package xin.galois.lang;

import org.junit.Test;

public class ParserTest {

    String code =
            "(fn render () <LinearLayout\n" +
                    "            [attrs\n" +
                    "                :width (dp 20)\n" +
                    "                :height (dp 30)\n" +
                    "                :visible (gt 0 (.size fans))]\n" +
                    "            <TextView\n" +
                    "                [attrs\n" +
                    "                    :text \"Hello world!\"\n" +
                    "                    :textSize (sp 12)\n" +
                    "                    :textColor (colorRes 'R.color.text_color_dark')]\n" +
                    "                    >\n" +
                    "            <TextView\n" +
                    "                [attrs\n" +
                    "                    :text \"little text\"\n" +
                    "                    :textSize (sp 8)\n" +
                    "                    :textColor (.parse Color '#FFddFF34')]>\n" +
                    "            <ImageView>>)";

    @Test
    public void test_parse_code() throws Exception {
        final AstNode ast = new Parser(code).parse();
        System.out.println("ast = " + ast);
    }
}