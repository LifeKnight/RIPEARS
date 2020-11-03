package com.lifeknight.ripears.mod;

import com.lifeknight.ripears.gui.Manipulable;
import com.lifeknight.ripears.gui.hud.EnhancedHudText;
import com.lifeknight.ripears.utilities.Chat;
import com.lifeknight.ripears.utilities.Utilities;
import com.lifeknight.ripears.variables.SmartBoolean;
import com.lifeknight.ripears.variables.SmartNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Timer;
import java.util.TimerTask;

import static net.minecraft.util.EnumChatFormatting.DARK_RED;
import static net.minecraft.util.EnumChatFormatting.WHITE;

@net.minecraftforge.fml.common.Mod(modid = Core.MOD_ID, name = Core.MOD_NAME, version = Core.MOD_VERSION, clientSideOnly = true)
public class Core {
    public static final String
            MOD_NAME = "R.I.P. EARS",
            MOD_VERSION = "1.0",
            MOD_ID = "ripears";
    public static final EnumChatFormatting MOD_COLOR = DARK_RED;
    public static boolean onHypixel = false;
    public static GuiScreen guiToOpen = null;
    public static final SmartBoolean runMod = new SmartBoolean("Mod", "Main", true, "Determines if the mod is enabled or not.") {
        @Override
        public void onSetValue() {
            if (!this.getValue()) {
                Core.restoreOriginalVolume();
            }
        }
    };
    public static final SmartNumber.SmartFloat increaseRate = new SmartNumber.SmartFloat("Increase Rate", "Main", 0.0005F, 0.00001F, 0.01F, "The rate at which the volume increases.");
    public static final SmartBoolean exponential = new SmartBoolean("Exponential", "Main", false, "The volume will increase at an exponential rate as opposed to a constant one.");
    public static final SmartBoolean showHud = new SmartBoolean("Show HUD", "HUD", false, "Show the current volume on screen.");
    public static final SmartBoolean hudTextShadow = new SmartBoolean("HUD Text Shadow", "HUD", false, "Shadow behind text shown in the HUD.");
    public static final SmartBoolean hudTextBox = new SmartBoolean("HUD Text Box", "HUD", true, "Box surrounding text shown in the HUD.");
    public static final SmartNumber.SmartFloat hudTextBoxOpacity = new SmartNumber.SmartFloat("HUD Text Box Opacity", "HUD", 0.7F, 0F, 1F, "Opacity of the HUD Text Box.");
    public static final SmartNumber.SmartFloat chromaSpeed = new SmartNumber.SmartFloat("Chroma Speed", "HUD", 0.5F, 0.125F, 1F, "Speed of the chroma speed of text in the HUD.");

    private static float volume;
    public static float originalVolume;

    private static boolean shouldIncrease = false;

    public static Configuration configuration;

    static {
        increaseRate.setiCustomDisplayString(objects -> {
            float value = (float) objects[0];
            return "Increase Rate: " + Utilities.shortenDouble(value * 100, 2) + "%";
        });
        hudTextBoxOpacity.setiCustomDisplayString(objects -> {
            float value = (float) objects[0];
            return "HUD Text Box Opacity: " + (int) (value * 100) + "%";
        });
        chromaSpeed.setiCustomDisplayString(objects -> {
            float value = (float) objects[0];
            return "Chroma Speed: " + (int) (value * 100) + "%";
        });
    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ModCommand());

        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;

        gameSettings.setSoundLevel(SoundCategory.MASTER, Math.min(gameSettings.getSoundLevel(SoundCategory.MASTER), 1F));

        originalVolume = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);
        volume = originalVolume;

        new EnhancedHudText("Status", 0, 0, "Volume") {
            @Override
            public String getTextToDisplay() {
                return (int) (volume * 100) + "%";
            }

            @Override
            public boolean isVisible() {
                return showHud.getValue() && shouldIncrease;
            }
        };

        configuration = new Configuration();
    }

    @SubscribeEvent
    public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Chat.sendQueuedChatMessages();
                onHypixel = !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
                if (!onHypixel) shouldIncrease = false;
            }
        }, 1000);
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (runMod.getValue() && onHypixel) {
            String message = Utilities.removeFormattingCodes(event.message.getFormattedText());

            if (message.equals("Cages opened! FIGHT!")) {
                shouldIncrease = true;
            } else if (message.toLowerCase().contains("(win)") || message.toLowerCase().contains(" win ")) {
                shouldIncrease = false;
                volume = originalVolume;
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (runMod.getValue() && shouldIncrease && Minecraft.getMinecraft().inGameHasFocus) {
                if (exponential.getValue()) {
                    volume += increaseRate.getValue() * volume;
                } else {
                    volume += increaseRate.getValue();
                }
                Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MASTER, volume);
            }

            if (guiToOpen != null) {
                Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
                guiToOpen = null;
            }

            if (runMod.getValue()) Manipulable.renderManipulables();
        }
    }

    public static void openGui(GuiScreen guiScreen) {
        guiToOpen = guiScreen;
    }

    public static void restoreOriginalVolume() {
        Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MASTER, originalVolume);
        volume = originalVolume;
    }
}