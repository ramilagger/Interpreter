import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by ramilagger on 14/10/17.
 */
public class Window {
    private JTextPane textPane1;
    public JPanel panel1;
    private JButton button1;
    private JButton button2;
    private JTextPane textPane2;
    public Style style;

    public Window() {
        style = textPane1.addStyle("style", null);
        button1.addActionListener(e -> {
            if(e.getSource() == button1) {
                String before = textPane1.getText();
                Lexer p = new Lexer(before);
                textPane1.setText("");
                StyledDocument doc = textPane1.getStyledDocument();
                try {
                    p.lexToString(style, doc);
                    JOptionPane.showConfirmDialog(textPane1,"Successfully lexed","Success",JOptionPane.DEFAULT_OPTION);

                }catch (Exception ex) {
                    JOptionPane.showConfirmDialog(textPane1,ex.getLocalizedMessage(),"Lexing error",JOptionPane.DEFAULT_OPTION);
                    textPane1.setText(before);
                }
            }
        });

        button2.addActionListener(e -> {
            if(e.getSource() == button2) {
                File temp = new File("ast.txt");
                try {
                    List<Method> a = new Parser(MainKt.removeComments(new Lexer(textPane1.getText()).lex())).parse();
                    try {
                        PrintWriter pw = new PrintWriter(new FileWriter(temp));
                        a.forEach(pw::println);
                        pw.close();
                        JFileChooser fileChooser = new JFileChooser();
                        StringBuilder sb = new StringBuilder();
                        a.forEach(i -> sb.append(i + "\n"));
                        textPane2.setText(sb.toString());
                        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            pw = new PrintWriter(new FileWriter(file));
                            a.forEach(pw::println);
                            // StringBuilder sb = new StringBuilder();
                            //a.forEach(i -> sb.append(i + "\n"));
                            pw.close();
                            //if(Desktop.isDesktopSupported())
                            //    Desktop.getDesktop().open(file);
                            // }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }catch (Exception a) {
                    textPane2.setText(a.getLocalizedMessage());
                }
                // Parser p = new Parser())
            }
        });
    }
}
