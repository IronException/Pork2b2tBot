package net.daporkchop.toobeetooteebot.util.cache.data.entity;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;
import net.daporkchop.lib.math.vector.d.Vec3d;
import net.daporkchop.toobeetooteebot.Bot;

import java.util.Optional;

import static net.daporkchop.toobeetooteebot.util.Constants.*;

public class PlayerBot extends EntityPlayer {

    protected double serverX, serverY, serverZ;
    protected float serverYaw, serverPitch;

    public PlayerBot() {
        super(true);
    }


    public void tick() {
        // TODO make a queue that lists all the actions that should be done...
        // TODO if there is nothing in the queue look at closest entity
        lookAtClosestEntity();

        updateServerAndClients();
    }

    public void lookAtClosestEntity() { // TODO settings whether to prefer players or entities
        Optional<Entity> closest = UTIL.getClosestPlayer().map(player -> player);
        if(!closest.isPresent()) {
            closest = UTIL.getClosestEntity();
        }
        closest.ifPresent(this::lookAt);
    }

    public void lookAt(final Entity entity) {
        // TODO getEyeLevel and look at that x y z
        lookAt(entity.getX(), entity.getY(), entity.getZ());

    }

    public void lookAt(final double x, final double y, final double z) {
        // TODO get eye level
        final Vec3d vector = new Vec3d(getX() - x, getY() - y, getZ() - z);
        final float yaw = (float) UTIL.calculateYaw(vector);
        final float pitch = (float) UTIL.calculatePitch(vector);
        lookAt(yaw, pitch);
    }

    public void lookAt(final float yaw, final float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }


    public void updateServerAndClients() {
        // TODO make the auto respawn here...
        if (CACHE.getPlayerCache().getThePlayer().getHealth() <= 0) {
            return;
        }
        // TODO what about onGround?
        // TODO also send the data to the connected clients...
        final boolean positionChanged = serverX != x
                || serverY != y
                || serverZ != z;
        final boolean rotationChanged = serverYaw != yaw || serverPitch != pitch;

        if(positionChanged && rotationChanged) {
            sendPacket(new ClientPlayerPositionRotationPacket(true, x, y, z, yaw, pitch));
            serverX = x;
            serverY = y;
            serverZ = z;
            serverYaw = yaw;
            serverPitch = pitch;
        } else if(positionChanged) {
            sendPacket(new ClientPlayerPositionPacket(true, x, y, z));
            serverX = x;
            serverY = y;
            serverZ = z;
        } else if(rotationChanged) {
            sendPacket(new ClientPlayerRotationPacket(true, yaw, pitch));
            serverYaw = yaw;
            serverPitch = pitch;
        }
    }

    private void sendPacket(final Packet packet) {
        Bot.getInstance().getClient().getSession().send(packet);
    }



}
