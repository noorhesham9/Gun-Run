import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;

public class GameApp {

    public static void main(String[] args) {
        new GameApp();
    }

    public GameApp(){
        JFrame frame = new JFrame("Gun Run - Main Menu");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // تحميل صورة الخلفية للقائمة
        Image bgImage = null;
        try {
            bgImage = ImageIO.read(new File("Assets/background1.png"));
        } catch (Exception e) {
            System.out.println("Image not found! Check file path.");
        }
        final Image finalBgImage = bgImage;

        // بانل الرسم للخلفية
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) {
                    g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // إنشاء الأزرار
        JButton btnStart = createStyledButton("Start Game");
        JButton btnScoreboard = createStyledButton("Scoreboard");
        JButton btnInstructions = createStyledButton("Instructions");
        JButton btnExit = createStyledButton("Exit");

        // --- الأكشنز (التفاعلات) ---

        // 1. زر Start (مجرد طباعة حالياً عشان ملخبطش الفريق)
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start Game Clicked! (Game Loop not integrated yet)");
                JOptionPane.showMessageDialog(frame, "Game Loop is coming soon!");
            }
        });

        // 2. زر Instructions (التاسك بتاعتك - شغالة)
        btnInstructions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "Game Instructions:\n\n" +
                                "1. Use Arrow Keys to Move.\n" +
                                "2. Press 'Space' to Shoot.\n" +
                                "3. Avoid enemies and obstacles.\n" +
                                "4. Survive as long as possible!\n\n" +
                                "Good Luck, Soldier!",
                        "How to Play",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 3. زر Exit
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // إضافة العناصر للشاشة
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnStart);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnScoreboard);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnInstructions);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnExit);
        menuPanel.add(Box.createVerticalGlue());

        frame.add(menuPanel);
        frame.setVisible(true);
    }

    // دالة تنسيق الأزرار
    private static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setMaximumSize(new Dimension(250, 60));
        btn.setBackground(Color.DARK_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }
}