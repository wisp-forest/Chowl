package com.chyzman.chowl.core.multipart.layer;

import com.chyzman.chowl.core.multipart.mixin.accessor.ChainRestrictedNeighborUpdaterAccessor;
import com.chyzman.chowl.core.multipart.mixin.accessor.WorldAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.map.MapState;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.attribute.WorldEnvironmentAttributeAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class MultipartLayer extends World {
    private final World mainWorld;
    private final MultipartChunkManager chunkManager;
    private final EmptyEntityLookup entityLookup;

    protected MultipartLayer(World mainWorld) {
        super(
          (MutableWorldProperties) mainWorld.getLevelProperties(),
          mainWorld.getRegistryKey(),
          mainWorld.getRegistryManager(),
          mainWorld.getDimensionEntry(),
          mainWorld.isClient(),
          mainWorld.isDebugWorld(),
          0L,
          ((WorldAccessor) mainWorld).getNeighborUpdater() instanceof ChainRestrictedNeighborUpdater ?
            ((ChainRestrictedNeighborUpdaterAccessor) (((WorldAccessor) mainWorld).getNeighborUpdater())).getMaxChainDepth() :
            100000
        );
        this.mainWorld = mainWorld;
        this.chunkManager = new MultipartChunkManager(mainWorld.getChunkManager());
        this.entityLookup = new EmptyEntityLookup();
    }

    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {}

    @Override
    public void playSound(@Nullable Entity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {}

    @Override
    public void playSoundFromEntity(@Nullable Entity source, Entity entity, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {}

    @Override
    public void createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, Pool<BlockParticleEffect> blockParticles, RegistryEntry<SoundEvent> soundEvent) {}

    @Override
    public String asString() {
        return "";
    }

    @Override
    public void setSpawnPoint(WorldProperties.SpawnPoint spawnPoint) {

    }

    @Override
    public WorldProperties.SpawnPoint getSpawnPoint() {
        return null;
    }

    @Override
    public @Nullable Entity getEntityById(int id) {
        return null;
    }

    @Override
    public Collection<EnderDragonPart> getEnderDragonParts() {
        return List.of();
    }

    @Override
    public TickManager getTickManager() {
        return mainWorld.getTickManager();
    }

    @Override
    public @Nullable MapState getMapState(MapIdComponent id) {
        return mainWorld.getMapState(id);
    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return mainWorld.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return mainWorld.getRecipeManager();
    }

    @Override
    protected EntityLookup<Entity> getEntityLookup() {
        return entityLookup;
    }

    @Override
    public WorldEnvironmentAttributeAccess getEnvironmentAttributes() {
        return null;
    }

    @Override
    public BrewingRecipeRegistry getBrewingRecipeRegistry() {
        return mainWorld.getBrewingRecipeRegistry();
    }

    @Override
    public FuelRegistry getFuelRegistry() {
        return mainWorld.getFuelRegistry();
    }

    @Override
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    @Override
    public void syncWorldEvent(@org.jspecify.annotations.Nullable Entity source, int eventId, BlockPos pos, int data) {}

    @Override
    public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, GameEvent.Emitter emitter) {}

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return List.of();
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return mainWorld.getRegistryManager().getOptionalEntry(BiomeKeys.THE_VOID)
          .map(biomes -> (RegistryEntry<Biome>) biomes) // Map to RegistryEntry so I can use orElse
          .orElse(mainWorld.getGeneratorStoredBiome(biomeX, biomeY, biomeZ));
    }

    @Override
    public int getSeaLevel() {
        return mainWorld.getSeaLevel();
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return mainWorld.getEnabledFeatures();
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return 0;
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return mainWorld.getBlockTickScheduler();
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return mainWorld.getFluidTickScheduler();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }
}
