package com.hamusuke.numguesser.client.gui.component.panel.main.play;

import com.hamusuke.numguesser.client.game.SinglePlayerGame;
import com.hamusuke.numguesser.client.game.SinglePlayerGame.Difficulty;
import com.hamusuke.numguesser.client.gui.component.panel.Panel;
import com.hamusuke.numguesser.client.gui.component.panel.dialog.ConfirmPanel;
import com.hamusuke.numguesser.client.gui.component.panel.menu.MainMenuPanel;
import com.hamusuke.numguesser.client.gui.component.table.SinglePlayerGameTable;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SinglePlayerGamePanel extends Panel {
    private final SinglePlayerGame game;
    private SinglePlayerGameTable table;
    private JXButton putButton;
    @Nullable
    private JXPanel nextCard;
    private JXPanel commandPanel;
    private GameState gameState = GameState.PLAYING;

    public SinglePlayerGamePanel(Difficulty difficulty) {
        this.game = new SinglePlayerGame(difficulty);
    }

    @Override
    public void init() {
        super.init();

        this.commandPanel = new JXPanel();
        if (this.nextCard != null) {
            this.commandPanel.add(this.nextCard, BorderLayout.CENTER);
        }

        if (this.putButton == null) {
            this.putButton = new JXButton("ここに置く");
            this.putButton.setEnabled(false);
            this.putButton.addActionListener(e -> this.onPut());
        }

        if (this.table == null) {
            this.table = new SinglePlayerGameTable(this.game.getDifficulty()) {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    super.valueChanged(e);
                    updatePutButton();
                }

                @Override
                public void columnSelectionChanged(ListSelectionEvent e) {
                    super.columnSelectionChanged(e);
                    updatePutButton();
                }
            };

            this.initGame();
        }

        if (this.gameState == GameState.CLEARED) {
            this.gameCleared();
        } else if (this.gameState == GameState.GAME_OVER) {
            this.gameOver();
        }

        this.table.clearSelection();
        this.commandPanel.add(this.putButton, BorderLayout.SOUTH);

        var scroll = new JScrollPane(this.table);
        scroll.getViewport().addChangeListener(e -> SwingUtilities.invokeLater(this.table::repaint));
        this.add(scroll, BorderLayout.CENTER);
        this.add(this.commandPanel, BorderLayout.EAST);
    }

    private void initGame() {
        for (int i = 0; i < this.game.getDifficulty().firstOpenedCardNum; i++) {
            var card = this.game.nextCard();
            card.open();
            this.table.setCardTo(card, i, this.game.getDifficulty().centerIndex);
        }

        this.showNextCard();
    }

    private void updatePutButton() {
        SwingUtilities.invokeLater(() -> {
            var row = this.table.getSelectedRow();
            var col = this.table.getSelectedColumn();
            if (row < 0 || col < 0 || !this.table.isValidPos(row, col)) {
                this.putButton.setEnabled(false);
                return;
            }

            this.putButton.setEnabled(true);
        });
    }

    private void onPut() {
        var row = this.table.getSelectedRow();
        var col = this.table.getSelectedColumn();
        if (row < 0 || col < 0 || !this.table.isValidPos(row, col)) {
            return;
        }

        var card = this.game.nextCard();
        if (card == null) {
            return;
        }

        card.open();
        this.table.setCardTo(card, row, col);
        this.showNextCard();

        if (SinglePlayerGame.isPermutationValid(((DefaultTableModel) this.table.getModel()).getDataVector())) {
            if (this.game.getNextCard() == null && !this.gameState.isGameEnded()) {
                this.gameCleared();
            }

            return;
        }

        if (this.gameState.isGameEnded()) {
            return;
        }

        this.gameOver();
    }

    private void gameCleared() {
        this.add(new JXLabel("ゲームクリア！", SwingConstants.CENTER), BorderLayout.NORTH);
        this.putRestartButton();
        this.gameState = GameState.CLEARED;
    }

    private void gameOver() {
        this.add(new JXLabel("ゲームオーバー！", SwingConstants.CENTER), BorderLayout.NORTH);
        this.putRestartButton();
        this.gameState = GameState.GAME_OVER;
    }

    private void putRestartButton() {
        var restart = new JXButton("もう一度プレイする");
        restart.addActionListener(e -> this.client.setPanel(new SinglePlayerGamePanel(this.game.getDifficulty())));
        this.add(restart, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        });
    }

    private void showNextCard() {
        if (this.nextCard != null) {
            this.commandPanel.remove(this.nextCard);
        }

        if (this.game.getNextCard() == null) {
            return;
        }

        this.nextCard = this.game.getNextCard().toPanel();
        this.commandPanel.add(this.nextCard, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        });
    }

    @Nullable
    @Override
    public JMenuBar createMenuBar() {
        var bar = new JMenuBar();
        bar.add(this.createMenuMenu());
        bar.add(this.createThemeMenu());
        return bar;
    }

    @Override
    protected JMenu createMenuMenu() {
        var menu = new JMenu("メニュー");
        var item = new JMenuItem("やめる");
        item.addActionListener(e -> this.client.setPanel(new ConfirmPanel(this, "ゲームをやめますか", b -> {
        }) {
            @Override
            public void onClose() {
                if (this.accepted) {
                    SinglePlayerGamePanel.this.client.setPanel(new MainMenuPanel());
                } else {
                    SinglePlayerGamePanel.this.client.setPanel(SinglePlayerGamePanel.this);
                }
            }
        }));

        menu.add(item);
        return menu;
    }

    private enum GameState {
        PLAYING,
        CLEARED,
        GAME_OVER;

        private boolean isGameEnded() {
            return this == CLEARED || this == GAME_OVER;
        }
    }
}
