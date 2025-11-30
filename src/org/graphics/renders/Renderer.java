package org.graphics.renders;

import static org.lwjgl.opengl.GL46.*;

import org.graphics.utils.InputAction;
import org.graphics.utils.ShaderProgram;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Renderer {
    private int vaoID, vboID;
    private ShaderProgram shaderProgram;
    private float angleX=0f;
    private float angleY=0f;
    private int vertexCount;

    public void init() {
        glEnable(GL_DEPTH_TEST);

        shaderProgram = new ShaderProgram("res/shaders/vertexShader.vert", "res/shaders/fragmentShader.frag");

        float[] vertices = generateSphere(0.5f, 80, 80);
        vertexCount = vertices.length / 6;

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Атрибут 0 - позиция
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Атрибут 1 - цвят
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);


        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shaderProgram.use();
        glBindVertexArray(vaoID);

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix
                .rotateY((float) Math.toRadians(angleY))
                .rotateX((float) Math.toRadians(angleX));

        shaderProgram.setUniform("modelMatrix", modelMatrix);

        Matrix4f viewMatrix = new Matrix4f().lookAt(0.0f, 0.0f, 3.0f,  0.0f, 0.0f, 0.0f,  0.0f, 1f, 0.0f);
        Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(45.0f), 1.0f, 0.1f, 100f);
        shaderProgram.setUniform("viewMatrix", viewMatrix);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glBindVertexArray(0);
    }

    public void handleInputAction(InputAction action) {
        switch (action) {
            case RIGHT -> setAngleY(angleY + 1f);
            case LEFT -> setAngleY(angleY - 1f);
            case UP -> setAngleX(angleX + 1f);
            case DOWN -> setAngleX(angleX - 1f);
        }
    }

    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public void setAngleY(float angleY) {
        this.angleY = angleY;
    }

    private float[] generateSphere(float radius, int sectors, int stacks) {
        List<Float> vertexList = new ArrayList<>();

        for (int i = 0; i <= stacks; i++) {
            float alpha = (float) Math.PI * i / stacks;
            float sinAlpha = (float) Math.sin(alpha);
            float cosAlpha = (float) Math.cos(alpha);

            for (int j = 0; j <= sectors; j++) {
                float beta = (float) (2.0f * Math.PI * j / sectors);
                float sinBeta = (float) Math.sin(beta);
                float cosBeta = (float) Math.cos(beta);


                float x = radius * sinAlpha * cosBeta;
                float y = radius * sinAlpha * sinBeta;
                float z = radius * cosAlpha;

                vertexList.add(x);
                vertexList.add(y);
                vertexList.add(z);
                vertexList.add(0.3f);
                vertexList.add(0.5f);
                vertexList.add(0.8f);
            }
        }

        // Създаване на триъгълници (индексирани чрез strip логика)
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < stacks; i++) {
            int k1 = i * (sectors + 1);
            int k2 = k1 + sectors + 1;

            for (int j = 0; j < sectors; j++, k1++, k2++) {
                if (i != 0) {
                    indices.add(k1);
                    indices.add(k2);
                    indices.add(k1 + 1);
                }
                if (i != (stacks - 1)) {
                    indices.add(k1 + 1);
                    indices.add(k2);
                    indices.add(k2 + 1);
                }
            }
        }

        // Генериране на финален float[] масив
        float[] sphereVertices = new float[indices.size() * 6];
        for (int i = 0; i < indices.size(); i++) {
            int idx = indices.get(i);
            sphereVertices[i * 6 + 0] = vertexList.get(idx * 6 + 0); // x
            sphereVertices[i * 6 + 1] = vertexList.get(idx * 6 + 1); // y
            sphereVertices[i * 6 + 2] = vertexList.get(idx * 6 + 2); // z
            sphereVertices[i * 6 + 3] = vertexList.get(idx * 6 + 3); // r
            sphereVertices[i * 6 + 4] = vertexList.get(idx * 6 + 4); // g
            sphereVertices[i * 6 + 5] = vertexList.get(idx * 6 + 5); // b
        }

        return sphereVertices;
    }

}