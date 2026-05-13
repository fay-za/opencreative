/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.mcchickenstudio.opencreative.OpenCreative;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This Main class is called when user executes .jar file.
 * It's not called when plugin loads on PaperMC server.
 * @see OpenCreative
 */
public class Main {

    private static String buildVersion = "";

    /**
     * Creates window with installation instruction when user executes this .jar file.
     * @param args Arguments to launch Java application
     */
    public static void main(String[] args) {
        Toolkit.getDefaultToolkit().beep();
        buildVersion = getBuildVersion();
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println(getSimpleMessage());
            for (Hyperlink hyperlink : Hyperlink.values()) {
                System.out.println(hyperlink.text + ": " + hyperlink.url);
            }
            try {
                Thread.sleep(5000);
            } catch (Exception ignored) {}
        } else {
            JFrame frame = createWindow();
            frame.setVisible(true);
        }

    }

    /**
     * Returns OpenCreative+ frame window to display on screen.
     * @return frame of OpenCreative+.
     */
    private static @NotNull JFrame createWindow() {
        JFrame frame = new JFrame("OpenCreative+" + buildVersion);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        double heightFactor = (screen.height <= 800) ? 0.49 : 0.3;

        int width = (int) (screen.width * 0.6);
        int height = (int) (screen.height * heightFactor);

        frame.setSize(Math.max(width, 400), Math.max(height, 300));

        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        frame.setLayout(new BorderLayout());
        frame.add(createContentPanel(), BorderLayout.CENTER);
        frame.add(createButtonsPanel(frame), BorderLayout.SOUTH);

        return frame;
    }

    /**
     * Returns content panel with logo and text.
     * @return content panel.
     */
    private static @NotNull JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        String formattedMessage = getMessage();

        JLabel label = new JLabel(formattedMessage);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, scale(17f)));
        label.setForeground(Color.WHITE);
        label.setAlignmentY(Component.TOP_ALIGNMENT);

        contentPanel.setBackground(Color.darkGray);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
                scale(30), scale(40), scale(30), scale(40)));

        URL iconURL = Main.class.getResource("/logo.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            int iconSize = Math.round(scale(128f));
            Image scaledImage = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setAlignmentY(Component.TOP_ALIGNMENT);
            imageLabel.setBorder(BorderFactory.createEmptyBorder(scale(10), 0, 0, scale(40)));
            contentPanel.add(imageLabel);
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    label.setText("<html>Well, it's possible!</html>");
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    label.setText(formattedMessage);
                }
            });
        }

        contentPanel.add(label);
        return contentPanel;
    }

    /**
     * Returns panel, that contains hyperlink buttons.
     * @param frame instance of frame.
     * @return buttons panel.
     */
    private static @NotNull JPanel createButtonsPanel(@NotNull Frame frame) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(scale(10), 0, scale(20), 0));
        buttonPanel.setBackground(Color.BLACK);

        for (Hyperlink link : Hyperlink.values()) {
            JButton button = createHyperlinkButton(link.text, link.url, link.color);
            buttonPanel.add(button);
        }

        JButton okButton = createButton("Close");
        okButton.setBackground(new Color(255, 83, 83));
        okButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(okButton);

        return buttonPanel;
    }

    /**
     * Creates button with default style.
     * @param text text on button.
     * @return styled button with text.
     */
    private static @NotNull JButton createButton(@NotNull String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(47, 181, 235));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14).deriveFont(scale(14f)));
        return button;
    }

    /**
     * Creates styled button, that opens hyperlink on clicking it.
     * @param text text on button.
     * @param URL link of website to open.
     * @param color color of button.
     * @return hyperlink button.
     */
    private static @NotNull JButton createHyperlinkButton(@NotNull String text, @NotNull String URL, @Nullable Color color) {
        JButton button = createButton(text);
        if (color != null) button.setBackground(color);
        button.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(URL));
            } catch (Exception ignored) {
                System.out.println(text + ": " + URL);
            }
        });
        return button;
    }

    /**
     * Returns build version with space at the beginning, or empty string.
     * @return build version " Some version", or ""
     */
    private static @NotNull String getBuildVersion() {
        try (InputStream stream = Main.class.getResourceAsStream("/plugin.yml")) {
            if (stream == null) return "";
            InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);

            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("version:")) {
                    int colonIndex = line.indexOf(':');
                    if (colonIndex != -1) {
                        String versionValue = line.substring(colonIndex + 1).trim();

                        if (versionValue.startsWith("'") && versionValue.endsWith("'")) {
                            versionValue = versionValue.substring(1, versionValue.length() - 1);
                        }

                        int limit = 25;
                        if (versionValue.length() > limit) {
                            versionValue = versionValue.substring(0, limit-3) + "...";
                        }

                        return " " + versionValue;
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * Returns message, that will be displayed in window.
     * @return message for displaying in window.
     */
    private static String getMessage() {
        String message = """
                This is Open<b>Creative</b><span style='color: #2fb5ebff;'>+</span>""" + buildVersion + """
                , a plugin for Minecraft servers.
                Made by <span style='color: #fa5061;'>McChicken Studio 2017-2026</span>.
                
                Installation:
                To install plugin please download PaperMC server, then load it and accept EULA.
                After that put this .jar file into server's /plugins/ folder and launch a server.
                """;
        return "<html>" + message.replaceAll("\n", "<br>") + "</html>";
    }

    /**
     * Returns simple message, that will be displayed, if system can't open window.
     * @return message for displaying in terminal.
     */
    private static String getSimpleMessage() {
        return """
                This is OpenCreative+""" + buildVersion + """
                , a plugin for Minecraft servers.
                Made by McChicken Studio 2017-2026.
                
                Installation:
                To install plugin please download PaperMC server, then load it and accept EULA.
                After that put this .jar file into server's /plugins/ folder and launch a server.
                """;
    }

    /**
     * Scales size of font for screen resolution.
     * @param size size of font.
     * @return scaled size.
     */
    private static float scale(float size) {
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        return size * dpi / 96f;
    }

    /**
     * Scales size of font for screen resolution.
     * @param size size of font.
     * @return scaled size.
     */
    private static int scale(int size) {
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        return (int) (size * dpi / 96f);
    }

    enum Hyperlink {

        PAPER("Download PaperMC", "https://papermc.io/", new Color(55, 129, 47)),
        WIKI("Visit Wiki", "https://gitlab.com/eagles-creative/opencreative/-/wikis/home"),
        MODRINTH("Modrinth", "https://modrinth.com/plugin/opencreative"),
        CREATIVE("About Creative+", "https://creative-plus-en.fandom.com/wiki/About_Coding-in-Blocks"),
        DISCORD("Discord Support", "https://discord.com/invite/sSFCXUeq63", new Color(107, 70, 255));

        private final String text;
        private final String url;
        private final Color color;

        Hyperlink(@NotNull String text, @NotNull String URL) {
            this(text, URL, null);
        }

        Hyperlink(@NotNull String text, @NotNull String URL, @Nullable Color color) {
            this.text = text;
            this.url = URL;
            this.color = color;
        }
    }

}
