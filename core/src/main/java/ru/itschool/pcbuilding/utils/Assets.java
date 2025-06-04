package ru.itschool.pcbuilding.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public static AssetManager manager;

    public static void load() {
        manager = new AssetManager();
        // Загружаем текстуры
        manager.load("images/desktop_bg.png", Texture.class);
        manager.load("images/parts/cpu.png", Texture.class);
        manager.load("images/parts/case.png", Texture.class);
        manager.load("images/parts/gpu.png", Texture.class);
        manager.load("images/parts/ram.png", Texture.class);
        manager.load("images/icons/assembly_icon.png", Texture.class);
        manager.load("images/icons/order_icon.png", Texture.class);
        manager.load("images/icons/shop_icon.png", Texture.class);
        manager.finishLoading();  // Блокирующая загрузка (для простоты)
    }

    public static Texture getTexture(String name) {
        return manager.get("images/parts/" + name + ".png", Texture.class);
    }

    public static void dispose() {
        manager.dispose();
    }
}
