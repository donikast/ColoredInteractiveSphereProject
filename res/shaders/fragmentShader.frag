#version 460 core
in vec3 vertexColor;  // Получаваме интерполирания цвят от vertex шейдъра
out vec4 FragColor;

void main() {
    FragColor = vec4(vertexColor, 1.0); // Оцветяване с интерполиран цвят
}
