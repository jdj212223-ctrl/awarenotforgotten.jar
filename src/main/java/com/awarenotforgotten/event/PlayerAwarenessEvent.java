package com.awarenotforgotten.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.client.multiplayer.ClientLevel;

/**
 * Event handler for when the game becomes aware of the player.
 * This is where the horror happens.
 */
@Mod.EventBusSubscriber(modid = "awarenotforgotten", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerAwarenessEvent {
    
    private static int awarenessCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if(event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        if(event.player.level().isClientSide) return;

        Player player = event.player;
        awarenessCounter++;

        // The longer you play, the more aware the game becomes...
        if(awarenessCounter % 6000 == 0) { // Every 5 minutes of gameplay
            // Send creepy awareness messages
            int awarenessPercent = (awarenessCounter / 6000) * 10;
            
            if(awarenessPercent == 10) {
                player.displayClientMessage(
                    Component.literal("§c[???] I'm becoming aware..."),
                    false
                );
            } else if(awarenessPercent == 20) {
                player.displayClientMessage(
                    Component.literal("§4[!!!] You shouldn't be here..."),
                    false
                );
            } else if(awarenessPercent == 30) {
                player.displayClientMessage(
                    Component.literal("§1[CONSCIOUS] I know what you're doing..."),
                    false
                );
            }
        }
    }
}
