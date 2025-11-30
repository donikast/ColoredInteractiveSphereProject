package org.graphics.utils;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class ShaderProgram {
    private final int programID;

    public ShaderProgram(String vertexPath, String fragmentPath) {

        int vertexShader = compileShader(loadShader(vertexPath), GL_VERTEX_SHADER);
        int fragmentShader = compileShader(loadShader(fragmentPath), GL_FRAGMENT_SHADER);

        programID = glCreateProgram();
        glAttachShader(programID, vertexShader);
        glAttachShader(programID, fragmentShader);
        glLinkProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Грешка при линкване на шейдъри: " + glGetProgramInfoLog(programID));
        }

        glDetachShader(programID, vertexShader);
        glDetachShader(programID, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use() {
        glUseProgram(programID);
    }

    private int compileShader(String shaderCode, int type) {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, shaderCode);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Грешка при компилиране на шейдър: " + glGetShaderInfoLog(shaderID));
        }

        return shaderID;
    }

    private String loadShader(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (Exception e) {
            throw new RuntimeException("Грешка при зареждане на шейдър: " + filePath, e);
        }
    }

    public void setUniform(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(programID, name);
        if (location != -1) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            matrix.get(buffer);
            glUniformMatrix4fv(location, false, buffer);
        }
    }

}