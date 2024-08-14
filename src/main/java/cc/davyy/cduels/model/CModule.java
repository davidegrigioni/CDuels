package cc.davyy.cduels.model;

import cc.davyy.cduels.CDuels;
import cc.davyy.cduels.managers.KitManager;
import com.google.inject.AbstractModule;

public class CModule extends AbstractModule {

    private final CDuels instance;

    public CModule(CDuels instance) {
        this.instance = instance;
    }

    @Override
    protected void configure() {
        bind(CDuels.class).toInstance(instance);

        bind(KitManager.class).asEagerSingleton();
    }

}