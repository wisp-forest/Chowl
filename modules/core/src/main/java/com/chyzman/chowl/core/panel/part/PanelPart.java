package com.chyzman.chowl.core.panel.part;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.panel.item.api.Frameable;
import com.chyzman.chowl.core.panel.registry.PanelComponents;
import com.chyzman.chowl.core.panel.registry.PanelParts;
import com.chyzman.chowl.core.util.ChowlEndecs;
import com.chyzman.chowl.core.util.VoxelShapeHelper;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class PanelPart extends Part implements ItemOwner {
    private static final VoxelShape SHAPE = Block.box(2, 2, 0, 14, 14, 2);

    public static final StructEndec<PanelPart> ENDEC = StructEndecBuilder.of(
        ChowlEndecs.FRONT_AND_TOP.fieldOf("orientation", part -> part.orientation),
        MinecraftEndecs.ITEM_STACK.fieldOf("panel", part -> part.panel),
        PanelPart::new
    );

    public final FrontAndTop orientation;
    private ItemStack panel;

    public PanelPart(FrontAndTop orientation, ItemStack panel) {
        super(PanelParts.PANEL);
        this.orientation = orientation;
        this.panel = panel;

        this.addSubPart(new RemovePart());
        if (panel.has(PanelComponents.LOCKED)) this.addSubPart(new LockPart());
        if (panel.getItem() instanceof Frameable frameable) frameable.addSubParts(this, panel);
    }

    public PanelPart(List<Part> parts) {
        this(FrontAndTop.NORTH_UP, ItemStack.EMPTY);
    }

    public void update() {
        this.holder.markDirtyAndUpdateClients();
    }

    @Override
    public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context) {
        return VoxelShapeHelper.rotate(SHAPE, orientation);
    }

    @Override
    public Level level() {
        return level;
    }

    @Override
    public Vec3 position() {
        //FIXME: should probably be positioned where the center of the panel is idk
        return pos.getCenter();
    }

    @Override
    public float getVisualRotationYInDegrees() {
        //FIXME: verify this is correct
        return ((orientation.top() == Direction.UP) ? orientation.front() : orientation.top()).toYRot();
    }

    //TODO: should we make this an actual part?
    public class RemovePart extends Part {
        private static final VoxelShape SHAPE = Block.box(0, 14, -0.25, 2, 16, 0);

        protected RemovePart() {
            super(null);
        }

        @Override
        public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
            PanelPart.this.getHolder().removePart(PanelPart.this);
            update();
            return InteractionResult.SUCCESS;
        }

        @Override
        public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context) {
            return VoxelShapeHelper.rotate(SHAPE, orientation);
        }
    }

    public class LockPart extends Part {
        private static final VoxelShape SHAPE = Block.box(2, 14, -0.25, 4, 16, 0);

        protected LockPart() {
            super(null);
        }

        @Override
        public InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
            if (!panel.has(PanelComponents.LOCKED)) return InteractionResult.FAIL;
            panel.set(PanelComponents.LOCKED, !Boolean.TRUE.equals(panel.get(PanelComponents.LOCKED)));
            update();
            return InteractionResult.SUCCESS;
        }

        @Override
        public VoxelShape getPartOutlineShape(List<Part> otherParts, BlockGetter world, BlockPos pos, CollisionContext context) {
            return VoxelShapeHelper.rotate(SHAPE, orientation);
        }
    }
}
