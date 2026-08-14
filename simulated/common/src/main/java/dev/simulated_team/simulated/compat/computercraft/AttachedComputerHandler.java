package dev.simulated_team.simulated.compat.computercraft;

import dan200.computercraft.api.peripheral.IComputerAccess;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AttachedComputerHandler {

    private final Set<IComputerAccess> attachedComputers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void attach(IComputerAccess computer) {
        this.attachedComputers.add(computer);
    }

    public void detach(IComputerAccess computer) {
        this.attachedComputers.remove(computer);
    }

    public void queueEvent(String event, @Nullable Object... args) {
        for (final IComputerAccess computer : this.attachedComputers) {
            computer.queueEvent(event, args);
        }
    }
}
