package io.github.aratakileo.elegantia;

import com.google.gson.annotations.SerializedName;
import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigField;
import io.github.aratakileo.elegantia.util.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Elegantia.class);
    public final static String MODID = "elegantia";

    static public class ElegantiaConfig extends Config {
        @ConfigField
        public String testText = "我操你妈";

        @ConfigField(translationKey = "eee")
        public boolean testBoolean = false;

        @SerializedName("super_turbo_fast_boolean")
        @ConfigField
        public boolean testBoolean2 = false;
    }

    @Override
    public void onInitializeClient() {
        Config.init(ElegantiaConfig.class, Elegantia.MODID);
    }
}
