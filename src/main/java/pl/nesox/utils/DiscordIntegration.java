package pl.nesox.utils;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import pl.nesox.TxtMaker;

public class DiscordIntegration {
    private static DiscordRPC rpc = DiscordRPC.INSTANCE;


    //metoda która startuje status na discordzie
    public static void startRPC() {
        String applicationId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize(applicationId, handlers, true, null);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.largeImageKey = "";
        presence.largeImageText = "TxtMaker";
        rpc.Discord_UpdatePresence(presence);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                presence.details = "Tworze wspaniałego txtpacka";
                presence.state = "Aktualnie zrobiłem już " + TxtMaker.variables.getInt("valueOfCreatedTxT", 0) + " TxTPacków";
                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }


    //metoda która zatrzymuje status na discordzie
    public static void stopRPC() {
        rpc.Discord_Shutdown();
        rpc.Discord_ClearPresence();
    }

}