package net.daporkchop.toobeetooteebot.mc;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import net.daporkchop.lib.math.vector.d.Vec3d;
import net.daporkchop.toobeetooteebot.Bot;
import net.daporkchop.toobeetooteebot.util.cache.data.entity.Entity;
import net.daporkchop.toobeetooteebot.util.cache.data.entity.EntityPlayer;

import static net.daporkchop.toobeetooteebot.util.Constants.*;

public class BotHandler {



    public void checkToLookAtClosestEntity() {
        if(!CONFIG.client.extra.customRotations.enabled) {
            return;
        }
        if(CONFIG.client.extra.customRotations.runEvenIfClientsConnected && Bot.getInstance().getCurrentPlayer() == null) {
            // TODO fix this check
            /* setting playerConnected cancel?
                  0  0  0
                  0  1  1
                  1  0  0
                  1  1  0

             */
            return;
        }



        if(CONFIG.client.extra.customRotations.lookAtClosestEntity) {
            lookAtClosestEntity();
        }

    }

    public void lookAtClosestEntity() {
        final EntityPlayer player = CACHE.getPlayerCache().getThePlayer();
        CACHE.getEntityCache()
                .getCachedEntities()
                .values()
                .stream()
                .filter(entity -> entity != player)
                .min((a, b) -> (int) (distance(a, player) - distance(b, player)))
                .ifPresent(entity -> lookAt(player, entity.getX(), entity.getY(), entity.getZ()));
    }

    public void lookAt(final Entity origin, final double x, final double y, final double z) {
        // TODO get eye level somehow
        final Vec3d vector = new Vec3d(origin.getX() - x, origin.getY() - y, origin.getZ() - z);
        final float yaw = (float) calculateYaw(vector);
        final float pitch = (float) calculatePitch(vector);

        lookAt(yaw, pitch);

    }


    public void lookAt(final float yaw, final float pitch) {
        new Thread(() -> {

            // TODO only send the packet if last sent is not this..
            if(Bot.getInstance().isConnected() && CACHE.getPlayerCache().getThePlayer().getHealth() > 0) {
                Bot.getInstance().getClient().getSession().send(
                        new ClientPlayerRotationPacket(true, yaw, pitch));

            }

        }).start();
    }


    // TODO make a util class?
    private double calculateYaw(final Vec3d vector) {
        return Math.toDegrees(Math.atan2(vector.getZ(), vector.getX()) + Math.PI / 2.0);
    }

    private double calculatePitch(final Vec3d vector) {
        final double mag = Math.sqrt(square(vector.getX()) + square(vector.getZ()));
        return Math.toDegrees(Math.atan2(vector.getY(), mag));
    }

    private double distance(final Entity a, final Entity b) {
        return square(a.getX() - b.getX()) + square(a.getY() - b.getY()) + square(a.getZ() - b.getZ());
    }

    private double square(final double number) {
        return number * number;
    }


}
