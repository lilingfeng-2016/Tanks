package com.ThirtyNineEighty.Game;

import android.view.MotionEvent;

import com.ThirtyNineEighty.Game.Collisions.CollisionManager;
import com.ThirtyNineEighty.Game.Menu.GameMenu;
import com.ThirtyNineEighty.Game.Objects.GameObject;
import com.ThirtyNineEighty.Game.Objects.IGameObject;
import com.ThirtyNineEighty.Helpers.Vector3;
import com.ThirtyNineEighty.Renderable.I2DRenderable;
import com.ThirtyNineEighty.Renderable.I3DRenderable;
import com.ThirtyNineEighty.System.DeltaTime;

import java.util.ArrayList;
import java.util.Collection;

public class World
{
  protected ArrayList<IGameObject> objects;

  protected AI ai;
  protected GameMenu menu;
  protected CollisionManager collisionManager;

  private GameObject playerTank;

  public void initialize(String mapName)
  {
    ai = new AI();
    menu = new GameMenu();
    collisionManager = new CollisionManager();

    playerTank = new GameObject(0, this, "tank");
    playerTank.onMoved(-20);

    GameObject otherTank = new GameObject(1, this, "tank");
    GameObject land = new GameObject(2, this, "land");

    land.onMoved(Vector3.zAxis, -0.8f);

    objects = new ArrayList<IGameObject>();
    objects.add(playerTank);
    objects.add(otherTank);
    objects.add(land);
  }

  public AI getAI()
  {
    return ai;
  }

  public I3DRenderable getCameraTarget()
  {
    return playerTank.getRenderable();
  }

  public Collection<I3DRenderable> get3DRenderable()
  {
    ArrayList<I3DRenderable> result = new ArrayList<I3DRenderable>();
    for(IGameObject object : objects)
      result.add(object.getRenderable());

    return result;
  }

  public Collection<I2DRenderable> get2DRenderable()
  {
    return menu.getControls();
  }

  public void move(IGameObject gameObject, float length)
  {
    collisionManager.move(gameObject, objects, length);
  }

  public void rotate(IGameObject gameObject, float angleX, float angleY, float angleZ)
  {
    collisionManager.rotate(gameObject, objects, angleX, angleY, angleZ);
  }

  public boolean pushEvent(MotionEvent event, float width, float height)
  {
    return menu.processEvent(event, width, height);
  }

  public void update()
  {
    if (menu.getForwardState())
      playerTank.move(0.2f);//* DeltaTime.getDelta());

    if (menu.getLeftState())
      playerTank.rotate(0.0f, 0.0f, 45f * DeltaTime.getDelta());

    if (menu.getRightState())
      playerTank.rotate(0.0f, 0.0f, -45f * DeltaTime.getDelta());

    ai.update(objects);
  }
}
