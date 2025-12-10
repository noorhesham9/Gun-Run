import javax.media.opengl.*;
import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameGlListener implements GLEventListener, KeyListener, MouseListener {

    class Bullet {
        float x, y;
        boolean facingRight;
        boolean active;
        boolean isPlayer2;

        public Bullet(float x, float y, boolean facingRight, boolean isPlayer2) {
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
            this.active = true;
            this.isPlayer2 = isPlayer2;
        }
    }

    JFrame myFrame;
    boolean isPaused = false;
    boolean isMultiplayer = false;
    int timerSeconds = 0;
    int score = 0;
    long lastTime;
    boolean isGameOver = false;
    boolean isGameRunning = true;
    boolean isWin = false;
    String difficultyLevel;

    GLCanvas glCanvas;
    FPSAnimator animator;
    TextRenderer timerRenderer;
    TextRenderer textRenderer;
    TextRenderer menuRenderer;
    TextRenderer ScoreRenderer;

    Texture backgroundTexture;
    Texture pauseButtonTexture;
    Texture scoreBoardTexture;
    Texture timerBoardTexture;
    Texture gamePausedTexture;
    Texture continueTexture;
    Texture exitTexture;
    Texture winTexture;
    Texture loseTexture;
    Texture Enter;
    Texture To;
    Texture Exit;
    Texture bulletTexture;

    Texture muteOnTexture;
    Texture muteOffTexture;

    Texture[] numbersTextures = new Texture[10];
    Texture[] healthImages = new Texture[6];

    int playerHealth = 78;
    ArrayList<Texture> idleTextures = new ArrayList<>();
    ArrayList<Texture> walkingTextures = new ArrayList<>();
    ArrayList<Texture> shootingTextures = new ArrayList<>();
    ArrayList<Texture> jumpTextures = new ArrayList<>();
    float playerX = 10;
    float playerY = 15;
    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean isWalking = false;
    boolean isJumping = false;
    float verticalVelocity = 0;
    float gravity = 0.15f;
    float jumpStrength = 3.2f;
    boolean isShooting = false;
    boolean facingRight = true;
    long lastFrameTime = 0;
    int currentFrameIndex = 0;
    long shootingStartTime = 0;

    ArrayList<Texture> player2Textures = new ArrayList<>();
    float player2X = 30;
    float player2Y = 15;
    int player2Health = 2;
    boolean p2IsAlive = true;
    boolean p2LeftPressed = false;
    boolean p2RightPressed = false;
    boolean p2FacingRight = false;
    boolean p2IsWalking = false;
    boolean p2IsShooting = false;
    long p2LastFrameTime = 0;
    int p2FrameIndex = 0;
    long p2ShootingStartTime = 0;

    float groundLevel = 15;
    ArrayList<Bullet> bullets = new ArrayList<>();

    Rectangle continueBtnBounds = new Rectangle(35, 50, 30, 10);
    Rectangle exitBtnBounds = new Rectangle(35, 35, 30, 10);
    Rectangle gamePausedBounds = new Rectangle(25, 70, 50, 15);
    Rectangle pauseGameBtnBounds = new Rectangle(82, 88, 16, 8);

    Rectangle scoreBoardBounds = new Rectangle(2, 88, 20, 8);
    Rectangle timerBoardBounds = new Rectangle(40, 88, 20, 8);

    Rectangle muteBtnBounds = new Rectangle(5, 80, 10, 10);

    public GameGlListener(String difficulty, boolean isMultiplayer) {
        this.difficultyLevel = difficulty;
        this.isMultiplayer = isMultiplayer;

        if (difficulty.equals("Easy")) {
            timerSeconds = 60;
        } else if (difficulty.equals("Medium")) {
            timerSeconds = 30;
        } else if (difficulty.equals("Hard")) {
            timerSeconds = 15;
        }

        GLCapabilities capabilities = new GLCapabilities();
        glCanvas = new GLCanvas(capabilities);
        glCanvas.addGLEventListener(this);
        glCanvas.addKeyListener(this);
        glCanvas.addMouseListener(this);

        myFrame = new JFrame("Metal Slug - Game Mode (" + difficulty + ")");
        myFrame.setLayout(new BorderLayout());
        myFrame.add(glCanvas, BorderLayout.CENTER);
        myFrame.setSize(800, 600);
        myFrame.setLocationRelativeTo(null);
        myFrame.setResizable(false);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);

        animator = new FPSAnimator(glCanvas, 60);
        animator.start();

        lastTime = System.currentTimeMillis();
        glCanvas.requestFocus();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        try {
            backgroundTexture = TextureIO.newTexture(new File("Assets/Background.png"), true);
            pauseButtonTexture = TextureIO.newTexture(new File("Assets/Pauseboard (1).png"), true);
            scoreBoardTexture = TextureIO.newTexture(new File("Assets/scoreboard (1).png"), true);
            timerBoardTexture = TextureIO.newTexture(new File("Assets/timerboard (1).png"), true);
            gamePausedTexture = TextureIO.newTexture(new File("Assets/gamepausedboard (1).png"), true);
            continueTexture = TextureIO.newTexture(new File("Assets/continue (1).png"), true);
            exitTexture = TextureIO.newTexture(new File("Assets/exitboard (1).png"), true);
            winTexture = TextureIO.newTexture(new File("Assets/youwin.png"), true);
            loseTexture = TextureIO.newTexture(new File("Assets/youlose.png"), true);
            Enter = TextureIO.newTexture(new File("Assets/word enter.png"), true);
            To = TextureIO.newTexture(new File("Assets/word to.png"), true);
            Exit = TextureIO.newTexture(new File("Assets/exit.png"), true);

            muteOnTexture = TextureIO.newTexture(new File("Assets/MuteOff (1).png"), true);
            muteOffTexture = TextureIO.newTexture(new File("Assets/MuteOn (1).png"), true);

            for (int i = 0; i < 10; i++) {
                numbersTextures[i] = TextureIO.newTexture(new File("Assets/numbers board (" + i + ").png"), true);
            }
            for (int i = 0; i < 5; i++) {
                healthImages[i] = TextureIO.newTexture(new File("Assets/helthbar/" + i + ".png"), true);
            }

            File w1 = new File("Assets/playerWalking/15.png");
            File w2 = new File("Assets/playerWalking/13.png");
            File w3 = new File("Assets/playerWalking/14.png");
            if (w1.exists()) walkingTextures.add(TextureIO.newTexture(w1, true));
            if (w2.exists()) walkingTextures.add(TextureIO.newTexture(w2, true));
            if (w3.exists()) walkingTextures.add(TextureIO.newTexture(w3, true));

            int i = 1;
            while (true) {
                File f = new File("Assets/playerIdle/" + i + ".png");
                if (!f.exists()) break;
                idleTextures.add(TextureIO.newTexture(f, true));
                i++;
            }
            i = 1;
            while (true) {
                File f = new File("Assets/playerShooting/" + i + ".png");
                if (!f.exists()) break;
                shootingTextures.add(TextureIO.newTexture(f, true));
                i++;
            }
            i = 1;
            while (true) {
                File f = new File("Assets/PlayerJumpUp/" + i + ".png");
                if (!f.exists()) break;
                jumpTextures.add(TextureIO.newTexture(f, true));
                i++;
            }

            for (int k = 1; k <= 10; k++) {
                File p2File = new File("Assets/enemy2/Enemy2 " + k + " (1).png");
                if (p2File.exists()) {
                    player2Textures.add(TextureIO.newTexture(p2File, true));
                }
            }

            File bFile = new File("Assets/bullet.png");
            if (bFile.exists()) bulletTexture = TextureIO.newTexture(bFile, true);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        timerRenderer = new TextRenderer(new Font("Stencil", Font.BOLD, 40));
        menuRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 28));
        ScoreRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 40));
        textRenderer = new TextRenderer(new Font("Stencil", Font.BOLD, 30));
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        drawBackground(gl);

        if (isGameRunning) {
            drawPlayer1(gl);
            if (isMultiplayer && p2IsAlive) {
                drawPlayer2(gl);
            }
            drawHUD(gl, drawable);
            checkGameStatus();
            drawHealthBar(gl);
        } else {
            renderEndScreen(gl, drawable.getWidth(), drawable.getHeight());
        }
        if (isPaused) {
            drawPauseMenu(gl, drawable);
        } else {
            updateTimer();
        }
    }

    private void drawPlayer1(GL gl) {
        if (!isPaused && !isGameOver) {
            if (leftPressed) { playerX -= 0.8f; facingRight = false; }
            if (rightPressed) { playerX += 0.8f; facingRight = true; }

            playerX = Math.max(0, Math.min(playerX, 90));

            if (isJumping) {
                playerY += verticalVelocity;
                verticalVelocity -= gravity;
                if (playerY <= groundLevel) {
                    playerY = groundLevel;
                    isJumping = false;
                    verticalVelocity = 0;
                }
            }
            animateSprite(gl);
            updateBullets(gl);
        }
    }

    private void drawPlayer2(GL gl) {
        if (!isPaused && !isGameOver) {
            if (p2LeftPressed) { player2X -= 0.8f; p2FacingRight = false; }
            if (p2RightPressed) { player2X += 0.8f; p2FacingRight = true; }

            player2X = Math.max(0, Math.min(player2X, 90));

            if (!player2Textures.isEmpty()) {
                if (p2IsWalking) {
                    if (System.currentTimeMillis() - p2LastFrameTime > 75) {
                        p2FrameIndex++;
                        p2LastFrameTime = System.currentTimeMillis();
                    }
                    if (p2FrameIndex >= player2Textures.size()) {
                        p2FrameIndex = 0;
                    }
                } else {
                    p2FrameIndex = 0;
                }

                Texture p2Frame = player2Textures.get(p2FrameIndex);
                if (p2Frame != null) {
                    gl.glEnable(GL.GL_BLEND);
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                    p2Frame.enable();
                    p2Frame.bind();
                    gl.glColor3f(1, 1, 1);
                    gl.glBegin(GL.GL_QUADS);

                    if (p2FacingRight) {
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(player2X, player2Y + 15);
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(player2X + 10, player2Y + 15);
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(player2X + 10, player2Y);
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(player2X, player2Y);
                    } else {
                        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(player2X, player2Y + 15);
                        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(player2X + 10, player2Y + 15);
                        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(player2X + 10, player2Y);
                        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(player2X, player2Y);
                    }
                    gl.glEnd();
                    p2Frame.disable();
                }
            }
        }
    }

    private void animateSprite(GL gl) {
        ArrayList<Texture> currentAnim;
        if (isShooting) {
            currentAnim = shootingTextures;
            if (System.currentTimeMillis() - shootingStartTime > 300) isShooting = false;
        } else if (isJumping && !jumpTextures.isEmpty()) {
            currentAnim = jumpTextures;
        } else if (isWalking) {
            currentAnim = walkingTextures;
        } else {
            currentAnim = idleTextures;
        }

        if (currentAnim.isEmpty()) return;

        if (System.currentTimeMillis() - lastFrameTime > 75) {
            currentFrameIndex++;
            lastFrameTime = System.currentTimeMillis();
        }
        if (currentFrameIndex >= currentAnim.size()) currentFrameIndex = 0;

        Texture frame = currentAnim.get(currentFrameIndex);
        if (frame != null) {
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            frame.enable();
            frame.bind();
            gl.glColor3f(1, 1, 1);

            gl.glBegin(GL.GL_QUADS);
            if (facingRight) {
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(playerX, playerY + 15);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(playerX + 10, playerY + 15);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(playerX + 10, playerY);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(playerX, playerY);
            } else {
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(playerX, playerY + 15);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(playerX + 10, playerY + 15);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(playerX + 10, playerY);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(playerX, playerY);
            }
            gl.glEnd();
            frame.disable();
        }
    }

    private void updateBullets(GL gl) {
        if (bulletTexture == null) return;

        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            if (b.active) {
                if (b.facingRight) b.x += 2;
                else b.x -= 2;

                if (b.x < 0 || b.x > 100) b.active = false;

                bulletTexture.enable();
                bulletTexture.bind();
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(b.x, b.y + 3);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(b.x + 5, b.y + 3);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(b.x + 5, b.y);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(b.x, b.y);
                gl.glEnd();
                bulletTexture.disable();
            }
        }
    }

    private void drawBackground(GL gl) {
        if (backgroundTexture == null) return;
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0, 1, 0, 1, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glColor3f(1, 1, 1);
        backgroundTexture.enable();
        backgroundTexture.bind();
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(0.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(0.0f, 0.0f);
        gl.glEnd();
        backgroundTexture.disable();
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    private void drawHUD(GL gl, GLAutoDrawable drawable) {
        int width = drawable.getWidth();
        int height = drawable.getHeight();
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        if (scoreBoardTexture != null) {
            scoreBoardTexture.enable();
            scoreBoardTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(scoreBoardBounds.x, scoreBoardBounds.y + scoreBoardBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(scoreBoardBounds.x + scoreBoardBounds.width, scoreBoardBounds.y + scoreBoardBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(scoreBoardBounds.x + scoreBoardBounds.width, scoreBoardBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(scoreBoardBounds.x, scoreBoardBounds.y);
            gl.glEnd();
            scoreBoardTexture.disable();
        }
        drawNumber(gl, score, scoreBoardBounds.x + scoreBoardBounds.width + 1, scoreBoardBounds.y + 1, 4, 6, 3);

        if (timerBoardTexture != null) {
            timerBoardTexture.enable();
            timerBoardTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(timerBoardBounds.x, timerBoardBounds.y + timerBoardBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(timerBoardBounds.x + timerBoardBounds.width, timerBoardBounds.y + timerBoardBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(timerBoardBounds.x + timerBoardBounds.width, timerBoardBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(timerBoardBounds.x, timerBoardBounds.y);
            gl.glEnd();
            timerBoardTexture.disable();
        }
        drawNumber(gl, timerSeconds, timerBoardBounds.x + timerBoardBounds.width + 1, timerBoardBounds.y + 1, 4, 6, 2);

        if (!isPaused && pauseButtonTexture != null) {
            pauseButtonTexture.enable();
            pauseButtonTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(pauseGameBtnBounds.x, pauseGameBtnBounds.y + pauseGameBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(pauseGameBtnBounds.x + pauseGameBtnBounds.width, pauseGameBtnBounds.y + pauseGameBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(pauseGameBtnBounds.x + pauseGameBtnBounds.width, pauseGameBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(pauseGameBtnBounds.x, pauseGameBtnBounds.y);
            gl.glEnd();
            pauseButtonTexture.disable();
        }
    }

    private void drawHealthBar(GL gl) {
        if (healthImages != null) {
            int index = 0;
            if (playerHealth >= 80) index = 5;
            else if (playerHealth >= 60) index = 4;
            else if (playerHealth >= 40) index = 3;
            else if (playerHealth >= 20) index = 2;
            else if (playerHealth >= 5) index = 1;

            if (healthImages[index] != null) {
                float x = 2; float y = 82; float w = 30; float h = 8;
                healthImages[index].enable();
                healthImages[index].bind();
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(x, y + h);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(x + w, y + h);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(x + w, y);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(x, y);
                gl.glEnd();
                healthImages[index].disable();
            }
        }
    }

    private void drawNumber(GL gl, int number, int x, int y, int width, int height, int minDigits) {
        String numStr = String.format("%0" + minDigits + "d", number);
        for (int i = 0; i < numStr.length(); i++) {
            int digit = Character.getNumericValue(numStr.charAt(i));
            Texture tex = numbersTextures[digit];
            if (tex != null) {
                tex.enable();
                tex.bind();
                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(x + (i * width), y + height);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(x + (i * width) + width, y + height);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(x + (i * width) + width, y);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(x + (i * width), y);
                gl.glEnd();
                tex.disable();
            }
        }
    }

    private void drawMuteButton(GL gl) {
        Texture currentMuteTexture = Sound.isMuted() ? muteOffTexture : muteOnTexture;

        if (currentMuteTexture != null) {
            float x = muteBtnBounds.x;
            float y = muteBtnBounds.y;
            float w = muteBtnBounds.width;
            float h = muteBtnBounds.height;

            currentMuteTexture.enable();
            currentMuteTexture.bind();

            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(x, y + h);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(x + w, y + h);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(x + w, y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(x, y);
            gl.glEnd();
            currentMuteTexture.disable();
        }
    }

    private void drawPauseMenu(GL gl, GLAutoDrawable drawable) {
        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.7f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 0); gl.glVertex2f(100, 0); gl.glVertex2f(100, 100); gl.glVertex2f(0, 100);
        gl.glEnd();

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        if (gamePausedTexture != null) {
            gamePausedTexture.enable();
            gamePausedTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(gamePausedBounds.x, gamePausedBounds.y + gamePausedBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(gamePausedBounds.x + gamePausedBounds.width, gamePausedBounds.y + gamePausedBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(gamePausedBounds.x + gamePausedBounds.width, gamePausedBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(gamePausedBounds.x, gamePausedBounds.y);
            gl.glEnd();
            gamePausedTexture.disable();
        }
        if (continueTexture != null) {
            continueTexture.enable();
            continueTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(continueBtnBounds.x, continueBtnBounds.y + continueBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(continueBtnBounds.x + continueBtnBounds.width, continueBtnBounds.y + continueBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(continueBtnBounds.x + continueBtnBounds.width, continueBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(continueBtnBounds.x, continueBtnBounds.y);
            gl.glEnd();
            continueTexture.disable();
        }
        if (exitTexture != null) {
            exitTexture.enable();
            exitTexture.bind();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(exitBtnBounds.x, exitBtnBounds.y + exitBtnBounds.height);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(exitBtnBounds.x + exitBtnBounds.width, exitBtnBounds.y + exitBtnBounds.height);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(exitBtnBounds.x + exitBtnBounds.width, exitBtnBounds.y);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(exitBtnBounds.x, exitBtnBounds.y);
            gl.glEnd();
            exitTexture.disable();
        }
        drawMuteButton(gl);
    }

    private void renderEndScreen(GL gl, int width, int height) {
        Texture currentTex = isWin ? winTexture : loseTexture;
        if (currentTex != null) {
            gl.glMatrixMode(GL.GL_PROJECTION); gl.glPushMatrix(); gl.glLoadIdentity();
            gl.glOrtho(0, 1, 0, 1, -1, 1);
            gl.glMatrixMode(GL.GL_MODELVIEW); gl.glPushMatrix(); gl.glLoadIdentity();
            gl.glDisable(GL.GL_DEPTH_TEST); gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glColor3f(1, 1, 1);
            currentTex.enable(); currentTex.bind();
            gl.glScaled(0.5, 0.5, 1);
            gl.glTranslatef(0.5f, 0.75f, 0);
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(0.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(1.0f, 1.0f);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(1.0f, 0.0f);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(0.0f, 0.0f);
            gl.glEnd();
            currentTex.disable(); gl.glDisable(GL.GL_BLEND);
            gl.glPopMatrix(); gl.glMatrixMode(GL.GL_PROJECTION); gl.glPopMatrix();
            gl.glMatrixMode(GL.GL_MODELVIEW); gl.glEnable(GL.GL_DEPTH_TEST);
        }
        ScoreRenderer.beginRendering(width, height);
        menuRenderer.beginRendering(width, height);
        menuRenderer.setColor(Color.WHITE);
        menuRenderer.draw("Score: " + playerHealth, (width / 2) - 80, (height / 2) - 50);
        menuRenderer.setColor(Color.YELLOW);

        gl.glMatrixMode(GL.GL_MODELVIEW); gl.glPushMatrix(); gl.glLoadIdentity();
        float btnSize = 0.2f; float startX = 0.27f; float posY = 0.25f; float spacing = 0.12f;
        gl.glMatrixMode(GL.GL_PROJECTION); gl.glPushMatrix(); gl.glLoadIdentity();
        gl.glOrtho(0, 1, 0, 1, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glDisable(GL.GL_DEPTH_TEST); gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        Texture[] buttons = {Enter, To, Exit};
        for (int i = 0; i < buttons.length; i++) {
            Texture btnTex = buttons[i];
            if (btnTex == null) continue;
            float currentX = startX + (i * spacing);
            gl.glColor3f(1, 1, 1);
            btnTex.enable(); btnTex.bind();
            TextureCoords coords = btnTex.getImageTexCoords();
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(coords.left(), coords.bottom()); gl.glVertex2f(currentX, posY);
            gl.glTexCoord2f(coords.right(), coords.bottom()); gl.glVertex2f(currentX + btnSize, posY);
            gl.glTexCoord2f(coords.right(), coords.top()); gl.glVertex2f(currentX + btnSize, posY + btnSize);
            gl.glTexCoord2f(coords.left(), coords.top()); gl.glVertex2f(currentX, posY + btnSize);
            gl.glEnd();
            btnTex.disable();
        }
        gl.glDisable(GL.GL_BLEND);
        gl.glMatrixMode(GL.GL_MODELVIEW); gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION); gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        menuRenderer.endRendering();
        ScoreRenderer.endRendering();
    }

    private void checkGameStatus() {
        if (playerHealth <= 0) { isGameRunning = false; isWin = false; }
        if (timerSeconds <= 0) { isGameRunning = false; isWin = true; }
    }

    private void updateTimer() {
        if (!isGameOver && System.currentTimeMillis() - lastTime > 1000) {
            timerSeconds--;
            lastTime = System.currentTimeMillis();
            if (timerSeconds <= 0) isGameOver = true;
        }
    }

    @Override public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION); gl.glLoadIdentity();
        gl.glOrtho(0, 100, 0, 100, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    @Override public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (isPaused) {
            if (e.getKeyCode() == KeyEvent.VK_P) isPaused = !isPaused;
            return;
        }
        if (!isGameRunning) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                myFrame.dispose();
                new GameApp();
            }
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) { leftPressed = true; facingRight = false; isWalking = true; }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) { rightPressed = true; facingRight = true; isWalking = true; }
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (!isJumping) { isJumping = true; verticalVelocity = jumpStrength; currentFrameIndex = 0; }
        }
        else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            isShooting = true; shootingStartTime = System.currentTimeMillis(); currentFrameIndex = 0;
            float bulletStartX = facingRight ? playerX + 8 : playerX - 1;
            bullets.add(new Bullet(bulletStartX, playerY + 8, facingRight, false));
        }

        if (isMultiplayer && p2IsAlive) {
            if (e.getKeyCode() == KeyEvent.VK_A) { p2LeftPressed = true; p2FacingRight = false; p2IsWalking = true; }
            else if (e.getKeyCode() == KeyEvent.VK_D) { p2RightPressed = true; p2FacingRight = true; p2IsWalking = true; }
            else if (e.getKeyCode() == KeyEvent.VK_F) {
                p2IsShooting = true; p2ShootingStartTime = System.currentTimeMillis(); p2FrameIndex = 0;
                float bulletStartX = p2FacingRight ? player2X + 10 : player2X - 5;
                bullets.add(new Bullet(bulletStartX, player2Y + 8, p2FacingRight, true));
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_P) isPaused = !isPaused;
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (isPaused) { myFrame.dispose(); new GameApp(); } else isPaused = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
        isWalking = leftPressed || rightPressed;

        if (isMultiplayer) {
            if (e.getKeyCode() == KeyEvent.VK_A) p2LeftPressed = false;
            if (e.getKeyCode() == KeyEvent.VK_D) p2RightPressed = false;
            p2IsWalking = p2LeftPressed || p2RightPressed;
        }
    }

    @Override public void mousePressed(MouseEvent e) {
        double w = glCanvas.getWidth(); double h = glCanvas.getHeight();
        double mouseX = (e.getX() / w) * 100.0; double mouseY = ((h - e.getY()) / h) * 100.0;

        if (isPaused) {
            if (mouseX >= muteBtnBounds.x && mouseX <= muteBtnBounds.x + muteBtnBounds.width &&
                    mouseY >= muteBtnBounds.y && mouseY <= muteBtnBounds.y + muteBtnBounds.height) {

                Sound.toggleMute();
                glCanvas.repaint();
                return;
            }

            if (mouseX >= continueBtnBounds.x && mouseX <= continueBtnBounds.x + continueBtnBounds.width && mouseY >= continueBtnBounds.y && mouseY <= continueBtnBounds.y + continueBtnBounds.height) {
                isPaused = false;
            }
            else if (mouseX >= exitBtnBounds.x && mouseX <= exitBtnBounds.x + exitBtnBounds.width && mouseY >= exitBtnBounds.y && mouseY <= exitBtnBounds.y + exitBtnBounds.height) {
                myFrame.dispose();
                if (animator != null) animator.stop();
                new GameApp();
            }
        } else {
            if (mouseX >= pauseGameBtnBounds.x && mouseX <= pauseGameBtnBounds.x + pauseGameBtnBounds.width && mouseY >= pauseGameBtnBounds.y && mouseY <= pauseGameBtnBounds.y + pauseGameBtnBounds.height) isPaused = true;
        }
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}