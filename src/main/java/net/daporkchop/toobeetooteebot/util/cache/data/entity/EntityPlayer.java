/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.toobeetooteebot.util.cache.data.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.packet.Packet;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class EntityPlayer extends EntityEquipment {
    @NonNull
    protected boolean selfPlayer;

    protected int food;
    protected float saturation;

    @Override
    public void addPackets(@NonNull Consumer<Packet> consumer) {
        if (this.selfPlayer) {
            consumer.accept(new ServerPlayerHealthPacket(
                    this.health,
                    this.food,
                    this.saturation
            ));
        } else {
            consumer.accept(new ServerSpawnPlayerPacket(
                    this.entityId,
                    this.uuid,
                    this.x,
                    this.y,
                    this.z,
                    this.yaw,
                    this.pitch,
                    this.metadata.toArray(new EntityMetadata[0])
            ));
        }
        super.addPackets(consumer);
    }

    /* // TODO figure out how to get the bounding box size and whether player is sneaking...
    @Override
    public double getEyeHeight() {
        final double boundingBoxHeight = 2;
        // this is minecraft code in EntityPlayer line 2471
        double height = 1.62F;

        if (this.isPlayerSleeping()) {
            height = 0.2F;
        } else if (!this.isSneaking() && boundingBoxHeight != 1.65F) {
            if (this.isElytraFlying() || boundingBoxHeight == 0.6F) {
                height = 0.4F;
            }
        } else {
            height -= 0.08F;
        }

        return height;
    }*/
}
