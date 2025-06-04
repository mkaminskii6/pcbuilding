package ru.itschool.pcbuilding.components;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class PartDraggable extends Image {
    private float originalX, originalY;
    private String type;

    public PartDraggable(String type, float width, float height) {
        super(new Texture(Gdx.files.internal("images/parts/" + type + ".png")));
        this.type = type;
        setSize(width, height);

        addListener(new DragListener() {
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                originalX = getX();
                originalY = getY();
                toFront();
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                moveBy(x - getWidth() / 2, y - getHeight() / 2);
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                // Пока просто возвращаем на место
                setPosition(originalX, originalY);
            }
        });
    }

    public String getPartType() {
        return type;
    }
}



