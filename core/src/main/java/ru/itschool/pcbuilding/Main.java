package ru.itschool.pcbuilding;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import ru.itschool.pcbuilding.screens.MainMenuScreen;
import ru.itschool.pcbuilding.utils.Assets;
import ru.itschool.pcbuilding.utils.GameState;

public class Main extends Game {
    public SpriteBatch batch; // Для отрисовки
    public Skin skin;        // Для интерфейса

    @Override
    public void create() {
        batch = new SpriteBatch();

        Assets.load();

        // Загрузка skin (обязательно!)
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        // Проверка
        if (skin == null) {
            Gdx.app.error("Main", "Skin не загружен!");
            return;
        }
        GameState.balance = 2000; // Начальный баланс
        setScreen(new MainMenuScreen(this));

        // Запуск первого экрана
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Важно! Без этого экраны не обновятся
    }

    @Override
    public void dispose() {
        batch.dispose();
        skin.dispose();
        Assets.dispose();
    }
}
