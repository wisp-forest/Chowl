package com.chyzman.chowl.core.network.codecs;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

public interface MorePacketCodecs {
    PacketCodec<ByteBuf, Vec3d> VEC_3D = new PacketCodec<>() {
        public Vec3d decode(ByteBuf byteBuf) {
            return PacketByteBuf.readVec3d(byteBuf);
        }

        public void encode(ByteBuf byteBuf, Vec3d vec3d) {
            PacketByteBuf.writeVec3d(byteBuf, vec3d);
        }
    };
}
