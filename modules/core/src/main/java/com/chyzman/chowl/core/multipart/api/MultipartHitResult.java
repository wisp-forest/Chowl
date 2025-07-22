package com.chyzman.chowl.core.multipart.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class MultipartHitResult extends BlockHitResult {
    public static final PacketCodec<ByteBuf, MultipartHitResult> PACKET_CODEC = PacketCodec.tuple(
      PacketCodecs.VECTOR_3F,
      MultipartHitResult::getOffset,
      Direction.PACKET_CODEC,
      MultipartHitResult::getSide,
      BlockPos.PACKET_CODEC,
      MultipartHitResult::getBlockPos,
      PacketCodecs.BYTE_ARRAY,
      MultipartHitResult::getPart,
      PacketCodecs.BOOLEAN,
      MultipartHitResult::isInsideBlock,
      PacketCodecs.BOOLEAN,
      MultipartHitResult::isAgainstWorldBorder,
      MultipartHitResult::new
    );

    private final byte[] part;

    public MultipartHitResult(BlockHitResult result, byte[] part) {
        this(result.getPos(), result.getSide(), result.getBlockPos(), part, result.isInsideBlock(), result.isAgainstWorldBorder());
    }

    public MultipartHitResult(Vec3d pos, Direction side, BlockPos blockPos, byte[] part, boolean insideBlock) {
        this(pos, side, blockPos, part, insideBlock, false);
    }

    public MultipartHitResult(Vec3d pos, Direction side, BlockPos blockPos, byte[] part, boolean insideBlock, boolean againstWorldBorder) {
        super(pos, side, blockPos, insideBlock, againstWorldBorder);
        this.part = part;
    }

    private MultipartHitResult(Vector3f offset, Direction side, BlockPos blockPos, byte[] part, boolean insideBlock, boolean againstWorldBorder) {
        this(new Vec3d(offset.add(blockPos.getX(), blockPos.getY(), blockPos.getZ())), side, blockPos, part, insideBlock, againstWorldBorder);
    }

    public byte[] getPart() {
        return part;
    }

    public Vector3f getOffset() {
        return this.pos.subtract(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()).toVector3f();
    }
}
