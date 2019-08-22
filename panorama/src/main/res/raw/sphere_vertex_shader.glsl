uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;// A constant representing the combined model/view matrix.

attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_TextureCoordinates;

varying vec3 v_Position;		// Interpolated position for this fragment.
varying vec4 v_Color;
varying vec2 v_TextureCoordinates;

void main() {
    // Transform the vertex into eye space.
	v_Position = vec3(u_MVMatrix * a_Position);

    v_Color = a_Color;

    v_TextureCoordinates = a_TextureCoordinates;

    gl_Position = u_MVPMatrix * a_Position;
}