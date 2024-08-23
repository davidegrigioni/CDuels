package cc.davyy.cduels.module;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.managers.DatabaseManager;
import cc.davyy.cduels.managers.DuelManager;
import cc.davyy.cduels.managers.KitManager;
import cc.davyy.cduels.managers.WorldCreatorManager;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class CModule extends AbstractModule {

    private final CDuels instance;

    public CModule(CDuels instance) {
        this.instance = instance;
    }

    @Override
    protected void configure() {
        this.bind(CDuels.class).toInstance(instance);

        this.bind(KitManager.class).asEagerSingleton();
        this.bind(DatabaseManager.class).asEagerSingleton();

        this.bind(WorldCreatorManager.class).in(Singleton.class);
        this.bind(DuelManager.class).in(Singleton.class);
    }

}