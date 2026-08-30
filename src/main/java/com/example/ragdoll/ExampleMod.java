package com.example.ragdoll;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "ragdoll";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public ExampleMod(IEventBus modEventBus) {
    }
}