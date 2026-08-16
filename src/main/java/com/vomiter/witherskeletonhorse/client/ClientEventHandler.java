package com.vomiter.witherskeletonhorse.client;

import net.minecraftforge.eventbus.api.IEventBus;

public class ClientEventHandler {
    public static void init(IEventBus modBus){
        modBus.addListener(WitherSkeletonHorseRenderer::register);
    }}
