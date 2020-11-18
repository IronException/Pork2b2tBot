package net.daporkchop.toobeetooteebot.mc;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import net.daporkchop.lib.math.vector.d.Vec3d;
import net.daporkchop.toobeetooteebot.Bot;
import net.daporkchop.toobeetooteebot.util.cache.data.entity.Entity;
import net.daporkchop.toobeetooteebot.util.cache.data.entity.EntityPlayer;

import static net.daporkchop.toobeetooteebot.util.Constants.CACHE;
import static net.daporkchop.toobeetooteebot.util.Constants.CONFIG;

public class RotationHandler {

private final static RotationHandler INSTANCE = new RotationHandler();

    public static void update() {
        INSTANCE.lookAtClosestEntity();
    }

    public void lookAtClosestEntity() {
        if(!CONFIG.client.extra.customRotations.enabled) {
            return;
        }
        if(!CONFIG.client.extra.customRotations.runEvenIfClientsConnected && Bot.getInstance().getCurrentPlayer() != null) {
            return;
        }

        final EntityPlayer player = CACHE.getPlayerCache().getThePlayer();

        if(CONFIG.client.extra.customRotations.lookedAtClosestEntity) {
            CACHE.getEntityCache()
                    .getCachedEntities()
                    .values()
                    .stream()
                    .filter(entity -> entity != player)
                    .min((a, b) -> (int) (distance(a, player) - distance(b, player)))
                    .ifPresent(entity -> lookAt(player, entity.getX(), entity.getY(), entity.getZ()));
        }

    }

    public void lookAt(final Entity origin, final double x, final double y, final double z) {
        final Vec3d vector = new Vec3d(origin.getX() - x, origin.getY() - y, origin.getZ() - z);
        final float yaw = (float) calculateYaw(vector);
        final float pitch = (float) calculatePitch(vector);

        lookAt(yaw, pitch);

    }

    private double calculateYaw(final Vec3d vector) {
        return Math.toDegrees(Math.atan2(vector.getZ(), vector.getX()) + Math.PI / 2.0);
    }

    private double calculatePitch(final Vec3d vector) {
        final double mag = Math.sqrt(square(vector.getX()) + square(vector.getZ()));
        return Math.toDegrees(Math.atan2(vector.getY(), mag));
    }

    public void lookAt(final float yaw, final float pitch) {
        new Thread(() -> {

            if(Bot.getInstance().isConnected() && CACHE.getPlayerCache().getThePlayer().getHealth() > 0) {
                Bot.getInstance().getClient().getSession().send(
                        new ClientPlayerRotationPacket(true, yaw, pitch));

            }

        }).start();
    }

    private double distance(final Entity a, final Entity b) {
        return square(a.getX() - b.getX()) + square(a.getY() - b.getY()) + square(a.getZ() - b.getZ());
    }

    private double square(final double number) {
        return number * number;
    }


}
