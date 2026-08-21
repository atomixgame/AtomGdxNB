#version 120
#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    vec3 glow = vec3(0.0, 0.94, 1.0) * (sin(u_time * 2.0) * 0.2 + 0.8);
    gl_FragColor = vec4(texColor.rgb * glow, texColor.a) * v_color;
}
