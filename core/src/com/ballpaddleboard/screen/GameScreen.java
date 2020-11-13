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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ballpaddleboard.core.PhysicalSprite;
import com.ballpaddleboard.game.BallPaddleBoard;

/**
 *
 * @author DELL
 */
public class GameScreen implements Screen {

    static final float WORLD_SCALE = 100f;

    static final short WORLD_BOUND = 1;
    static final short PADDLE = 1 << 1;
    static final short BRICK = 1 << 2;
    static final short BALL = 1 << 3;
    static final short BALL_KILLER = 1 << 4;

    static final float BALL_SENTINEL = 1.0f;

    BallPaddleBoard game;
    World world;
    Texture padTex;
    Texture ballTex;
    Texture brickTex;
    Texture bgTex;
    Fixture ballKiller;
    PhysicalSprite paddle;
    PhysicalSprite ball;
    Array<Body> bumpers;
    Array<PhysicalSprite> bricks;
    Array<PhysicalSprite> toBeDestroyed;

    private boolean ballStarted = false;
    private boolean resetBall = false;

    private int score = 0;
    private int scoreMultiplier;
    private int lives = 3;
    private int screenWidth;
    private int screenHeight;

    GameScreen(BallPaddleBoard game) {
        this(game, 0, 1, 3);
    }

    GameScreen(BallPaddleBoard game, int score, int scoreMultiplier, int lives) {
        this.game = game;
        this.score = score;
        this.scoreMultiplier = scoreMultiplier;
        this.lives = lives;
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();

        this.padTex = new Texture("paddle.png");
        this.ballTex = new Texture("ball.png");
        this.brickTex = new Texture("brick.png");
        this.bgTex = new Texture("background.png");

        this.bumpers = new Array<Body>();
        this.bricks = new Array<PhysicalSprite>();
        this.toBeDestroyed = new Array<PhysicalSprite>();

        this.world = new World(new Vector2(0, 0), true);

        this.world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fixa = contact.getFixtureA();
                Fixture fixb = contact.getFixtureB();

                for (PhysicalSprite brick : bricks) {
                    if ((fixa == brick.fixture && fixb == ball.fixture)
                            || (fixa == ball.fixture && fixb == brick.fixture)) {
                        toBeDestroyed.add(brick);
                    }
                }

                if ((fixa == ballKiller && fixb == ball.fixture)
                        || (fixa == ball.fixture && fixb == ballKiller)) {
                    resetBall = true;
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

        this.createEdges();
        this.createPaddle();
        this.createBall();
        this.createBricks();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float f) {
        if (this.resetBall) {
            this.ball.setScreenPosition(
                    (Gdx.graphics.getWidth() / 2) - (this.ball.sprite.getWidth() / 2),
                    100,
                    0);
            this.ball.body.setAngularVelocity(0);
            this.ball.body.setLinearVelocity(0, 0);
            this.ball.body.setAwake(false);// TODO did this fix it?
            this.resetBall = false;
            this.ballStarted = false;
            if (this.lives-- == 0) {
                this.game.setScreen(new GameOverScreen(this.game, this.score));
                this.dispose();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.game.setScreen(new PausedScreen(this.game, this));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.paddle.body.applyForceToCenter(-1f, 0f, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.paddle.body.applyForceToCenter(1f, 0f, true);
        }

        if (!this.ballStarted
                && (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))) {
            //|| Gdx.input.justTouched()))
            this.ball.body.applyForceToCenter(
                    (float) (((Math.random() * 2) - 1) / 10),
                    (float) (((Math.random() * 2) - 1) / 10),
                    true);
            this.ball.body.applyAngularImpulse(
                    (float) (((Math.random() * 2) - 1) / 1000),
                    true);
            this.ballStarted = true;
        }

        this.world.step(f, 6, 2);

// destroy any collided bricks
        while (this.toBeDestroyed.size != 0) {
            PhysicalSprite dead = this.toBeDestroyed.first();
            this.toBeDestroyed.removeIndex(0);
            this.world.destroyBody(dead.body);
            if (this.bricks.contains(dead, true)) {
                this.bricks.removeValue(dead, true);
                this.score += this.scoreMultiplier;
            }
        }

        // level up
        if (this.bricks.size == 0) {
            this.game.setScreen(new GameScreen(
                    this.game, this.score, this.scoreMultiplier + 1,
                    // bonus life every 5 levels
                    this.lives + (this.scoreMultiplier % 5 == 0 ? 1 : 0)));
            this.dispose();
            return;
        }

        // ensure the ball has a minimum velocity
        if (this.ballStarted) {
            Vector2 ballVel = this.ball.body.getLinearVelocity();
            if (Math.abs(ballVel.x) < BALL_SENTINEL) {
                if (ballVel.x == 0f) {
                    this.ball.body.applyForceToCenter(BALL_SENTINEL, 0, true);
                } else {
                    this.ball.body.applyForceToCenter(
                            BALL_SENTINEL * Math.signum(ballVel.x), 0, true);
                }
            }
            if (Math.abs(ballVel.y) < BALL_SENTINEL) {
                if (ballVel.y == 0f) {
                    this.ball.body.applyForceToCenter(
                            0, BALL_SENTINEL, true);
                } else {
                    this.ball.body.applyForceToCenter(
                            0, BALL_SENTINEL * Math.signum(ballVel.y), true);
                }
            }
            if (this.ball.body.getAngularVelocity() == 0.0f) {
                this.ball.body.applyAngularImpulse(
                        (float) (((Math.random() * 2) - 1) / 1000),
                        true);
            }
        }

        // UPDATE SPRITE POSITIONS FROM PHYSICS DATA
        this.paddle.update();
        this.ball.update();
        for (PhysicalSprite brick : this.bricks) {
            brick.update();
        }

        // DRAW SPRITES
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.game.batch.begin();
        this.game.batch.draw(this.bgTex, 0, 0);
        this.paddle.draw(this.game.batch);
        this.ball.draw(this.game.batch);
        for (PhysicalSprite brick : this.bricks) {
            brick.draw(this.game.batch);
        }
        this.drawScore();
        this.game.batch.end();

    }

    @Override
    public void resize(int i, int i1) {
        this.screenWidth = i;
        this.screenHeight = i1;
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
        this.padTex.dispose();
        this.ballTex.dispose();
        this.brickTex.dispose();
        this.world.dispose();
    }
    
    private void drawScore() {
        this.game.font.setColor(1, 0, 0, 1);
        this.game.font.draw(this.game.batch, String.format("Score: %d", score), 0, 30);
        this.game.batch.draw(ballTex, Gdx.graphics.getWidth() - ballTex.getWidth() - 110, 5);
        this.game.font.draw(this.game.batch, String.format("x %d", lives), Gdx.graphics.getWidth() - 100, 30);
    }

    private void createPaddle() {
        float x = (this.screenWidth / 2) - (this.padTex.getWidth() / 2);
        float y = 3;
        PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                this.padTex, x, y, WORLD_SCALE);

        defs.bodyDef.type = BodyDef.BodyType.DynamicBody;
        defs.bodyDef.fixedRotation = true;

        defs.fixtureDef.density = 0.1f;
        defs.fixtureDef.filter.categoryBits = PADDLE;
        defs.fixtureDef.filter.maskBits = (BALL | WORLD_BOUND);

        this.paddle = new PhysicalSprite(defs, this.world);
        this.paddle.body.setLinearDamping(2.0f);
    }

    private void createBall() {
        float x = this.screenWidth / 2;
        float y = 100;

        PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                this.ballTex, x, y, WORLD_SCALE);

        defs.bodyDef.type = BodyDef.BodyType.DynamicBody;

        defs.fixtureDef.friction = 0;
        defs.fixtureDef.density = 0.1f;
        defs.fixtureDef.restitution = 1f;
        defs.fixtureDef.filter.categoryBits = BALL;
        defs.fixtureDef.filter.maskBits = (PADDLE | BRICK | WORLD_BOUND | BALL_KILLER);

        this.ball = new PhysicalSprite(defs, this.world);
    }

