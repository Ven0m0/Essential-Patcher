package com.leclowndu93150.essentialpatcher.httpsync;

import gg.essential.Essential;
import gg.essential.gui.elementa.state.v2.ReferenceHolderImpl;
import gg.essential.gui.elementa.state.v2.State;
import gg.essential.gui.elementa.state.v2.StateKt;
import kotlin.Unit;

import java.util.UUID;

final class ModernSps {

    private static ReferenceHolderImpl hostingWatcher;

    private ModernSps() {
    }

    static void listenForHosting() {
        hostingWatcher = new ReferenceHolderImpl();
        State<?> integratedServerWorld = integratedServerWorld();
        StateKt.effect(hostingWatcher, observer -> {
            Object world = observer.invoke(integratedServerWorld);
            if (world != null && observer.invoke(localWorldOpen(world))) {
                String[] key = SessionKey.compute();
                if (key != null) {
                    CosmeticHttpSync.get().onServerJoin(key[0], key[1]);
                }
            }
            return Unit.INSTANCE;
        });
    }

    static UUID localHost() {
        Object world = integratedServerWorld().getUntracked();
        return world != null && localWorldOpen(world).getUntracked()
                ? host(world).getUntracked()
                : null;
    }

    private static State<?> integratedServerWorld() {
        try {
            Object essential = Essential.getInstance();
            Object worldsManager = essential.getClass().getMethod("getWorldsManager").invoke(essential);
            return (State<?>) worldsManager.getClass().getMethod("getIntegratedServerWorld").invoke(worldsManager);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to access modern SPS worlds manager", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static State<Boolean> localWorldOpen(Object world) {
        try {
            return (State<Boolean>) world.getClass().getMethod("getLocalWorldOpen").invoke(world);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to access modern SPS hosting state", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static State<UUID> host(Object world) {
        try {
            return (State<UUID>) world.getClass().getMethod("getHost").invoke(world);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to access modern SPS host state", e);
        }
    }
}
