package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

import java.util.List;
import java.util.Random;

public class ExampleMod implements ModInitializer {
    private static int timer = 0;
    private static final Random RANDOM = new Random();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            timer++;
            // 300 тиков = 15 секунд (для быстрой проверки)
            if (timer >= 300) { 
                timer = 0;
                
                server.getAllLevels().forEach(level -> {
                    List<ServerPlayer> players = level.players();
                    if (!players.isEmpty()) {
                        ServerPlayer target = players.get(RANDOM.nextInt(players.size()));
                        String targetName = target.getScoreboardName();
                        
                        int offsetX = (RANDOM.nextBoolean() ? 1 : -1) * (20 + RANDOM.nextInt(11));
                        int offsetZ = (RANDOM.nextBoolean() ? 1 : -1) * (20 + RANDOM.nextInt(11));
                        BlockPos spawnPos = target.blockPosition().offset(offsetX, 0, offsetZ);

                        if (level.isEmptyBlock(spawnPos) && level.isEmptyBlock(spawnPos.above())) {
                            // Самый надежный способ спауна без лишних импортов параметров
                            Zombie mom = new Zombie(EntityType.ZOMBIE, level);
                            if (mom != null) {
                                mom.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
                                mom.setCustomName(Component.literal(targetName + "_mom"));
                                mom.setCustomNameVisible(true);
                                level.addFreshEntity(mom);
                                
                                server.getPlayerList().broadcastSystemMessage(
                                    Component.literal("§e" + targetName + "_mom joined the game"), false
                                );
                            }
                        }
                    }
                });
            }
        });
    }
}
