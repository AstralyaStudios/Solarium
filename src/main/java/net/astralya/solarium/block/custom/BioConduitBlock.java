package net.astralya.solarium.block.custom;

import com.mojang.serialization.MapCodec;
import net.astralya.solarium.block.entity.ModBlockEntityTypes;
import net.astralya.solarium.block.entity.custom.BioConduitBlockEntity;
import net.astralya.solarium.item.custom.WrenchItem;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BioConduitBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty EXTRACT_UP = BooleanProperty.create("extract_up");
    public static final BooleanProperty EXTRACT_DOWN = BooleanProperty.create("extract_down");
    public static final BooleanProperty EXTRACT_NORTH = BooleanProperty.create("extract_north");
    public static final BooleanProperty EXTRACT_SOUTH = BooleanProperty.create("extract_south");
    public static final BooleanProperty EXTRACT_WEST = BooleanProperty.create("extract_west");
    public static final BooleanProperty EXTRACT_EAST = BooleanProperty.create("extract_east");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final MapCodec<BioConduitBlock> CODEC = simpleCodec(BioConduitBlock::new);

    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape ARM_UP = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape ARM_DOWN = Block.box(6, 0, 6, 10, 6, 10);
    private static final VoxelShape ARM_NORTH = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape ARM_SOUTH = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape ARM_WEST = Block.box(0, 6, 6, 6, 10, 10);
    private static final VoxelShape ARM_EAST = Block.box(10, 6, 6, 16, 10, 10);

    public BioConduitBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition.any()
                        .setValue(UP, false).setValue(DOWN, false)
                        .setValue(NORTH, false).setValue(SOUTH, false)
                        .setValue(WEST, false).setValue(EAST, false)
                        .setValue(EXTRACT_UP, false).setValue(EXTRACT_DOWN, false)
                        .setValue(EXTRACT_NORTH, false).setValue(EXTRACT_SOUTH, false)
                        .setValue(EXTRACT_WEST, false).setValue(EXTRACT_EAST, false)
                        .setValue(WATERLOGGED, false)
                        .setValue(LIT, false)
        );
    }

    @Override
    protected MapCodec<? extends BioConduitBlock> codec() { return CODEC; }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        FluidState fluid = level.getFluidState(pos);
        return defaultBlockState()
                .setValue(UP, isConnected(level, pos, Direction.UP))
                .setValue(DOWN, isConnected(level, pos, Direction.DOWN))
                .setValue(NORTH, isConnected(level, pos, Direction.NORTH))
                .setValue(SOUTH, isConnected(level, pos, Direction.SOUTH))
                .setValue(WEST, isConnected(level, pos, Direction.WEST))
                .setValue(EAST, isConnected(level, pos, Direction.EAST))
                .setValue(EXTRACT_UP, false).setValue(EXTRACT_DOWN, false)
                .setValue(EXTRACT_NORTH, false).setValue(EXTRACT_SOUTH, false)
                .setValue(EXTRACT_WEST, false).setValue(EXTRACT_EAST, false)
                .setValue(WATERLOGGED, fluid.is(FluidTags.WATER) && fluid.getAmount() == 8)
                .setValue(LIT, false);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (level instanceof Level lvl) {
            BlockEntity be = lvl.getBlockEntity(pos);
            if (be instanceof BioConduitBlockEntity conduit) {
                conduit.onNeighborGraphChanged();
            }
            // Use isActuallyConnected to respect disconnections
            boolean connected = isActuallyConnected(lvl, pos, dir);
            return state.setValue(getConnProp(dir), connected);
        }
        // Fallback for LevelAccessor
        boolean connected = isConnected(level, pos, dir);
        return state.setValue(getConnProp(dir), connected);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BioConduitBlockEntity conduit) {
            conduit.onNeighborGraphChanged();
        }

        // Use isActuallyConnected instead of isConnected to respect disconnections
        BlockState newState = state
                .setValue(UP, isActuallyConnected(level, pos, Direction.UP))
                .setValue(DOWN, isActuallyConnected(level, pos, Direction.DOWN))
                .setValue(NORTH, isActuallyConnected(level, pos, Direction.NORTH))
                .setValue(SOUTH, isActuallyConnected(level, pos, Direction.SOUTH))
                .setValue(WEST, isActuallyConnected(level, pos, Direction.WEST))
                .setValue(EAST, isActuallyConnected(level, pos, Direction.EAST));

        if (newState != state) {
            level.setBlockAndUpdate(pos, newState);
        }
    }

    private boolean isConnected(LevelAccessor accessor, BlockPos pos, Direction dir) {
        if (accessor instanceof Level lvl) {
            return isActuallyConnected(lvl, pos, dir);
        }
        BlockPos otherPos = pos.relative(dir);
        BlockState other = accessor.getBlockState(otherPos);
        if (other.getBlock() instanceof BioConduitBlock) return true;
        BlockEntity be = accessor.getBlockEntity(otherPos);
        return be != null;
    }

    private static BooleanProperty getConnProp(Direction d) {
        return switch (d) {
            case UP -> UP; case DOWN -> DOWN;
            case NORTH -> NORTH; case SOUTH -> SOUTH;
            case WEST -> WEST; case EAST -> EAST;
        };
    }

    private static BooleanProperty getExtractProp(Direction d) {
        return switch (d) {
            case UP -> EXTRACT_UP; case DOWN -> EXTRACT_DOWN;
            case NORTH -> EXTRACT_NORTH; case SOUTH -> EXTRACT_SOUTH;
            case WEST -> EXTRACT_WEST; case EAST -> EXTRACT_EAST;
        };
    }

    public boolean isActuallyConnected(Level level, BlockPos pos, Direction dir) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BioConduitBlockEntity selfBE && selfBE.isDisconnected(dir)) return false;

        BlockPos otherPos = pos.relative(dir);
        BlockState otherState = level.getBlockState(otherPos);
        Block otherBlock = otherState.getBlock();

        if (otherBlock instanceof BioConduitBlock) {
            BlockEntity otherBe = level.getBlockEntity(otherPos);
            if (otherBe instanceof BioConduitBlockEntity oBE && oBE.isDisconnected(dir.getOpposite())) return false;
            return true;
        } else {
            return canConnectTo(level, pos, dir);
        }
    }

    public boolean canConnectTo(Level level, BlockPos pos, Direction dir) {
        BlockPos other = pos.relative(dir);
        BlockState otherState = level.getBlockState(other);
        if (otherState.isAir()) return false;
        IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, other, dir.getOpposite());
        return cap != null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        VoxelShape shape = CORE;
        if (state.getValue(UP)) shape = Shapes.or(shape, ARM_UP);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, ARM_DOWN);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, ARM_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, ARM_SOUTH);
        if (state.getValue(WEST)) shape = Shapes.or(shape, ARM_WEST);
        if (state.getValue(EAST)) shape = Shapes.or(shape, ARM_EAST);
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getShape(state, level, pos, ctx);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getShape(state, level, pos, ctx);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getShape(state, level, pos, CollisionContext.empty());
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack,
                                              BlockState state,
                                              Level level,
                                              BlockPos pos,
                                              Player player,
                                              InteractionHand hand,
                                              BlockHitResult hit) {
        if (!(stack.getItem() instanceof WrenchItem)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        Direction side = pickClickedSide(pos, hit);
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BioConduitBlockEntity conduit)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        boolean sneaking = player.isShiftKeyDown();
        BlockState newState = state;

        if (sneaking) {
            // Toggle disconnection
            boolean wasDisconnected = conduit.isDisconnected(side);
            boolean nowDisconnected = !wasDisconnected;

            // Set disconnection state first
            conduit.setDisconnected(side, nowDisconnected);

            // If we're disconnecting and the side was extracting, turn off extraction
            if (nowDisconnected && conduit.isExtracting(side)) {
                conduit.setExtracting(side, false);
                newState = newState.setValue(getExtractProp(side), false);
            }

            // Update connection state based on disconnection
            boolean shouldBeConnected = !nowDisconnected && canConnectTo(level, pos, side);
            newState = newState.setValue(getConnProp(side), shouldBeConnected);

            // Handle neighbor conduit updates
            BlockPos neighborPos = pos.relative(side);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof BioConduitBlock neighborConduit) {
                Direction oppositeSide = side.getOpposite();

                // Update neighbor's connection state
                boolean neighborShouldConnect = !nowDisconnected &&
                        neighborConduit.canConnectTo(level, neighborPos, oppositeSide) &&
                        neighborConduit.isActuallyConnected(level, neighborPos, oppositeSide);

                BlockState newNeighborState = neighborState.setValue(getConnProp(oppositeSide), neighborShouldConnect);
                level.setBlockAndUpdate(neighborPos, newNeighborState);

                // Notify neighbor conduit of graph change
                BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                if (neighborBE instanceof BioConduitBlockEntity neighborConduitBE) {
                    neighborConduitBE.onNeighborGraphChanged();
                }
            }

            // Send block updates to neighbors
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(neighborPos, level.getBlockState(neighborPos).getBlock());

        } else {
            // Toggle extraction (only if not disconnected)
            if (!conduit.isDisconnected(side)) {
                boolean newExtract = !conduit.isExtracting(side);
                conduit.setExtracting(side, newExtract);
                newState = newState.setValue(getExtractProp(side), newExtract);
            }

            // Always update connection state to current actual state
            boolean actuallyConnected = isActuallyConnected(level, pos, side);
            newState = newState.setValue(getConnProp(side), actuallyConnected);
        }

        // Mark everything as changed
        conduit.onNeighborGraphChanged();
        conduit.setChanged();

        // Update the block state
        level.setBlockAndUpdate(pos, newState);

        // Sync to clients
        conduit.syncData();

        return ItemInteractionResult.SUCCESS;
    }

    private static Direction pickClickedSide(BlockPos pos, BlockHitResult hit) {
        Vec3 local = hit.getLocation().subtract(Vec3.atLowerCornerOf(pos));
        double dx = local.x - 0.5;
        double dy = local.y - 0.5;
        double dz = local.z - 0.5;

        double ax = Math.abs(dx), ay = Math.abs(dy), az = Math.abs(dz);
        if (ay >= ax && ay >= az) return dy > 0 ? Direction.UP : Direction.DOWN;
        if (ax >= ay && ax >= az) return dx > 0 ? Direction.EAST : Direction.WEST;
        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(UP, DOWN, NORTH, SOUTH, WEST, EAST,
                EXTRACT_UP, EXTRACT_DOWN, EXTRACT_NORTH, EXTRACT_SOUTH, EXTRACT_WEST, EXTRACT_EAST,
                WATERLOGGED, LIT);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BioConduitBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntityTypes.BIO_CONDUIT.get(),
                (lvl, p, s, be) -> be.tick(lvl, p, s));
    }
}