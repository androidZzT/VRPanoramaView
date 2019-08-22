precision mediump float; //中等 float 精度

varying vec4 v_Color;
varying vec2 v_TextureCoordinates;

uniform sampler2D u_Texture;

void main() {
    gl_FragColor = texture2D(u_Texture, v_TextureCoordinates);
}
