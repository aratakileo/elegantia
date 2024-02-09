package io.github.aratakileo.elegantia;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.screen.ConfigScreen;

import java.util.HashMap;
import java.util.Map;

public class ModMenuGetter implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreen.of(Elegantia.ElegantiaConfig.class, parent);
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return new HashMap<>() {{
            Config.forEach((configClass, configInfo) -> put(
                    configInfo.modId(),
                    parent -> configInfo.instance().getScreen(parent)
            ));
        }};
    }
}
