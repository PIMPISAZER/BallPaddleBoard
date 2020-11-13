package com.ballpaddleboard.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ballpaddleboard.game.BallPaddleBoard;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DELL
 */
public class MenuScreen implements Screen {

    Texture img;
    BallPaddleBoard game;
    Sprite logo;

    public MenuScreen(BallPaddleBoard game) {
        this.game = game;
        this.img = new Texture("ST.jpg");
        this.logo = new Sprite(this.img);
        this.logo.setPosition(
                Gdx.graphics.getWidth() / 2 - this.logo.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - this.logo.getHeight() / 2);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.game.batch.begin();
        this.game.batch.draw(this.logo, this.logo.getX(), this.logo.getY());
        this.game.font.setColor(1, 0, 0, 1);
        this.game.font.draw(this.game.batch, "Welcome to Ball&Paddle Board!!! ", 220, 530);
        this.game.font.draw(this.game.batch, "Tap Anywhere to Begin!!! ", 250, 100);
        this.game.batch.end();

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            this.game.setScreen(new GameScreen(this.game));
            dispose();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int i, int i1) {
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
    }

}
