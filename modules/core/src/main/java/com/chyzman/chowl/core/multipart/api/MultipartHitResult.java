package com.chyzman.chowl.core.multipart.api;

import com.chyzman.chowl.core.network.codecs.MorePacketCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MultipartHitResult extends BlockHitResult {
    public static final StreamCodec<ByteBuf, MultipartHitResult> PACKET_CODEC = StreamCodec.composite(
      MorePacketCodecs.VEC_3D,
      MultipartHitResult::getLocation,
      Direction.STREAM_CODEC,
      MultipartHitResult::getDirection,
      BlockPos.STREAM_CODEC,
      MultipartHitResult::getBlockPos,
      ByteBufCodecs.BYTE_ARRAY,
      MultipartHitResult::getPart,
      ByteBufCodecs.BOOL,
      MultipartHitResult::isInside,
      ByteBufCodecs.BOOL,
      MultipartHitResult::isWorldBorderHit,
      MultipartHitResult::new
    );

    private final byte[] part;

    public MultipartHitResult(BlockHitResult result, byte[] part) {
        this(result.getLocation(), result.getDirection(), result.getBlockPos(), part, result.isInside(), result.isWorldBorderHit());
    }

    public MultipartHitResult(Vec3 pos, Direction side, BlockPos blockPos, byte[] part, boolean insideBlock) {
        this(pos, side, blockPos, part, insideBlock, false);
    }

    public MultipartHitResult(Vec3 pos, Direction side, BlockPos blockPos, byte[] part, boolean insideBlock, boolean againstWorldBorder) {
        super(pos, side, blockPos, insideBlock, againstWorldBorder);
        this.part = part;
    }

    public byte[] getPart() {
        return part;
    }
}
