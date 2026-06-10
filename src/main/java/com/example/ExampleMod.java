package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
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
                
                if (server != null) {
                    server.getAllLevels().forEach(level -> {
                        List<ServerPlayer> players = level.players();
                        if (!players.isEmpty()) {
                            ServerPlayer target = players.get(RANDOM.nextInt(players.size()));
                            String targetName = target.getScoreboardName();
                            
                            int offsetX = (RANDOM.nextBoolean() ? 1 : -1) * (20 + RANDOM.nextInt(11));
                            int offsetZ = (RANDOM.nextBoolean() ? 1 : -1) * (20 + RANDOM.nextInt(11));
                            
                            double x = target.getX() + offsetX;
                            double y = target.getY();
                            double z = target.getZ() + offsetZ;

                            String summonCmd = String.format(
                                "execute at %s run summon zombie %.2f %.2f %.2f {CustomNameVisible:1b,Attributes:[{Name:\"generic.max_health\",Base:20.0},{Name:\"generic.movement_speed\",Base:0.3}],CustomName:'{\"text\":\"%s_mom\"}'}",
                                targetName, x, y, z, targetName
                            );
                            
                            String msgCmd = String.format(
                                "tellraw @a {\"text\":\"\",\"extra\":[{\"text\":\"%s_mom joined the game\",\"color\":\"yellow\"}]}",
                                targetName
                            );

                            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), summonCmd);
                            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), msgCmd);
                        }
                    });
                }
            }
        });
    }
}
