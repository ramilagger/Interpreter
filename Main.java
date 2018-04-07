import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class Main {

    private JFrame frame;
    private JTextPane editorCode;
    private JTextPane editorConsole;
    private Lexer lexer;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Main window = new Main();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public Main() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 50, 900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnfile = new JMenu("File");
        menuBar.add(mnfile);

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.exit(0);
            }
        });
        mnfile.add(mntmExit);

        JToolBar toolBar = new JToolBar();
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

        JButton btnRun = new JButton("Execute");
        ImageIcon runIcon = new ImageIcon(
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("resources/execute.png"))
                        .getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        btnRun.setIcon(runIcon);
        toolBar.add(btnRun);


        btnRun.addActionListener(e -> {
            try {
                try {
                    MainKt.setPw(new PrintWriter(new FileWriter("out.txt")));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Document doc = editorCode.getDocument();
                List<Method> methods = null;
                try {
                    methods = new Parser(MainKt.removeComments(new Lexer(doc.getText(0, doc.getLength())).lex())).parse();
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                Method m = methods.get(methods.size() - 1);
                List<Statement> list = m.getStatements();
                Scope scope = new Scope();
                for (Statement s : list) {
                    StatementsKt.eval(s, scope);
                    System.err.println(s);
                }
                MainKt.getPw().flush();

                Scanner sc = null;
                try {
                    sc = new Scanner(new File("out.txt"));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                StringBuilder sb = new StringBuilder();
                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine() + "\n");
                }
                editorConsole.setText(sb.toString());
            } catch (Exception ex) {
                editorConsole.setText(ex.getLocalizedMessage());
            }

        });
        JSplitPane splitPane = new JSplitPane();
        splitPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        splitPane.setOneTouchExpandable(true);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        editorCode = new JTextPane();
        editorCode.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                highlight(e);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                highlight(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        editorCode.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        editorCode.setMinimumSize(new Dimension(0, 500));
        editorCode.setText(MainKt.getProgram());
        splitPane.setLeftComponent(editorCode);

        editorConsole = new JTextPane();
        editorConsole.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        splitPane.setRightComponent(editorConsole);
    }

    protected void highlight(DocumentEvent e) {
        Runnable doHighlight = () -> {
            try {
                Document doc = e.getDocument();

                String code = doc.getText(0, doc.getLength());

                lexer = new Lexer(code);

                HashMap<String, Color> tokenColors = new HashMap<>();
                //tokenColors.put(Lexer.TokenType.FLOAT.name(), Color.RED);
                //tokenColors.put(Lexer.TokenType.NUMBER.name(), Color.GREEN);

                HashMap<String, Style> tokenStyles = new HashMap<>();
                for (String key : tokenColors.keySet()) {
                    Color color = tokenColors.get(key);
                    Style style = editorCode.addStyle(key, null);
                    StyleConstants.setForeground(style, color);
                    tokenStyles.put(key, style);
                }

                Style defaultStyle = editorCode.addStyle("default", null);
                StyleConstants.setForeground(defaultStyle, Color.BLACK);

                Style style = editorCode.addStyle("style", null);

                //System.err.println("here");
                // now iterate through tokens and try to highlight them
                // see the example below


                //editorCode.setText("");
                int position = editorCode.getCaretPosition();
                StyledDocument dst = lexer.lexToString(editorCode.getStyle("style"), new DefaultStyledDocument());
                editorCode.setStyledDocument(dst);
                editorCode.setCaretPosition(position);
                editorCode.getStyledDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        highlight(e);
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        highlight(e);
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {

                    }
                });
                //editorCode.setDocument(dst);

            } catch (BadLocationException e1) {
                // exception
            }
        };
        SwingUtilities.invokeLater(doHighlight);
    }
}