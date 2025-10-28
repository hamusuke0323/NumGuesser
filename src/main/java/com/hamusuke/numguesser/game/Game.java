package com.hamusuke.numguesser.game;

import com.hamusuke.numguesser.game.data.GameData;
import com.hamusuke.numguesser.game.data.GameDataHandlerRegistry;
import com.hamusuke.numguesser.game.data.GameDataSyncer;
import com.hamusuke.numguesser.game.phase.phases.AttackPhase;

import java.util.Collections;
import java.util.List;

public abstract class Game {
    public static final GameData<List<Integer>> SEATING_ARRANGEMENT = GameDataSyncer.registerData(GameDataHandlerRegistry.VAR_INT_LIST);
    public static final GameData<Integer> CURRENT_ATTACKER = GameDataSyncer.registerData(GameDataHandlerRegistry.VAR_INT);
    public static final GameData<Boolean> CANCELLABLE = GameDataSyncer.registerData(GameDataHandlerRegistry.BOOLEAN);
    public static final GameData<AttackPhase.SyncedAttackData> ATTACK_CARD = GameDataSyncer.registerData(GameDataHandlerRegistry.ATTACK_DATA);
    protected final GameDataSyncer dataSyncer = new GameDataSyncer();

    protected Game() {
        this.dataSyncer.define(SEATING_ARRANGEMENT, Collections.emptyList());
        this.dataSyncer.define(CURRENT_ATTACKER, -1);
        this.dataSyncer.define(CANCELLABLE, false);
        this.dataSyncer.define(ATTACK_CARD, AttackPhase.SyncedAttackData.DUMMY);
    }

    public void tick() {
    }
}
