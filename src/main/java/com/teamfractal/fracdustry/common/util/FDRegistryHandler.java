package com.teamfractal.fracdustry.common.util;

import com.teamfractal.fracdustry.common.Fracdustry;
import com.teamfractal.fracdustry.common.block.init.FDBlocks;
import com.teamfractal.fracdustry.common.item.init.FDItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FDRegistryHandler
{
    public static final DeferredRegister<Item> Items = DeferredRegister.create(ForgeRegistries.ITEMS, Fracdustry.MODID);
    public static final DeferredRegister<Block> Blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, Fracdustry.MODID);
    public static final DeferredRegister<SoundEvent> Sounds = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Fracdustry.MODID);

    public static void register()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        FDItems.register();
        FDBlocks.register();

        Items.register(eventBus);
        Blocks.register(eventBus);
    }
}
