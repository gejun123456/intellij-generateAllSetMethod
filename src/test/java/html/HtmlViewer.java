package html;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlViewer {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new HtmlViewer().show();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void show() throws Exception {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        JScrollPane scrollPane = new JBScrollPane(jEditorPane);
        HTMLEditorKit kit = new HTMLEditorKit();
        jEditorPane.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body { color:#000; margin: 4px; font-size: 10px; }");

        String xml = new String(Files.readAllBytes(Paths.get("src/main/resources/META-INF/plugin.xml")));
        Pattern pattern = Pattern.compile("<description><!\\[CDATA\\[(?<html>.*)]]></description>",
                Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);
        String html = matcher.find() ? matcher.group("html") : "fail";

        Document document = kit.createDefaultDocument();
        jEditorPane.setDocument(document);
        jEditorPane.setText(html);

        JFrame frame = new JFrame("Html Viewer");
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(400, 600));
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
