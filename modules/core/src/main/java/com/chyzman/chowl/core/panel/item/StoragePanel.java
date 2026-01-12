package com.chyzman.chowl.core.panel.item;

import com.chyzman.chowl.core.multipart.api.Part;
import com.chyzman.chowl.core.panel.item.api.Frameable;
import com.chyzman.chowl.core.panel.registry.PanelComponents;
import com.chyzman.chowl.core.util.VoxelShapeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class StoragePanel extends Item implements Frameable {
    public StoragePanel(Properties properties) {
        super(properties);
    }

    @Override
    public void addSubParts(Part part, ItemStack stack) {

    }

    public class StoragePart extends Part {
        private static final VoxelShape SHAPE = Block.box(2, 2, -0.25, 14, 14, 0);

        protected StoragePart() {
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
