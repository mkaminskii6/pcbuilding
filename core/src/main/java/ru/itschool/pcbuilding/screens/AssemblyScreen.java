
package ru.itschool.pcbuilding.screens;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Arrays;

import ru.itschool.pcbuilding.Main;
import ru.itschool.pcbuilding.components.PartDraggable;
import ru.itschool.pcbuilding.utils.GameState;
import java.util.List;
import java.util.ArrayList;

public class AssemblyScreen implements Screen {

    private final Main game;
    private Stage stage;
    private Image caseImage;
    private Image cpuSlot, gpuSlot, ramSlot;
    private Table inventoryTable;
    private Group dragLayer;
    private PartDraggable currentCPU, currentGPU, currentRAM;
    private TextButton assembleButton;

    public AssemblyScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // 1. Фон (простой черный)
        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(game.skin.getDrawable("default-pane"));
        stage.getRoot().addActorAt(0, background);

        // 2. Верхняя панель
        Table topBar = new Table();
        topBar.setBackground(game.skin.getDrawable("default-pane"));
        topBar.setFillParent(true);
        topBar.top().padTop(10);

        // Кнопка назад
        TextButton backButton = new TextButton("Back to Desktop", game.skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new DesktopScreen(game));
            }
        });

        // Заголовок
        BitmapFont font = new BitmapFont(); // стандартный шрифт
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label titleLabel = new Label("ASSEMBLING", style);

        topBar.add(backButton).width(150).height(50).padLeft(10);
        topBar.add(titleLabel).expandX().center();
        stage.addActor(topBar);

        // 3. Корпус и слоты
        caseImage = new Image(new Texture(Gdx.files.internal("images/parts/case.png")));
        caseImage.setSize(400, 300);
        caseImage.setPosition(
            Gdx.graphics.getWidth() / 2f - 200,
            Gdx.graphics.getHeight() / 2f - 100
        );
        assembleButton = new TextButton("ASSEMBLE", game.skin);
        assembleButton.setVisible(false);
        assembleButton.setPosition(
            Gdx.graphics.getWidth() - 200,
            Gdx.graphics.getHeight() / 2f
        );
        assembleButton.setSize(150, 60);

        assembleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Удаляем детали из инвентаря
                GameState.inventory.remove(currentCPU.getName());
                GameState.inventory.remove(currentGPU.getName());
                GameState.inventory.remove(currentRAM.getName());

                // Добавляем собранный ПК
                List<String> pcBuild = Arrays.asList(
                    currentCPU.getName(),
                    currentGPU.getName(),
                    currentRAM.getName()
                );
                GameState.builtPCs.add(pcBuild);

                // Сбрасываем сборку
                currentCPU.remove();
                currentGPU.remove();
                currentRAM.remove();
                currentCPU = null;
                currentGPU = null;
                currentRAM = null;

                assembleButton.setVisible(false);
                refreshInventory(); // Вернуть детали на места, если остались
            }
        });

        stage.addActor(assembleButton);
        stage.addActor(caseImage);

        // Слоты (полупрозрачные)
        cpuSlot = createSlot(150, 180, Color.GREEN);
        gpuSlot = createSlot(230, 90, Color.BLUE);
        ramSlot = createSlot(70, 90, Color.YELLOW);

        dragLayer = new Group();
        stage.addActor(dragLayer);

        // 4. Инвентарь
        inventoryTable = new Table();
        inventoryTable.setBackground(game.skin.getDrawable("default-pane"));


        ScrollPane scrollPane = new ScrollPane(inventoryTable, game.skin);
        scrollPane.setSize(Gdx.graphics.getWidth(), 180);
        scrollPane.setPosition(0, 0);
        stage.addActor(scrollPane);


        refreshInventory();
    }

    private Image createSlot(float relX, float relY, Color color) {
        Pixmap pm = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pm.setColor(color.r, color.g, color.b, 0.3f);
        pm.fill();
        Image slot = new Image(new Texture(pm));
        pm.dispose();
        slot.setPosition(caseImage.getX() + relX, caseImage.getY() + relY);
        stage.addActor(slot);
        return slot;
    }

    private void refreshInventory() {
        inventoryTable.clear();

        for (String itemName : GameState.inventory) {
            Table itemCell = new Table(game.skin);
            String type = getComponentType(itemName);

            // Это просто иконка в ячейке инвентаря
            PartDraggable icon = new PartDraggable(type,  80, 80);
            icon.setName(itemName);

            Label label = new Label(itemName, game.skin);
            label.setAlignment(Align.center);

            itemCell.add(icon).size(80, 80).row();
            itemCell.add(label).width(120).padTop(5);
            inventoryTable.add(itemCell).pad(10);

            // Настраиваем перетаскивание
            icon.addListener(new ClickListener() {
                private PartDraggable dragPart;

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    // Создаем новый дубликат перетаскиваемой детали
                    dragPart = new PartDraggable(type, 80, 80);
                    dragPart.setName(itemName);
                    dragPart.setPosition(event.getStageX() - 40, event.getStageY() - 40);
                    dragLayer.addActor(dragPart);
                    dragPart.toFront(); // поверх
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    if (dragPart != null) {
                        dragPart.setPosition(event.getStageX() - 40, event.getStageY() - 40);
                    }
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (dragPart != null) {
                        boolean placed = false;
                        Rectangle partRect = new Rectangle(dragPart.getX(), dragPart.getY(), dragPart.getWidth(), dragPart.getHeight());

                        if (type.equals("cpu")) {
                            Rectangle slotRect = new Rectangle(cpuSlot.getX(), cpuSlot.getY(), cpuSlot.getWidth(), cpuSlot.getHeight());
                            if (partRect.overlaps(slotRect)) {
                                dragPart.setPosition(
                                    cpuSlot.getX() + (cpuSlot.getWidth() - dragPart.getWidth()) / 2,
                                    cpuSlot.getY() + (cpuSlot.getHeight() - dragPart.getHeight()) / 2
                                );
                                currentCPU = dragPart;
                                placed = true;
                            }
                        }

                        else if (type.equals("gpu")) {
                            Rectangle slotRect = new Rectangle(gpuSlot.getX(), gpuSlot.getY(), gpuSlot.getWidth(), gpuSlot.getHeight());
                            if (partRect.overlaps(slotRect)) {
                                dragPart.setPosition(
                                    gpuSlot.getX() + (gpuSlot.getWidth() - dragPart.getWidth()) / 2,
                                    gpuSlot.getY() + (gpuSlot.getHeight() - dragPart.getHeight()) / 2
                                );
                                currentGPU = dragPart;
                                placed = true;
                            }
                        }

                        else if (type.equals("ram")) {
                            Rectangle slotRect = new Rectangle(ramSlot.getX(), ramSlot.getY(), ramSlot.getWidth(), ramSlot.getHeight());
                            if (partRect.overlaps(slotRect)) {
                                dragPart.setPosition(
                                    ramSlot.getX() + (ramSlot.getWidth() - dragPart.getWidth()) / 2,
                                    ramSlot.getY() + (ramSlot.getHeight() - dragPart.getHeight()) / 2
                                );
                                currentRAM = dragPart;
                                placed = true;
                            }
                        }

                        if (!placed) {
                            checkAssemblyComplete();
                            dragPart.remove(); // если не установлено — убираем
                        }
                    }
                }
            });
        }
    }

    private void checkAssemblyComplete() {
        boolean complete = currentCPU != null && currentGPU != null && currentRAM != null;
        assembleButton.setVisible(complete);
    }

    private String getComponentType(String name) {
        name = name.toLowerCase();
        if (name.contains("cpu") || name.contains("intel") || name.contains("ryzen")) return "cpu";
        if (name.contains("gpu") || name.contains("rtx") || name.contains("radeon")|| name.contains("rx")) return "gpu";
        if (name.contains("ram") || name.contains("gb")) return "ram";
        return "unknown";
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
    }
}





