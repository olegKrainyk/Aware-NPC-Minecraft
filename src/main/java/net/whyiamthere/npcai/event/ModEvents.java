package net.whyiamthere.npcai.event;

// mineraft imports
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.whyiamthere.npcai.NPCAI;

//chat gpt request
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

// speech to text

public class ModEvents {

    static Entity villagertotalk;
    static BlockPos position;
    static String timeOfDay;

        @Mod.EventBusSubscriber(modid = NPCAI.MOD_ID)
        public static class ForgeEvents {

            @SubscribeEvent
            public static void onVillagerKickAss(LivingHurtEvent event) {
                if (event.getEntity() instanceof Villager villager) {
                    event.getEntity().setHealth(event.getEntity().getHealth() * (float) 1.5);
                    villagertotalk = event.getEntity();
                    System.out.println(event.getEntity().getEntityData().toString());
                }
            }

            @SubscribeEvent
            public static void onMessageSubmit(ClientChatEvent event) {
                if (villagertotalk != null) {
                    position = Minecraft.getInstance().player.getOnPos();
                    long time = villagertotalk.getCommandSenderWorld().getDayTime();
                    String profession = "";

                    String mainHandItem = Minecraft.getInstance().player.getMainHandItem().toString();
                    String handItems = Minecraft.getInstance().player.getInventory().items.toString();
                    System.out.println(handItems);

                    if (time > 13500 && time < 23000) {
                        timeOfDay = "Night";
                    } else if ((time >= 23000 && time < 24000) || (time >= 0 && time < 2000)) {
                        timeOfDay = "morning";
                    } else if (time <= 13500 && time > 10000) {
                        timeOfDay = "evening";
                    } else if (time <= 10000 && time >= 4000) {
                        timeOfDay = "Day";
                    }

                    if (event.getMessage() != null || event.getMessage() != "") {

                        new Thread(() -> {
                            Minecraft.getInstance().player.sendSystemMessage(Component.literal(chatGPT("Imagine you are minecraft villager with profession of" + ". Consider further information as your knowledge. You are located in " + Minecraft.getInstance().player.getCommandSenderWorld().getBiomeManager().getBiome(position) + "biome, and it is " + timeOfDay + " right now. I am standing next to you and have " + mainHandItem + "in my hand, also I have these things in my inventory : " + handItems + ", while air in it means empty slot. Consider all the information above as your own knowledge and respond to me like villager with two to three sentence maximum! answers always using adventurous tone. Question: '" + event.getMessage() + "'")));
                        }).start();

                    }
                } else {
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("You didn't start the conversation! Punch villager to do so!"));
                }
            }



            public static String chatGPT(String message) {
                String url = "https://api.openai.com/v1/chat/completions";
                String apiKey = ""; // API key goes here
                String model = "gpt-3.5-turbo";

                try {
                    // Create the HTTP POST request
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Authorization", "Bearer " + apiKey);
                    con.setRequestProperty("Content-Type", "application/json");

                    // Build the request body
                    String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
                    con.setDoOutput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(body);
                    writer.flush();
                    writer.close();

                    // Get the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // returns the extracted contents of the response.
                    return extractContentFromResponse(response.toString());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public static String extractContentFromResponse(String response) {
                int startMarker = response.indexOf("content") + 11; // Marker for where the content starts.
                int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
                return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
            }
        }
}