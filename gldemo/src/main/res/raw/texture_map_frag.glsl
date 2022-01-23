varying vec2 v_texCoord;
uniform sampler2D s_TextureMap;
void main()
{
   gl_FragColor = texture2D(s_TextureMap, v_texCoord);
}