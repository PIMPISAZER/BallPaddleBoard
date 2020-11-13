package com.ballpaddleboard.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ballpaddleboard.game.BallPaddleBoard;
import com.ballpaddleboard.util.Constant;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new BallPaddleBoard(), config);
        config.title = Constant.gameTitle;
        config.width = Constant.screenWidth;
        config.height = Constant.screenHeight;
        config.resizable = Constant.screenResizable;
    }
}
