/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ballpaddleboard.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author DELL
 */
public class PhysicalSprite {

    public Sprite sprite;
    public Body body;
    BodyDef bodyDef;
    FixtureDef fixtureDef;
    public Fixture fixture;
    private float scale;

    public PhysicalSprite(Texture tex, World world, float x, float y, float scale, BodyDef bodyDef, FixtureDef fixtureDef, Object bodyUserData, Object fixtureUserData) {
        this.scale = scale;
        this.sprite = new Sprite(tex);
        this.sprite.setPosition(x, y);

        this.bodyDef = bodyDef;
        this.body = world.createBody(bodyDef);
        this.body.setUserData(bodyUserData);

        this.fixtureDef = fixtureDef;
        this.fixture = this.body.createFixture(fixtureDef);
        this.fixture.setUserData(fixtureUserData);
    }

    public PhysicalSprite(Texture tex, World world, float x, float y, float scale, BodyDef bodyDef, FixtureDef fixtureDef) {
        this(tex, world, x, y, scale, bodyDef, fixtureDef, null, null);
    }

    public PhysicalSprite(Defs defs, World world, Object bodyUserData, Object fixtureUserData) {
        this(defs.tex, world, defs.x, defs.y, defs.scale, defs.bodyDef, defs.fixtureDef, bodyUserData, fixtureUserData);
    }

    public PhysicalSprite(Defs defs, World world) {
        this(defs, world, null, null);
    }

    /*
     * Sets this sprite's position, in screen coordinates (pixels).
     */
    public void setScreenPosition(float x, float y, float angle) {
        this.sprite.setX(x);
        this.sprite.setY(y);
        this.body.setTransform(new Vector2(
                (x + this.sprite.getWidth() / 2) / this.scale,
                (y + this.sprite.getHeight() / 2) / this.scale),
                angle);
    }

    /*
     * Sets this sprite's position, in physics world coordinates (meters).
     */
    public void setWorldPosition(float x, float y, float angle) {
        this.body.setTransform(x, y, angle);
        this.update();
    }

    /*
     * update sprite's position from physics body's position
     */
    public void update() {
        this.sprite.setPosition(
                (this.body.getPosition().x * this.scale) - this.sprite.getWidth() / 2,
                (this.body.getPosition().y * this.scale) - this.sprite.getHeight() / 2);
        this.sprite.setRotation((float) Math.toDegrees(this.body.getAngle()));
    }

    /*
     * Draws this sprite onto the given sprite batch.
     */
    public void draw(SpriteBatch batch) {
        batch.draw(this.sprite,
                this.sprite.getX(),
                this.sprite.getY(),
                this.sprite.getOriginX(),
                this.sprite.getOriginY(),
                this.sprite.getWidth(),
                this.sprite.getHeight(),
                this.sprite.getScaleX(),
                this.sprite.getScaleY(),
                this.sprite.getRotation());
    }

    /*
     * helper for initializing BodyDef and FixtureDef to a given size and position
     */
    public static class Defs {

        public Texture tex;
        public BodyDef bodyDef;
        public FixtureDef fixtureDef;
        float x;
        float y;
        float scale;

        private Defs() {
        }

        /*
         * Creates BodyDef and FixtureDef from given screen coordinates.
         */
        public static Defs fromScreenCoordinates(Texture tex, float x, float y, float scale) {
            Defs defs = new Defs();
            defs.tex = tex;
            defs.x = x;
            defs.y = y;
            defs.scale = scale;

            defs.bodyDef = new BodyDef();
            defs.bodyDef.position.set(
                    (x + tex.getWidth() / 2) / scale,
                    (y + tex.getHeight() / 2) / scale);

            defs.fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(
                    tex.getWidth() / 2 / scale,
                    tex.getHeight() / 2 / scale);
            defs.fixtureDef.shape = shape;
            return defs;
        }

        /*
         * Creates BodyDef and FixtureDef from given world coordinates.
         */
        public static Defs fromWorldCoordinates(Texture tex, float x, float y, float scale) {
            Defs defs = new Defs();
            defs.tex = tex;
            defs.x = x * scale;
            defs.y = y * scale;
            defs.scale = scale;

            defs.bodyDef = new BodyDef();
            defs.bodyDef.position.set(x, y);

            defs.fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(
                    tex.getWidth() / 2 / scale,
                    tex.getHeight() / 2 / scale);
            defs.fixtureDef.shape = shape;
            return defs;
        }
    }
}
