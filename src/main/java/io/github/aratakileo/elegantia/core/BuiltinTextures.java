package io.github.aratakileo.elegantia.core;

import io.github.aratakileo.elegantia.client.graphics.drawable.ElasticTextureDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.util.type.InitOnGet;

public interface BuiltinTextures {
    TextureProvider MINECRAFT_PROVIDER = Namespace.MINECRAFT.getTextureProvider(),
            ELEGANTIA_PROVIDER = Namespace.ELEGANTIA.getTextureProvider();

    InitOnGet<TextureDrawable> SLOT_BACKGROUND = MINECRAFT_PROVIDER.defineGui("sprites/container/slot.png"),
            MINECRAFT_BUTTON = MINECRAFT_PROVIDER.defineGui("sprites/widget/button.png"),
            MINECRAFT_BUTTON_DISABLED = MINECRAFT_PROVIDER.defineGui("sprites/widget/button_disabled.png"),
            MINECRAFT_BUTTON_FOCUSED = MINECRAFT_PROVIDER.defineGui("sprites/widget/button_highlighted.png"),
            BUCKET_SLOT_ICON = ELEGANTIA_PROVIDER.defineSlotIcon("bucket.png"),
            BLOCK_SLOT_ICON = ELEGANTIA_PROVIDER.defineSlotIcon("block.png"),
            COAL_SLOT_ICON = ELEGANTIA_PROVIDER.defineSlotIcon("coal.png"),
            LEAF_SLOT_ICON = ELEGANTIA_PROVIDER.defineSlotIcon("leaf.png"),
            RED_CROSS_ICON = ELEGANTIA_PROVIDER.defineGuiIcon("red_cross.png"),
            DEFAULT_BLOCK_INVENTORY = ELEGANTIA_PROVIDER.defineGui("default_block_inventory.png");

    InitOnGet<ElasticTextureDrawable> ELASTIC_SLOT_BACKGROUND = ElasticTextureDrawable.of(SLOT_BACKGROUND, 1),
            ELASTIC_MINECRAFT_BUTTON = ElasticTextureDrawable.of(MINECRAFT_BUTTON, 3),
            ELASTIC_MINECRAFT_BUTTON_DISABLED = ElasticTextureDrawable.of(MINECRAFT_BUTTON_DISABLED, 1),
            ELASTIC_MINECRAFT_BUTTON_FOCUSED = ElasticTextureDrawable.of(MINECRAFT_BUTTON_FOCUSED, 3);
}
