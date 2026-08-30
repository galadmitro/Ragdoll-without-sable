package com.example.ragdoll;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ExampleMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ExampleModClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ExampleMod.LOGGER.info("HELLO FROM CLIENT SETUP");
        if (Minecraft.getInstance().getUser() != null) {
            ExampleMod.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}