    private void createBricks() {
        int brickWidth = this.brickTex.getWidth();
        int brickHeight = this.brickTex.getHeight();
        int numCols = 9;
        int numRows = 7;

        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                int col = i * brickWidth + 22;
                int row = this.screenHeight - brickHeight - (j * brickHeight) - 22;
                Gdx.app.log("Creating", String.format("at row %d and col %d", row, col));

                PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                        this.brickTex, col, row, WORLD_SCALE);

                defs.bodyDef.type = BodyDef.BodyType.DynamicBody;

                defs.fixtureDef.density = 0.1f;
                defs.fixtureDef.filter.categoryBits = BRICK;
                defs.fixtureDef.filter.maskBits = (BALL | WORLD_BOUND);

                PhysicalSprite brick = new PhysicalSprite(defs, this.world);
                this.bricks.add(brick);
            }
        }
    }

    private void createEdges() {
        float w = Gdx.graphics.getWidth() / WORLD_SCALE;
        float h = Gdx.graphics.getHeight() / WORLD_SCALE;

        for (Vector2[] points : new Vector2[][]{
            {new Vector2(0, 0), new Vector2(0, h)}, // left
            {new Vector2(w, 0), new Vector2(w, h)}, // right
            {new Vector2(0, 0), new Vector2(w, 0)}, // top
            {new Vector2(0, h), new Vector2(w, h)}} // bottom
                ) {
            Vector2 p1, p2;
            p1 = points[0];
            p2 = points[1];
            Gdx.app.log("GameScreen", String.format("Creating edge from %s to %s", p1, p2));

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(0f, 0f);

            EdgeShape shape = new EdgeShape();
            shape.set(p1, p2);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = WORLD_BOUND;

            Body body = this.world.createBody(bodyDef);
            body.createFixture(fixtureDef);

            this.bumpers.add(body);
            shape.dispose();
        }

        // create ball killer
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0f, 0f);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 1 / WORLD_SCALE, w, 1 / WORLD_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = BALL_KILLER;

        Body body = this.world.createBody(bodyDef);
        this.ballKiller = body.createFixture(fixtureDef);

        this.bumpers.add(body);
        shape.dispose();
    }

}
