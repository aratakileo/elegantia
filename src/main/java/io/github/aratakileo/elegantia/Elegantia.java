package io.github.aratakileo.elegantia;

import com.google.gson.annotations.SerializedName;
import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigField;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Elegantia.class);

    static public class ElegantiaConfig extends Config {
        @ConfigField
        public String testText = "我操你妈";

        @ConfigField(description = "I was too lazy to set title to this field")
        public boolean testBoolean = false;

        @SerializedName("super_turbo_fast_boolean")
        @ConfigField(title = "Test boolean 2")
        public boolean testBoolean2 = false;
    }

    @Override
    public void onInitializeClient() {
        Config.init(ElegantiaConfig.class, "elegantia");
    }
}
