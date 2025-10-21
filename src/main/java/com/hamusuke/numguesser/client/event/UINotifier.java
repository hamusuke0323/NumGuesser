package com.hamusuke.numguesser.client.event;

import com.hamusuke.numguesser.client.NumGuesser;
import com.hamusuke.numguesser.client.gui.component.panel.menu.ServerListPanel;
import com.hamusuke.numguesser.event.EventHandler;

public class UINotifier {
    private final NumGuesser client = NumGuesser.getInstance();

    @EventHandler
    public void onServerInfoChange(final ServerInfoChangeEvent event) {
        if (this.client.getPanel() instanceof ServerListPanel panel) {
            panel.onServerInfoChanged();
        }
    }
}
