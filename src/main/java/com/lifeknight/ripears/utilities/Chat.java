package com.lifeknight.ripears.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.lifeknight.ripears.mod.Core.MOD_COLOR;
import static com.lifeknight.ripears.mod.Core.MOD_NAME;
import static net.minecraft.util.EnumChatFormatting.*;

public enum Chat {
    NORMAL(""),
    ALL("/allchat"),
    PARTY("/partychat"),
    GUILD("/guildchat"),
    SHOUT("/shout"),
    REPLY("/reply");

    private final String prefix;
    Chat(String prefix) {
        this.prefix = prefix;
    }

    public static final List<String> queuedMessages = new ArrayList<>();

    public static void addChatMessage(String message) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(MOD_COLOR + "" + EnumChatFormatting.BOLD + MOD_NAME + " > " + EnumChatFormatting.RESET + message));
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    addChatMessage(message);
                }
            }, 100L);
        }
    }
    public static void addCommandUsageMessage(String message) {
        addChatMessage(DARK_GREEN + message);
    }

    public static void addErrorMessage(String message) {
        addChatMessage(RED + message);
    }

    public static void addSuccessMessage(String message) {
        addChatMessage(GREEN + message);
    }

    public static void addChatMessageWithoutName(String message) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    addChatMessageWithoutName(message);
                }
            }, 100L);
        }
    }

    public static void sendChatMessage(String message, Chat chatType) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(chatType.prefix + message);
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sendChatMessage(message, chatType);
                }
            }, 100L);
        }

    }

    public static void queueChatMessageForConnection(String message) {
        queuedMessages.add(message);
    }

    public static void sendQueuedChatMessages() {
        for (String queuedMessage : queuedMessages) {
            Chat.addChatMessage(queuedMessage);
        }
        queuedMessages.clear();
    }
}
