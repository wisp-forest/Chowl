package com.chyzman.chowl.core.network.codecs;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public interface MorePacketCodecs {
    StreamCodec<ByteBuf, Vec3> VEC_3D = new StreamCodec<>() {
        public Vec3 decode(ByteBuf byteBuf) {
            return FriendlyByteBuf.readVec3(byteBuf);
        }

        public void encode(ByteBuf byteBuf, Vec3 vec3d) {
            FriendlyByteBuf.writeVec3(byteBuf, vec3d);
        }
    };
}
