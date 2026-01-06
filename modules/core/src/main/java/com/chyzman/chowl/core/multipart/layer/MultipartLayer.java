package com.chyzman.chowl.core.multipart.layer;

import com.chyzman.chowl.core.multipart.mixin.accessor.ChainRestrictedNeighborUpdaterAccessor;
import com.chyzman.chowl.core.multipart.mixin.accessor.WorldAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;

public class MultipartLayer extends Level {
    private final Level mainWorld;
    private final MultipartChunkManager chunkManager;
    private final EmptyEntityLookup entityLookup;

    protected MultipartLayer(Level mainWorld) {
        super(
          (WritableLevelData) mainWorld.getLevelData(),
          mainWorld.dimension(),
          mainWorld.registryAccess(),
          mainWorld.dimensionTypeRegistration(),
          mainWorld.isClientSide(),
          mainWorld.isDebug(),
          0L,
          ((ChainRestrictedNeighborUpdaterAccessor) (((WorldAccessor) mainWorld).getNeighborUpdater())).getMaxChainedNeighborUpdates()
        );
        this.mainWorld = mainWorld;
        this.chunkManager = new MultipartChunkManager(mainWorld.getChunkSource());
        this.entityLookup = new EmptyEntityLookup();
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {}

    @Override
    public void playSeededSound(@Nullable Entity source, double x, double y, double z, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {}

    @Override
    public void playSeededSound(@Nullable Entity source, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed) {}

    @Override
    public void explode(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, ExplosionInteraction explosionSourceType, ParticleOptions smallParticle, ParticleOptions largeParticle, WeightedList<ExplosionParticleInfo> blockParticles, Holder<SoundEvent> soundEvent) {}

    @Override
    public String gatherChunkSourceStats() {
        return "";
    }

    @Override
    public void setRespawnData(LevelData.RespawnData spawnPoint) {

    }

    @Override
    public LevelData.RespawnData getRespawnData() {
        return null;
    }

    @Override
    public @Nullable Entity getEntity(int id) {
        return null;
    }

    @Override
    public Collection<EnderDragonPart> dragonParts() {
        return List.of();
    }

    @Override
    public TickRateManager tickRateManager() {
        return mainWorld.tickRateManager();
    }

    @Override
    public @Nullable MapItemSavedData getMapData(MapId id) {
        return mainWorld.getMapData(id);
    }

    @Override
    public void destroyBlockProgress(int entityId, BlockPos pos, int progress) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return mainWorld.getScoreboard();
    }

    @Override
    public RecipeAccess recipeAccess() {
        return mainWorld.recipeAccess();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return entityLookup;
    }

    @Override
    public EnvironmentAttributeSystem environmentAttributes() {
        return null;
    }

    @Override
    public PotionBrewing potionBrewing() {
        return mainWorld.potionBrewing();
    }

    @Override
    public FuelValues fuelValues() {
        return mainWorld.fuelValues();
    }

    @Override
    public ChunkSource getChunkSource() {
        return chunkManager;
    }

    @Override
    public void levelEvent(@org.jspecify.annotations.Nullable Entity source, int eventId, BlockPos pos, int data) {}

    @Override
    public void gameEvent(Holder<GameEvent> event, Vec3 emitterPos, GameEvent.Context emitter) {}

    @Override
    public List<? extends Player> players() {
        return List.of();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int biomeX, int biomeY, int biomeZ) {
        return mainWorld.registryAccess().get(Biomes.THE_VOID)
          .map(biomes -> (Holder<Biome>) biomes) // Map to RegistryEntry so I can use orElse
          .orElse(mainWorld.getUncachedNoiseBiome(biomeX, biomeY, biomeZ));
    }

    @Override
    public int getSeaLevel() {
        return mainWorld.getSeaLevel();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return mainWorld.enabledFeatures();
    }

    @Override
    public float getShade(Direction direction, boolean shaded) {
        return 0;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return mainWorld.getBlockTicks();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return mainWorld.getFluidTicks();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }
}
