package ru.itschool.pcbuilding.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ru.itschool.pcbuilding.Main;
import ru.itschool.pcbuilding.utils.Assets;

public class MainMenuScreen implements Screen {
    private final Main parent; // Главный класс
    private Stage stage;

    private Texture background;

    public MainMenuScreen(Main main) {
        this.parent = main;
        this.stage = new Stage(new ScreenViewport());

        background = Assets.manager.get("images/desktop_bg.png", Texture.class);


        // Создание кнопки
        TextButton playButton = new TextButton("Start Game", parent.skin);
        playButton.setSize(200, 50);
        playButton.setPosition(
            Gdx.graphics.getWidth()/2f - 100,
            Gdx.graphics.getHeight()/2f - 25
        );

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.setScreen(new DesktopScreen(parent));
            }
        });

        stage.addActor(playButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Рисуем фон
        parent.batch.begin();
        parent.batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        parent.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Для кликов
    }



    @Override
    public void dispose() {
        stage.dispose();
    }


    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}

