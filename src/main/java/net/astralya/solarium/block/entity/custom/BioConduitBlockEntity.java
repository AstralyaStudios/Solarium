package net.astralya.solarium.block.entity.custom;

import net.astralya.solarium.block.custom.BioConduitBlock;
import net.astralya.solarium.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.*;

public class BioConduitBlockEntity extends BlockEntity {

    private static final int INVALIDATE_DELAY_TICKS = 2;
    private static final int TRANSFER_PER_TICK = 320;

    private int litTicks = 0;

    private boolean[] extractingSides = new boolean[Direction.values().length];
    private boolean[] disconnectedSides = new boolean[Direction.values().length];

    @Nullable private List<Connection> connectionCache;
    @Nullable private Connection[] extractingConnectionCache;

    private int invalidateCountdown = INVALIDATE_DELAY_TICKS;

    public BioConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BIO_CONDUIT.get(), pos, state);
    }

    @SuppressWarnings("unused")
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        if (invalidateCountdown == 0) {
            connectionCache = null;
            extractingConnectionCache = null;
            invalidateCountdown = -1;
        } else if (invalidateCountdown > 0) {
            invalidateCountdown--;
        }

        if (litTicks > 0) litTicks--;

        boolean movedAny = false;

        if (level instanceof ServerLevel server) {
            Connection[] sources = getExtractingConnectionCache(server);
            List<Connection> sinks = getInsertConnections(server);

            if (sources != null && !sinks.isEmpty()) {
                for (Direction dir : Direction.values()) {
                    Connection src = sources[dir.get3DDataValue()];
                    if (src == null) continue;

                    IEnergyStorage from = src.getEnergyHandler();
                    if (from == null || !from.canExtract()) continue;

                    int budget = TRANSFER_PER_TICK;

                    for (Connection sink : sinks) {
                        if (budget <= 0) break;

                        IEnergyStorage to = sink.getEnergyHandler();
                        if (to == null || !to.canReceive()) continue;

                        int canRx = to.receiveEnergy(budget, true);
                        if (canRx <= 0) continue;

                        int extracted = from.extractEnergy(canRx, false);
                        if (extracted <= 0) continue;

                        int accepted = to.receiveEnergy(extracted, false);
                        if (accepted < extracted) {
                            from.receiveEnergy(extracted - accepted, false);
                        }

                        if (accepted > 0) {
                            movedAny = true;
                            budget -= accepted;
                        }
                    }
                }
            }

            if (movedAny) {
                pulseLit(3);
                propagateLit(server, 2);
            }
        }

        boolean shouldBeLit = litTicks > 0;
        if (state.hasProperty(BioConduitBlock.LIT) && state.getValue(BioConduitBlock.LIT) != shouldBeLit) {
            level.setBlock(pos, state.setValue(BioConduitBlock.LIT, shouldBeLit), Block.UPDATE_CLIENTS);
        }
    }

    public boolean isExtracting(Direction side) {
        return extractingSides[side.get3DDataValue()];
    }

    public void setExtracting(Direction side, boolean extracting) {
        extractingSides[side.get3DDataValue()] = extracting;
        setChanged();
        markNetworkDirty();
        syncData();
    }

    public boolean isDisconnected(Direction side) {
        return disconnectedSides[side.get3DDataValue()];
    }

    public void setDisconnected(Direction side, boolean disconnected) {
        disconnectedSides[side.get3DDataValue()] = disconnected;
        setChanged();
        markNetworkDirty();
        syncData();
    }

    public void onNeighborGraphChanged() {
        markNetworkDirty();
    }

    public boolean isAnyExtracting() {
        for (boolean b : extractingSides) if (b) return true;
        return false;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        markNetworkDirty();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        markNetworkDirty();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        byte[] ex = new byte[6];
        byte[] dc = new byte[6];
        for (Direction d : Direction.values()) {
            int i = d.get3DDataValue();
            ex[i] = (byte) (extractingSides[i] ? 1 : 0);
            dc[i] = (byte) (disconnectedSides[i] ? 1 : 0);
        }
        tag.put("ExtractingSides", new ByteArrayTag(ex));
        tag.put("DisconnectedSides", new ByteArrayTag(dc));
        tag.putInt("InvalidateCountdown", Math.max(invalidateCountdown, 0));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        extractingSides = new boolean[6];
        disconnectedSides = new boolean[6];

        byte[] ex = tag.getByteArray("ExtractingSides");
        byte[] dc = tag.getByteArray("DisconnectedSides");
        for (Direction d : Direction.values()) {
            int i = d.get3DDataValue();
            if (i < ex.length) extractingSides[i] = ex[i] != 0;
            if (i < dc.length) disconnectedSides[i] = dc[i] != 0;
        }
        invalidateCountdown = INVALIDATE_DELAY_TICKS;
        markNetworkDirty();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    public void syncData() {
        Level lvl = this.level;
        if (!(lvl instanceof ServerLevel server)) return;
        LevelChunk chunk = server.getChunkAt(getBlockPos());
        server.getChunkSource().chunkMap
                .getPlayers(chunk.getPos(), false)
                .forEach(p -> p.connection.send(getUpdatePacket()));
    }

    private void pulseLit(int ticks) {
        if (ticks > litTicks) litTicks = ticks;
    }

    private void propagateLit(ServerLevel server, int ticks) {
        Queue<BlockPos> q = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        q.add(this.worldPosition);
        seen.add(this.worldPosition);

        while (!q.isEmpty()) {
            BlockPos at = q.remove();

            for (Direction d : Direction.values()) {
                BlockPos nb = at.relative(d);
                BlockState nState = server.getBlockState(nb);
                if (!(nState.getBlock() instanceof BioConduitBlock)) continue;

                if (!((BioConduitBlock) server.getBlockState(at).getBlock()).isActuallyConnected(server, at, d)) continue;
                if (!((BioConduitBlock) nState.getBlock()).isActuallyConnected(server, nb, d.getOpposite())) continue;

                if (seen.add(nb)) {
                    BlockEntity nBe = server.getBlockEntity(nb);
                    if (nBe instanceof BioConduitBlockEntity nConduit) {
                        nConduit.pulseLit(ticks);
                    }
                    q.add(nb);
                }
            }
        }
    }

    private void markNetworkDirty() {
        connectionCache = null;
        extractingConnectionCache = null;
        invalidateCountdown = 0;
    }

    private List<Connection> getInsertConnections(ServerLevel server) {
        if (connectionCache == null) {
            updateConnectionCache(server);
        }
        return connectionCache == null ? Collections.emptyList() : connectionCache;
    }

    private void updateConnectionCache(ServerLevel server) {
        Block block = getBlockState().getBlock();
        if (!(block instanceof BioConduitBlock)) {
            connectionCache = null;
            return;
        }
        if (!isAnyExtracting()) {
            connectionCache = Collections.emptyList();
            return;
        }

        Map<DirectionalPos, Connection> inserts = new HashMap<>();
        Map<BlockPos, Integer> queue = new HashMap<>();
        Set<BlockPos> visited = new HashSet<>();

        addToQueue(server, worldPosition, 1, queue, visited, inserts);

        while (!queue.isEmpty()) {
            Iterator<Map.Entry<BlockPos, Integer>> it = queue.entrySet().iterator();
            Map.Entry<BlockPos, Integer> e = it.next();
            BlockPos p = e.getKey();
            int dist = e.getValue();
            it.remove();
            visited.add(p);
            addToQueue(server, p, dist, queue, visited, inserts);
        }

        connectionCache = new ArrayList<>(inserts.values());
    }

    private void addToQueue(ServerLevel server, BlockPos at, int distance,
                            Map<BlockPos, Integer> queue, Set<BlockPos> visited,
                            Map<DirectionalPos, Connection> inserts) {
        BlockState stateAt = server.getBlockState(at);
        Block blockAt = stateAt.getBlock();
        if (!(blockAt instanceof BioConduitBlock conduitAt)) return;

        for (Direction d : Direction.values()) {
            if (!conduitAt.isActuallyConnected(server, at, d)) continue;

            BlockPos neighbor = at.relative(d);
            BlockState nState = server.getBlockState(neighbor);
            Block nBlock = nState.getBlock();
            boolean neighborIsConduit = nBlock instanceof BioConduitBlock;

            if (!neighborIsConduit) {
                DirectionalPos dp = new DirectionalPos(neighbor, d.getOpposite());
                Connection c = new Connection(server, dp.pos, dp.facing, distance);
                IEnergyStorage to = c.getEnergyHandler();
                if (to != null && to.canReceive() && !isExtracting(d)) {
                    Connection existing = inserts.get(dp);
                    if (existing == null || existing.getDistance() > distance) {
                        inserts.put(dp, c);
                    }
                }
            } else {
                if (!((BioConduitBlock) nBlock).isActuallyConnected(server, neighbor, d.getOpposite())) continue;
                if (!visited.contains(neighbor) && !queue.containsKey(neighbor)) {
                    queue.put(neighbor, distance + 1);
                }
            }
        }
    }

    @Nullable
    private Connection[] getExtractingConnectionCache(ServerLevel server) {
        if (extractingConnectionCache == null) {
            updateExtractingConnectionCache(server);
        }
        return extractingConnectionCache;
    }

    private void updateExtractingConnectionCache(ServerLevel server) {
        extractingConnectionCache = new Connection[Direction.values().length];

        for (Direction d : Direction.values()) {
            int i = d.get3DDataValue();

            if (!isExtracting(d) || isDisconnected(d)) {
                extractingConnectionCache[i] = null;
                continue;
            }

            BlockState selfState = getBlockState();
            if (!(selfState.getBlock() instanceof BioConduitBlock conduit)
                    || !conduit.isActuallyConnected(server, worldPosition, d)) {
                extractingConnectionCache[i] = null;
                continue;
            }

            BlockPos srcPos = worldPosition.relative(d);
            Connection conn = new Connection(server, srcPos, d.getOpposite(), 1);

            IEnergyStorage src = conn.getEnergyHandler();
            if (src == null || !src.canExtract()) {
                extractingConnectionCache[i] = null;
                continue;
            }

            extractingConnectionCache[i] = conn;
        }
    }

    private record DirectionalPos(BlockPos pos, Direction facing) { }

    public static class Connection {
        private final int distance;
        private final BlockCapabilityCache<IEnergyStorage, Direction> energy;

        public Connection(ServerLevel level, BlockPos pos, Direction face, int distance) {
            this.distance = distance;
            this.energy = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, level, pos, face);
        }

        @Nullable
        public IEnergyStorage getEnergyHandler() {
            return energy.getCapability();
        }

        public int getDistance() {
            return distance;
        }
    }
}