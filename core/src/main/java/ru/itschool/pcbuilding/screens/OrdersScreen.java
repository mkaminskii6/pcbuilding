package ru.itschool.pcbuilding.screens;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.itschool.pcbuilding.Main;
import ru.itschool.pcbuilding.components.Order;
import ru.itschool.pcbuilding.utils.Constants;
import ru.itschool.pcbuilding.utils.GameState;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.Random;

public class OrdersScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table ordersTable;
    private Label balanceLabel;
    private Random random;
    private float timeSinceLastOrder;

    public OrdersScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        this.ordersTable = new Table();
        this.random = new Random();
        this.timeSinceLastOrder = 0;

        // Если заказы ещё не были созданы
        if (GameState.orders == null) {
            GameState.orders = new ArrayList<>();
        }

        Gdx.input.setInputProcessor(stage);
        setupUI();
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        this.balanceLabel = new Label("Balance: $" + GameState.balance, skin, "title");
        mainTable.add(balanceLabel).top().right().pad(10).row();

        Label title = new Label("Orders", skin, "title");
        mainTable.add(title).colspan(2).padBottom(20).row();

        this.ordersTable = new Table();
        mainTable.add(ordersTable).colspan(2).expand().fill().row();

        TextButton backBtn = new TextButton("Back to Desktop", skin);
        backBtn.addListener(e -> {
            if (e.toString().contains("touchDown")) {
                game.setScreen(new DesktopScreen(game));
                return true;
            }
            return false;
        });
        mainTable.add(backBtn).colspan(2).padTop(20);

        refreshOrdersTable();
    }

    private void refreshOrdersTable() {
        ordersTable.clearChildren();

        ordersTable.add(new Label("Customer", skin, "bold")).pad(10);
        ordersTable.add(new Label("Components", skin, "bold")).pad(10).width(300);
        ordersTable.add(new Label("Price", skin, "bold")).pad(10);
        ordersTable.add(new Label("Status", skin, "bold")).pad(10);
        ordersTable.row();

        for (final Order order : GameState.orders) {
            ordersTable.add(new Label(order.getCustomerName(), skin)).pad(5);
            ordersTable.add(new Label(order.getComponentsList(), skin)).pad(5);
            ordersTable.add(new Label("$" + order.getPrice(), skin)).pad(5);

            if (order.isCompleted()) {
                ordersTable.add(new Label("Completed", skin)).pad(5);
            } else if (order == GameState.currentOrder && order.isAccepted()) {
                TextButton deliverBtn = new TextButton("Deliver", skin);
                deliverBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        order.complete();
                        GameState.addMoney(order.getPrice());
                        balanceLabel.setText("Balance: $" + GameState.balance);
                        GameState.currentOrder = null;
                        refreshOrdersTable();
                    }
                });
                ordersTable.add(deliverBtn).pad(5);
            } else if (order.isAccepted()) {
                ordersTable.add(new Label("In progress", skin)).pad(5);
            } else {
                TextButton acceptBtn = new TextButton("Accept", skin);
                acceptBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (GameState.currentOrder == null) {
                            order.accept();
                            GameState.currentOrder = order;
                            refreshOrdersTable();
                        }
                    }
                });
                ordersTable.add(acceptBtn).pad(5);
            }

            ordersTable.row();
        }
    }

    private void addRandomOrder() {
        if (GameState.orders.size() < 5) {
            GameState.orders.add(Order.generateRandomOrder(random));
            refreshOrdersTable();
        }
    }

    @Override
    public void render(float delta) {
        if (GameState.currentOrder == null) {
            timeSinceLastOrder += delta;
            if (timeSinceLastOrder >= 5f) {
                timeSinceLastOrder = 0;
                addRandomOrder();
            }
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
    }
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}


