package net.daporkchop.toobeetooteebot.mc;

import net.daporkchop.lib.math.vector.d.DoubleVector3;
import net.daporkchop.toobeetooteebot.util.cache.data.entity.Entity;
import net.daporkchop.toobeetooteebot.util.cache.data.entity.EntityPlayer;

import java.util.Optional;
import java.util.stream.Stream;

import static net.daporkchop.toobeetooteebot.util.Constants.CACHE;

public class Util {

    public Optional<EntityPlayer> getClosestPlayer() {
        final EntityPlayer player = CACHE.getPlayerCache().getThePlayer();
        return getEntities()
                .filter(EntityPlayer.class::isInstance)
                .map(EntityPlayer.class::cast)
                .min((a, b) -> (int) (distance(a, player) - distance(b, player)));
    }

    public Optional<Entity> getClosestEntity() {
        final EntityPlayer player = CACHE.getPlayerCache().getThePlayer();
        return getEntities()
                .min((a, b) -> (int) (distance(a, player) - distance(b, player)));
    }

    public Stream<Entity> getEntities() {
        return CACHE.getEntityCache()
                .getCachedEntities()
                .values()
                .stream()
                .filter(entity -> entity != CACHE.getPlayerCache().getThePlayer());
    }

    // TODO make a util class?
    public double calculateYaw(final DoubleVector3 vector) {
        return Math.toDegrees(Math.atan2(vector.getZ(), vector.getX()) + Math.PI / 2.0);
    }

    public double calculatePitch(final DoubleVector3 vector) {
        final double mag = Math.sqrt(square(vector.getX()) + square(vector.getZ()));
        return Math.toDegrees(Math.atan2(vector.getY(), mag));
    }

    public double distance(final Entity a, final Entity b) {
        return square(a.getX() - b.getX()) + square(a.getY() - b.getY()) + square(a.getZ() - b.getZ());
    }

    public double square(final double number) {
        return number * number;
    }


}
