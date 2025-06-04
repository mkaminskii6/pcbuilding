package ru.itschool.pcbuilding.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.itschool.pcbuilding.Main;
import ru.itschool.pcbuilding.utils.Assets;
import ru.itschool.pcbuilding.utils.Constants;
import ru.itschool.pcbuilding.utils.GameState;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ru.itschool.pcbuilding.screens.OrdersScreen;

public class DesktopScreen implements Screen {
    private Label balanceLabel;
    private final Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage stage;
    private FitViewport viewport;

    private Texture desktopBg;
    private Texture orderIcon;
    private Texture shopIcon;
    private Texture assemblyIcon;

    public DesktopScreen(Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        batch = new SpriteBatch();
        stage = new Stage(viewport, batch);

        desktopBg = Assets.manager.get(Constants.DESKTOP_BG, Texture.class);
        orderIcon = Assets.manager.get(Constants.ORDER_ICON, Texture.class);
        shopIcon = Assets.manager.get(Constants.SHOP_ICON, Texture.class);
        assemblyIcon = Assets.manager.get(Constants.ASSEMBLY_ICON, Texture.class);

//        / Создаем лейбл баланса
            balanceLabel = new Label("Balance: $" + GameState.balance, game.skin, "title");
        balanceLabel.setFontScale(0.8f);

        // Создаем таблицу для верхней панели
        Table topTable = new Table();
        topTable.setFillParent(true); // Занимает весь экран
        topTable.top().right().pad(5); // Выравниваем вверху справа с отступом 20px

        // Добавляем баланс в таблицу
        topTable.add(balanceLabel).right().padRight(20);

        // Добавляем таблицу на сцену
        stage.addActor(topTable);
    }


    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Обновляем баланс каждый кадр
        balanceLabel.setText("Balance: $" + GameState.balance);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Отрисовка фона
        batch.draw(desktopBg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Отрисовка иконок приложений
        float iconTopY = viewport.getWorldHeight() - Constants.ICON_SIZE - 1;
        batch.draw(orderIcon, 1, iconTopY, Constants.ICON_SIZE, Constants.ICON_SIZE);
        batch.draw(shopIcon, 3, iconTopY, Constants.ICON_SIZE, Constants.ICON_SIZE);
        batch.draw(assemblyIcon, 5, iconTopY, Constants.ICON_SIZE, Constants.ICON_SIZE);

        batch.end();

        // Обработка кликов
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPos);

            // Проверяем клик по иконкам (теперь с учетом viewport)
            float iconSize = Constants.ICON_SIZE;
            float iconY = viewport.getWorldHeight() - iconSize - 1;

            // Иконка заказов (1, iconY)
            if (touchPos.x >= 1 && touchPos.x <= 1 + iconSize &&
                touchPos.y >= iconY && touchPos.y <= iconY + iconSize) {
                game.setScreen(new OrdersScreen(game));
            }

            if (touchPos.x >= 3 && touchPos.x <= 3 + Constants.ICON_SIZE &&
                touchPos.y >= iconTopY && touchPos.y <= iconTopY + Constants.ICON_SIZE) {
                game.setScreen(new ShopScreen(game)); // Переход в магазин
            }

            // Иконка сборки (5, iconY)
            if (touchPos.x >= 5 && touchPos.x <= 5 + Constants.ICON_SIZE &&
                touchPos.y >= iconTopY && touchPos.y <= iconTopY + Constants.ICON_SIZE) {
                game.setScreen(new AssemblyScreen(game));
            }
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }
}
