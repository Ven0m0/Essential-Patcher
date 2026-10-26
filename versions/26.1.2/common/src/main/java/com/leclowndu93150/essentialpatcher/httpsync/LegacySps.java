package com.leclowndu93150.essentialpatcher.httpsync;

import gg.essential.Essential;
import kotlin.Unit;

import java.lang.reflect.Method;
import java.util.UUID;

final class LegacySps {

    private LegacySps() {
    }

    static void listenForHosting() throws ReflectiveOperationException {
        Class<?> eventClass = Class.forName("gg.essential.event.sps.SPSStartEvent");
        Method getAddress = eventClass.getMethod("getAddress");
        Essential.EVENT_BUS.register(eventClass, event -> {
            try {
                String[] key = SessionKey.fromEssentialSpsAddress((String) getAddress.invoke(event));
                if (key != null) {
                    CosmeticHttpSync.get().onServerJoin(key[0], key[1]);
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to read legacy SPS start event", e);
            }
            return Unit.INSTANCE;
        }, 0);
    }

    static UUID localHost() {
        try {
            Object connectionManager = Essential.getInstance().getConnectionManager();
            Object spsManager = connectionManager.getClass().getMethod("getSpsManager").invoke(connectionManager);
            Object localSession = spsManager.getClass().getMethod("getLocalSession").invoke(spsManager);
            return localSession != null
                    ? (UUID) localSession.getClass().getMethod("getHostUUID").invoke(localSession)
                    : null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
