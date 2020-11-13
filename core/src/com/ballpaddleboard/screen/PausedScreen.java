/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ballpaddleboard.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.ballpaddleboard.game.BallPaddleBoard;

/**
 *
 * @author DELL
 */
public class PausedScreen implements Screen {

    BallPaddleBoard game;
    Screen returnTo;

    public PausedScreen(BallPaddleBoard game, Screen returnTo) {
        this.game = game;
        this.returnTo = returnTo;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float f) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            this.game.setScreen(this.returnTo);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.game.batch.begin();;
        this.game.font.setColor(1, 0, 0, 1);
        this.game.font.draw(game.batch, "Paused", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        this.game.batch.end();
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
