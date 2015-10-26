package com.ThirtyNineEighty.Game.Subprograms;

import com.ThirtyNineEighty.Game.Objects.Descriptions.GameDescription;
import com.ThirtyNineEighty.Game.Objects.GameObject;
import com.ThirtyNineEighty.Game.Worlds.IWorld;
import com.ThirtyNineEighty.System.GameContext;

// TODO: state
public class MoveSubprogram
  extends Subprogram
{
  protected GameObject movedObject;
  protected boolean checkLength;
  protected float length;

  public MoveSubprogram(GameObject obj) { this (obj, 0); }
  public MoveSubprogram(GameObject obj, float len)
  {
    super(String.format("MoveSubprogram_%s", obj.getName()));

    movedObject = obj;
    length = len;
    checkLength = len > 0;
  }

  @Override
  public void onUpdate()
  {
    IWorld world = GameContext.content.getWorld();
    GameDescription description = movedObject.getDescription();

    float stepLength = description.getSpeed() * GameContext.getDelta();
    GameContext.collisions.move(movedObject, stepLength);

    if (checkLength)
    {
      length -= stepLength;
      if (length < 0)
        world.remove(movedObject);
    }
  }
}