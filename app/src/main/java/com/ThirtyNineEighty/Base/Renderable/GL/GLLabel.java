package com.ThirtyNineEighty.Base.Renderable.GL;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ThirtyNineEighty.Base.Common.Math.Vector3;
import com.ThirtyNineEighty.Base.Providers.IDataProvider;
import com.ThirtyNineEighty.Base.Renderable.Renderable;
import com.ThirtyNineEighty.Base.Renderable.RendererContext;
import com.ThirtyNineEighty.Base.Renderable.Shaders.Shader;
import com.ThirtyNineEighty.Base.Renderable.Shaders.ShaderLabel;
import com.ThirtyNineEighty.Base.Resources.Entities.Geometry;
import com.ThirtyNineEighty.Base.Resources.Entities.Texture;
import com.ThirtyNineEighty.Base.Resources.MeshMode;
import com.ThirtyNineEighty.Base.Resources.Sources.FileTextureSource;
import com.ThirtyNineEighty.Base.Resources.Sources.LabelGeometrySource;
import com.ThirtyNineEighty.Base.GameContext;

import java.nio.FloatBuffer;

public class GLLabel
  extends Renderable
{
  private static final long serialVersionUID = 1L;

  public static final int tabLength = 3;

  private IDataProvider<Data> dataProvider;
  private float[] modelViewMatrix;
  private String fontTexture;

  private transient Texture textureData;

  public GLLabel(IDataProvider<Data> provider)
  {
    this(FontTexture, provider);
  }

  public GLLabel(String font, IDataProvider<Data> provider)
  {
    dataProvider = provider;
    fontTexture = font;
    modelViewMatrix = new float[16];
  }

  @Override
  public void initialize()
  {
    textureData = GameContext.resources.getTexture(new FileTextureSource(fontTexture, false));

    super.initialize();
  }

  @Override
  public void uninitialize()
  {
    super.uninitialize();

    GameContext.resources.release(textureData);
  }

  @Override
  public boolean isVisible()
  {
    Data data = dataProvider.get();
    return data.visible;
  }

  @Override
  public int getShaderId()
  {
    return Shader.ShaderLabel;
  }

  @Override
  public IDataProvider getProvider()
  {
    return dataProvider;
  }

  @Override
  public void draw(RendererContext context)
  {
    Data data = dataProvider.get();
    Vector3 color = data.colorCoefficients;
    ShaderLabel shader = (ShaderLabel)Shader.getCurrent();

    // get dynamic resources
    Geometry geometryData = GameContext.resources.getGeometry(new LabelGeometrySource(data.value, data.mode, data.charWidth, data.charHeight));

    // build result matrix
    Matrix.multiplyMM(modelViewMatrix, 0, context.getOrthoMatrix(), 0, data.modelMatrix, 0);

    // bind texture to 0 slot
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureData.getHandle());

    // send data to shader
    GLES20.glUniform1i(shader.uniformTextureHandle, 0);
    GLES20.glUniformMatrix4fv(shader.uniformModelViewMatrixHandle, 1, false, modelViewMatrix, 0);
    GLES20.glUniform4f(shader.uniformColorCoefficients, color.getX(), color.getY(), color.getZ(), 1);

    // set buffer or reset if dynamic
    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, geometryData.getHandle());

    switch (geometryData.getMode())
    {
    case Static:
      GLES20.glVertexAttribPointer(shader.attributePositionHandle, 2, GLES20.GL_FLOAT, false, 16, 0);
      GLES20.glVertexAttribPointer(shader.attributeTexCoordHandle, 2, GLES20.GL_FLOAT, false, 16, 8);
      break;

    case Dynamic:
      FloatBuffer buffer = geometryData.getData();
      buffer.position(0);
      GLES20.glVertexAttribPointer(shader.attributePositionHandle, 2, GLES20.GL_FLOAT, false, 16, buffer);
      buffer.position(2);
      GLES20.glVertexAttribPointer(shader.attributeTexCoordHandle, 2, GLES20.GL_FLOAT, false, 16, buffer);
      break;
    }

    // enable arrays
    GLES20.glEnableVertexAttribArray(shader.attributePositionHandle);
    GLES20.glEnableVertexAttribArray(shader.attributeTexCoordHandle);

    // validating if debug
    shader.validate();
    geometryData.validate();
    textureData.validate();

    // draw
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, geometryData.getPointsCount());

    // disable arrays
    GLES20.glDisableVertexAttribArray(shader.attributePositionHandle);
    GLES20.glDisableVertexAttribArray(shader.attributeTexCoordHandle);

    // release dynamic resources
    GameContext.resources.release(geometryData);
  }

  public static class Data
    extends Renderable.Data
  {
    private static final long serialVersionUID = 1L;

    public float[] modelMatrix;
    public Vector3 colorCoefficients;

    public String value;
    public MeshMode mode;
    public int charWidth;
    public int charHeight;

    public Data()
    {
      modelMatrix = new float[16];
      colorCoefficients = Vector3.getInstance();
    }
  }
}
