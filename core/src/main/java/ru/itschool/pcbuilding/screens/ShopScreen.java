package ru.itschool.pcbuilding.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.itschool.pcbuilding.Main;
import ru.itschool.pcbuilding.utils.Constants;
import ru.itschool.pcbuilding.utils.GameState;

public class ShopScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private ScrollPane scrollPane;
    private Label balanceLabel;

    public ShopScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        this.balanceLabel = new Label("Balance: $" + GameState.balance, skin, "title");

        Gdx.input.setInputProcessor(stage);
        // Добавляем баланс в верхний правый угол
        Table balanceTable = new Table();
        balanceTable.setFillParent(true);
        balanceTable.top().right().pad(20);

        balanceLabel = new Label("Balance: $" + GameState.balance, skin, "title");
        balanceTable.add(balanceLabel);

        stage.addActor(balanceTable);


        setupUI();
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Заголовок
        Label title = new Label("PC Parts Shop", skin, "title");
        title.setAlignment(Align.center);
        mainTable.add(title).colspan(2).padBottom(20).row();

        // Создаем контейнер для прокрутки
        Table itemsTable = new Table();
        itemsTable.defaults().pad(10);

        // Добавляем процессоры
        addComponentCategory(itemsTable, "CPUs", new String[]{
            "Intel Core i5-12400F",
            "AMD Ryzen 5 5600X",
            "Intel Core i9",
            "AMD Ryzen 7 5800X3D"
        }, "cpu");

        // Добавляем видеокарты
        addComponentCategory(itemsTable, "GPUs", new String[]{
            "NVIDIA RTX 3070",
            "AMD RX 6700 XT",
            "NVIDIA RTX 4090",
            "NVIDIA GTX 1660"
        }, "gpu");

        // Добавляем оперативную память
        addComponentCategory(itemsTable, "RAM", new String[]{
            "Corsair Vengeance 16GB DDR4",
            "G.Skill Trident Z 32GB DDR4",
            "Kingston Fury 64GB DDR5",
            "Crucial Ballistix 16GB DDR4"
        }, "ram");

        scrollPane = new ScrollPane(itemsTable, skin);
        scrollPane.setFadeScrollBars(false);
        mainTable.add(scrollPane).expand().fill().colspan(2).row();

        // Кнопка назад
        TextButton backBtn = new TextButton("Back to Desktop", skin);
        backBtn.addListener(e -> {
            if (e.toString().contains("touchDown")) {
                game.setScreen(new DesktopScreen(game));
                return true;
            }
            return false;
        });
        mainTable.add(backBtn).colspan(2).padTop(20).width(200).height(50);
    }

    private void addComponentCategory(Table parent, String categoryName, String[] items, String imageName) {
        // Заголовок категории
        parent.add(new Label(categoryName, skin, "category")).colspan(2).padTop(20).row();

        for (String item : items) {
            // Картинка компонента
            final int price = getPriceForComponent(item);
            Image img = new Image(new Texture(Gdx.files.internal("images/parts/" + imageName + ".png")));
            img.setScaling(Scaling.fit);
            parent.add(img).size(100, 100).padRight(20);

            // Описание и цена
            Table infoTable = new Table();
            infoTable.defaults().left();
            infoTable.add(new Label(item, skin, "bold")).row();
            infoTable.add(new Label("Price: $" + getPriceForComponent(item), skin)).padTop(5);

            parent.add(infoTable).width(200).left();
//           Кнопка Buy
            TextButton buyBtn = new TextButton("Buy", skin);
            buyBtn.addListener(new ClickListener() {
                                   @Override
                                   public void clicked(InputEvent event, float x, float y) {
                                       if (GameState.balance >= price) {
                                           // Анимация покупки
                                           buyBtn.setColor(Color.GREEN);
                                           buyBtn.getLabel().setText("Bought!");

                                           Timer.schedule(new Timer.Task() {
                                               @Override
                                               public void run() {
                                                   buyBtn.setColor(Color.WHITE);
                                                   buyBtn.getLabel().setText("Buy");
                                               }
                                           }, 0.5f);

                                           GameState.spendMoney(price);
                                           GameState.addToInventory(item);
                                           updateBalanceLabel();
                                       } else {
                                           // Анимация недостатка денег
                                           buyBtn.setColor(Color.RED);
                                           Timer.schedule(new Timer.Task() {
                                               @Override
                                               public void run() {
                                                   buyBtn.setColor(Color.WHITE);
                                               }
                                           }, 0.5f);
                                       }
                                   }
                               });
            parent.add(buyBtn).padLeft(10).width(80).height(40);
            parent.row();
        }
    }

    private int getPriceForComponent(String name) {
        // Процессоры
        if (name.contains("i5-12400F")) return 180;
        if (name.contains("Ryzen 5 5600X")) return 220;
        if (name.contains("i9")) return 350;
        if (name.contains("Ryzen 7 5800X3D")) return 400;

        // Видеокарты
        if (name.contains("RTX 3070")) return 350;
        if (name.contains("NVIDIA GTX 1660")) return 400;
        if (name.contains("RTX 4090")) return 600;
        if (name.contains("AMD RX 6700 XT")) return 550;

        // Память
        if (name.contains("16GB DDR4")) return 70;
        if (name.contains("32GB DDR4")) return 120;
        if (name.contains("64GB DDR5")) return 250;
        if (name.contains("Ballistix 16GB")) return 80;

        return 0;
    }

    private void updateBalanceLabel() {

        if (balanceLabel != null) {
            balanceLabel.setText("Balance: $" + GameState.balance);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1); // Белый фон
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
