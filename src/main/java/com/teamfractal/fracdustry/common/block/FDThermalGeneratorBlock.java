package com.teamfractal.fracdustry.common.block;

import com.teamfractal.fracdustry.common.blockentity.FDThermalGeneratorBlockEntity;
import com.teamfractal.fracdustry.common.itemGroup.FDGroupInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

//Thank you mcjty!
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FDThermalGeneratorBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final String NAME = "fracdustry:thermal_generator";

    @ObjectHolder(NAME)
    public static FDThermalGeneratorBlock BLOCK;

    //Basic Block Properties
    public FDThermalGeneratorBlock () {
        super(Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .lightLevel(state -> state.getValue(BlockStateProperties.POWERED) ? 14 : 0)
                .strength(2.0f).isValidSpawn(FDThermalGeneratorBlock::never).isRedstoneConductor(FDThermalGeneratorBlock::never)
                .isSuffocating(FDThermalGeneratorBlock::never).isViewBlocking(FDThermalGeneratorBlock::never).noOcclusion());
    }

    @SubscribeEvent
    public static void onRegisterBlock(@Nonnull RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new FDThermalGeneratorBlock().setRegistryName(NAME));
    }

    //Register BlockItem
    @SubscribeEvent
    public static void onRegisterItem(@Nonnull RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BlockItem(BLOCK, new Item.Properties().tab(FDGroupInit.fdmachinery)).setRegistryName(NAME));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FDThermalGeneratorBlockEntity(pos, state);
    }

    //Connect BlockEntity Ticking
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        } else {
            return (level1, pos, state1, tile) -> {
                if (tile instanceof FDThermalGeneratorBlockEntity generator) {
                    generator.tickServer(state1);
                }
            };
        }
    }

    //These 4 func were introduced by arrokoth, I currently don't know what they are implementing XD
    private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType)
    {
        return false;
    }

    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_)
    {
        return false;
    }

    private static boolean always(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType)
    {
        return true;
    }

    private static boolean always(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_)
    {
        return true;
    }


    //Blockstate:facing and powered
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED);
    }

    //Four situations of hitbox when block being placed. A bit complex for it's not a standard 1x1x1 block
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context)
    {
        Direction d = state.getValue(FACING);
        switch (d) {
            case SOUTH :
            default :
                return Shapes.or(Block.box(5, 0, 12.5, 11, 8, 15.5), Block.box(11.5, 0, 12.5, 13.5, 10.75, 14),
                        Block.box(2.5, 0, 12.5, 4.5, 10.75, 14), Block.box(8.5, 8, 12.5, 10.5, 10.5, 15),
                        Block.box(5.5, 8, 12.5, 7.5, 10.5, 15), Block.box(1, 0, 1.5, 15, 12, 12.5),
                        Block.box(15, 4, 4, 16, 12, 12), Block.box(0, 4, 4, 1, 12, 12),
                        Block.box(1.75, 0.25, 0.5, 14.5, 8.75, 1.5), Block.box(1.75, 8.75, 0.5, 8.5, 11.5, 1.5),
                        Block.box(5, 12, 4.5, 10, 14, 9.5), Block.box(9.25, 12.25, 0.25, 14, 13.25, 3),
                        Block.box(9.25, 9.5, 0.25, 14, 12.25, 1.5));
            case NORTH :
                return Shapes.or(Block.box(5, 0, 0.5, 11, 8, 3.5), Block.box(2.5, 0, 2, 4.5, 10.75, 3.5),
                        Block.box(11.5, 0, 2, 13.5, 10.75, 3.5), Block.box(5.5, 8, 1, 7.5, 10.5, 3.5),
                        Block.box(8.5, 8, 1, 10.5, 10.5, 3.5), Block.box(1, 0, 3.5, 15, 12, 14.5),
                        Block.box(0, 4, 4, 1, 12, 12), Block.box(15, 4, 4, 16, 12, 12),
                        Block.box(1.5, 0.25, 14.5, 14.25, 8.75, 15.5), Block.box(7.5, 8.75, 14.5, 14.25, 11.5, 15.5),
                        Block.box(6, 12, 6.5, 11, 14, 11.5), Block.box(2, 12.25, 13, 6.75, 13.25, 15.75),
                        Block.box(2, 9.5, 14.5, 6.75, 12.25, 15.75));
            case EAST :
                return Shapes.or(Block.box(12.5, 0, 5, 15.5, 8, 11), Block.box(12.5, 0, 2.5, 14, 10.75, 4.5),
                        Block.box(12.5, 0, 11.5, 14, 10.75, 13.5), Block.box(12.5, 8, 5.5, 15, 10.5, 7.5),
                        Block.box(12.5, 8, 8.5, 15, 10.5, 10.5), Block.box(1.5, 0, 1, 12.5, 12, 15),
                        Block.box(4, 4, 0, 12, 12, 1), Block.box(4, 4, 15, 12, 12, 16),
                        Block.box(0.5, 0.25, 1.5, 1.5, 8.75, 14.25), Block.box(0.5, 8.75, 7.5, 1.5, 11.5, 14.25),
                        Block.box(4.5, 12, 6, 9.5, 14, 11), Block.box(0.25, 12.25, 2, 3, 13.25, 6.75),
                        Block.box(0.25, 9.5, 2, 1.5, 12.25, 6.75));
            case WEST :
                return Shapes.or(Block.box(0.5, 0, 5, 3.5, 8, 11), Block.box(2, 0, 11.5, 3.5, 10.75, 13.5),
                        Block.box(2, 0, 2.5, 3.5, 10.75, 4.5), Block.box(1, 8, 8.5, 3.5, 10.5, 10.5),
                        Block.box(1, 8, 5.5, 3.5, 10.5, 7.5), Block.box(3.5, 0, 1, 14.5, 12, 15),
                        Block.box(4, 4, 15, 12, 12, 16), Block.box(4, 4, 0, 12, 12, 1),
                        Block.box(14.5, 0.25, 1.75, 15.5, 8.75, 14.5), Block.box(14.5, 8.75, 1.75, 15.5, 11.5, 8.5),
                        Block.box(6.5, 12, 5, 11.5, 14, 10), Block.box(13, 12.25, 9.25, 15.75, 13.25, 14),
                        Block.box(14.5, 9.5, 9.25, 15.75, 12.25, 14));

        }
    }

    //Decides the block's facing direction, and normally it's off when being placed
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()).setValue(BlockStateProperties.POWERED, false);
    }

    //BlockItem's tooltip
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader, List<Component> list, TooltipFlag flags) {
        list.add(new TranslatableComponent("tooltips.fracdustry.thermal_generator"));
    }

    //Right-click interaction: opens a GUI with container
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof FDThermalGeneratorBlockEntity) {
                NetworkHooks.openGui((ServerPlayer) player, (FDThermalGeneratorBlockEntity)blockEntity, blockEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Named container provider is missing!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    //Block's particle effect, when it's working
    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState blockstate, Level world, BlockPos pos, Random random) {
        super.animateTick(blockstate, world, pos, random);
        if (blockstate.getValue(BlockStateProperties.POWERED)) {
            world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE
                    , pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5
                    , 0, 0, 0);
        }
    }
    }
