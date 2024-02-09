package io.github.aratakileo.elegantia;

import com.google.gson.annotations.SerializedName;
import io.github.aratakileo.elegantia.gui.config.Action;
import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigField;
import io.github.aratakileo.elegantia.gui.config.Trigger;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Elegantia.class);
    public final static String MODID = "elegantia";

    static public class ElegantiaConfig extends Config {
        @ConfigField
        public transient String testText = "我操你妈";

        @Trigger("debug")
        @ConfigField(translationKey = "eee")
        public boolean testBoolean = false;

        @SerializedName("super_turbo_fast_boolean")
        @ConfigField(triggeredBy = "debug")
        public boolean testBoolean2 = false;

        @ConfigField(triggeredBy = "debug", translationKey = "eee")
        public void smthAction() {
            LOGGER.info("You clicked smth action");
        }

        @ConfigField
        public static void test() {
            LOGGER.info("Really???");
        }
    }

    @Override
    public void onInitializeClient() {
        Config.init(ElegantiaConfig.class, Elegantia.MODID);
    }
}
