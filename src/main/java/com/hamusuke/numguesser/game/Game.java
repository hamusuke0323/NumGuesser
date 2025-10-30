package com.hamusuke.numguesser.game;

import com.hamusuke.numguesser.game.data.GameData;
import com.hamusuke.numguesser.game.data.GameDataHandlerRegistry;
import com.hamusuke.numguesser.game.data.GameDataSyncer;
import com.hamusuke.numguesser.game.pair.PlayerPair;
import com.hamusuke.numguesser.game.phase.phases.AttackPhase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Game {
    public static final GameData<List<Integer>> SEATING_ARRANGEMENT = GameDataSyncer.registerData(GameDataHandlerRegistry.VAR_INT_LIST);
    public static final GameData<Integer> CURRENT_ATTACKER = GameDataSyncer.registerData(GameDataHandlerRegistry.VAR_INT);
    public static final GameData<Boolean> CANCELLABLE = GameDataSyncer.registerData(GameDataHandlerRegistry.BOOLEAN);
    public static final GameData<AttackPhase.SyncedAttackData> ATTACK_CARD = GameDataSyncer.registerData(GameDataHandlerRegistry.ATTACK_DATA);
    public static final GameData<Boolean> LAST_ROUND = GameDataSyncer.registerData(GameDataHandlerRegistry.BOOLEAN);
    public static final GameData<Integer> CURRENT_TOSSER = GameDataSyncer.registerData(GameDataHandlerRegistry.VAR_INT);
    public static final GameData<Map<Integer, PlayerPair.PairColor>> PAIR_MAP = GameDataSyncer.registerData(GameDataHandlerRegistry.VAR_INT_TO_PAIR_COLOR);
    public static final GameData<List<Integer>> READY_PLAYERS = GameDataSyncer.registerData(GameDataHandlerRegistry.VAR_INT_LIST);
    protected final GameDataSyncer dataSyncer = new GameDataSyncer();

    protected Game() {
        this.dataSyncer.define(SEATING_ARRANGEMENT, Collections.emptyList());
        this.dataSyncer.define(CURRENT_ATTACKER, -1);
        this.dataSyncer.define(CANCELLABLE, false);
        this.dataSyncer.define(ATTACK_CARD, AttackPhase.SyncedAttackData.DUMMY);
        this.dataSyncer.define(LAST_ROUND, false);
        this.dataSyncer.define(CURRENT_TOSSER, -1);
        this.dataSyncer.define(PAIR_MAP, Collections.emptyMap());
        this.dataSyncer.define(READY_PLAYERS, Collections.emptyList());
    }

    public void tick() {
    }
}
