package org.graphics.renders;

import org.graphics.utils.InputAction;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;


public class Window {
    private long window;
    private final int width;
    private final int height;
    private final String title;
    private Renderer renderer;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Грешка при инициализация на GLFW!");
        }

        window = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        if (window == 0) {
            throw new RuntimeException("Неуспешно създаване на прозорец!");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL46.glViewport(0, 0, width, height);
        GL46.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glfwSwapInterval(1);

        renderer = new Renderer();
        renderer.init();
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);

            handleInput();
            renderer.render();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void cleanup() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void handleInput() {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            renderer.handleInputAction(InputAction.UP);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            renderer.handleInputAction(InputAction.DOWN);
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            renderer.handleInputAction(InputAction.RIGHT);
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            renderer.handleInputAction(InputAction.LEFT);
        }
    }
}