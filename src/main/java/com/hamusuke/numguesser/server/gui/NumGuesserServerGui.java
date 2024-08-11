package com.hamusuke.numguesser.server.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.google.common.collect.Lists;
import com.hamusuke.numguesser.logging.LogQueues;
import com.hamusuke.numguesser.server.NumGuesserServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTextArea;
import org.jdesktop.swingx.JXTextField;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class NumGuesserServerGui extends JXPanel {
    private static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, 12);
    private static final Logger LOGGER = LogManager.getLogger();
    final AtomicBoolean isClosing = new AtomicBoolean();
    private final NumGuesserServer server;
    private final Collection<Runnable> finalizers = Lists.newArrayList();
    private final CountDownLatch latch = new CountDownLatch(1);
    private Thread logAppenderThread;

    private NumGuesserServerGui(NumGuesserServer server) {
        this.server = server;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try {
            this.add(this.buildChatPanel(), BorderLayout.CENTER);
            this.add(this.buildInfoPanel(), BorderLayout.WEST);
        } catch (Exception e) {
            LOGGER.error("Couldn't build server GUI", e);
        }
    }

    public static NumGuesserServerGui showGuiFor(NumGuesserServer server) {
        FlatDarkLaf.setup();

        var jxFrame = new JXFrame("NumGuesser Server");
        var gui = new NumGuesserServerGui(server);
        jxFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jxFrame.add(gui);
        jxFrame.pack();
        jxFrame.setLocationRelativeTo(null);
        jxFrame.setVisible(true);
        jxFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!gui.isClosing.getAndSet(true)) {
                    jxFrame.setTitle("NumGuesser server - shutting down!");
                    server.stop(true);
                    gui.runFinalizers();
                }
            }
        });
        Objects.requireNonNull(jxFrame);
        gui.addFinalizer(jxFrame::dispose);
        gui.start();
        return gui;
    }

    public void addFinalizer(Runnable runnable) {
        this.finalizers.add(runnable);
    }

    private JComponent buildInfoPanel() {
        var jxPanel = new JXPanel(new BorderLayout());
        jxPanel.add(this.buildPlayerPanel(), BorderLayout.CENTER);
        jxPanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jxPanel;
    }

    private JComponent buildPlayerPanel() {
        var list = new PlayerList(this.server);
        var jscrollpane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jscrollpane;
    }

    private JComponent buildChatPanel() {
        var jxPanel = new JXPanel(new BorderLayout());
        var jxTextArea = new JXTextArea();
        var jscrollpane = new JScrollPane(jxTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jxTextArea.setEditable(false);
        jxTextArea.setFont(MONOSPACED);
        var jxTextField = new JXTextField();
        jxTextField.addActionListener(e -> {
            var s = jxTextField.getText().trim();
            if (!s.isEmpty()) {
                if (s.startsWith("/")) {
                    server.enqueueCommand(s.substring(1));
                } else {
                    server.sendMessageToAll(s);
                }
            }

            jxTextField.setText("");
        });
        jxPanel.add(jscrollpane, BorderLayout.CENTER);
        jxPanel.add(jxTextField, BorderLayout.SOUTH);
        jxPanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        this.logAppenderThread = new Thread(() -> {
            String s;
            while ((s = LogQueues.getNextLogEvent("ServerGuiConsole")) != null) {
                this.print(jxTextArea, jscrollpane, s);
            }
        });
        this.logAppenderThread.setUncaughtExceptionHandler((t, e) -> LOGGER.error("Caught exception", e));
        this.logAppenderThread.setDaemon(true);
        return jxPanel;
    }

    public void start() {
        this.logAppenderThread.start();
        this.latch.countDown();
    }

    public void close() {
        if (!this.isClosing.getAndSet(true)) {
            this.runFinalizers();
        }
    }

    void runFinalizers() {
        this.finalizers.forEach(Runnable::run);
    }

    public void print(JXTextArea area, JScrollPane pane, String s) {
        try {
            this.latch.await();
        } catch (InterruptedException ignored) {
        }

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> this.print(area, pane, s));
        } else {
            var document = area.getDocument();
            var jscrollbar = pane.getVerticalScrollBar();
            boolean flag = false;
            if (pane.getViewport().getView() == area) {
                flag = (double) jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double) (MONOSPACED.getSize() * 4) > (double) jscrollbar.getMaximum();
            }

            try {
                document.insertString(document.getLength(), s, null);
            } catch (BadLocationException ignored) {
            }

            if (flag) {
                jscrollbar.setValue(Integer.MAX_VALUE);
            }
        }
    }
}
