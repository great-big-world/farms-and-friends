package dev.creoii.farmsandfriends.block;

import com.mojang.serialization.MapCodec;
import dev.creoii.farmsandfriends.block.entity.OvenBlockEntity;
import dev.creoii.farmsandfriends.registry.FarmsAndFriendsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class OvenBlock extends AbstractFurnaceBlock {
    public static final VoxelShape BODY = Block.box(0d, 1d, 0d, 16d, 12d, 16d);
    public static final VoxelShape FEET = Shapes.or(Block.box(0d, 0d, 0d, 3d, 1d, 3d), Block.box(13d, 0d, 0d, 16d, 1d, 3d), Block.box(0d, 0d, 13d, 3d, 1d, 16d), Block.box(13d, 0d, 13d, 16d, 1d, 16d));
    public static final VoxelShape CHIMNEY = Block.box(5d, 12d, 5d, 11d, 16d, 11d);
    public static final VoxelShape SHAPE = Shapes.or(BODY, FEET, CHIMNEY);
    public static final MapCodec<OvenBlock> CODEC = simpleCodec(OvenBlock::new);

    public MapCodec<OvenBlock> codec() {
        return CODEC;
    }

    public OvenBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new OvenBlockEntity(blockPos, blockState);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if (level instanceof ServerLevel serverLevel) {
            return createTickerHelper(blockEntityType, FarmsAndFriendsBlockEntities.OVEN, (levelx, blockPos, state, abstractFurnaceBlockEntity) -> OvenBlockEntity.serverTick(serverLevel, blockPos, state, abstractFurnaceBlockEntity));
        } else return null;
    }

    protected void openContainer(Level level, BlockPos blockPos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof OvenBlockEntity) {
            player.openMenu((MenuProvider) blockEntity);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (blockState.getValue(LIT)) {
            double d = (double)blockPos.getX() + (double).5F;
            double e = blockPos.getY() + .25d;
            double f = (double)blockPos.getZ() + (double).5F;
            if (randomSource.nextDouble() < .1d) {
                level.playLocalSound(d, e, f, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1f, 1f, false);
            }

            Direction direction = blockState.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double h = randomSource.nextDouble() * .6d - .3d;
            double i = axis == Direction.Axis.X ? (double)direction.getStepX() * .52d : h;
            double j = randomSource.nextDouble() * (double)6f / (double)16f;
            double k = axis == Direction.Axis.Z ? (double)direction.getStepZ() * .52d : h;
            level.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0f, 0f, 0f);
            level.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0f, 0f, 0f);
        }
    }
}
