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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author DELL
 */
public class GameOverScreen implements Screen {

    private static final int LINE_SPACE = 15;

    BallPaddleBoard game;
    int score;
    List<Integer> scores;

    public GameOverScreen(BallPaddleBoard game, int score) {
        this.game = game;
        this.score = score;
        this.scores = new ArrayList<>();
    }

    @Override
    public void show() {
        Collections.sort(this.scores, Collections.reverseOrder());
        this.scores = this.scores.subList(0, (this.scores.size() > 10 ? 10 : this.scores.size()));
    }

    @Override
    public void render(float f) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)
                || Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || Gdx.input.justTouched()) {
            this.game.setScreen(new MenuScreen(this.game));
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.game.batch.begin();
        this.game.font.setColor(1, 0, 0, 1);
        this.game.font.draw(this.game.batch, "GAME OVER", 350, 350);
        this.game.font.draw(this.game.batch, String.format("Your Score: %d", this.score), 330, 270);

        int i = 0;
        for (int iscore : this.scores) {
            this.game.font.draw(this.game.batch,
                    String.format("%d", iscore),
                    Gdx.graphics.getWidth() / 2,
                    Gdx.graphics.getHeight() - (++i * LINE_SPACE + (LINE_SPACE * 4)));
        }
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
