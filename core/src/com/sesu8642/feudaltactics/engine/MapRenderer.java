package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;
import com.sesu8642.feudaltactics.FeudalTactics;

public class MapRenderer {

	final float SPRITE_SIZE_MULTIPLIER = 1.5F;

	float width = HexMap.HEX_OUTER_RADIUS * 2;
	float height = HexMap.HEX_OUTER_RADIUS * (float) Math.sqrt(3);
	private HexMap hexMap;
	private PolygonSpriteBatch polySpriteBatch;
	private Texture textureSolid;
	private ArrayList<PolygonSprite> polySprites;
	private HashMap<Vector2, Animation<TextureRegion>> contents;
	float stateTime; // for keeping animations at the correct pace
	Pixmap pix;
	TextureRegion textureRegion;

	public MapRenderer(HexMap hexMap) {
		this.hexMap = hexMap;
		contents = new HashMap<Vector2, Animation<TextureRegion>>();
		polySpriteBatch = new PolygonSpriteBatch();
		pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		polySprites = new ArrayList<PolygonSprite>();
		pix.setColor(0x00DE00FF);
		pix.fill();
		textureSolid = new Texture(pix);
		textureRegion = new TextureRegion(textureSolid);
		stateTime = 0F;
	}

	public void updateMap() {
		// create tiles
		contents.clear();
		polySprites.clear();
		for (Entry<Vector2, HexTile> hexTileEntry : (hexMap.getTiles()).entrySet()) {
			pix.setColor(hexTileEntry.getValue().getPlayer().getColor());
			pix.fill();
			textureSolid = new Texture(pix);
			textureRegion = new TextureRegion(textureSolid);
			Vector2 hexCoords = hexTileEntry.getKey();
			Vector2 mapCoords = getMapCoordinatesFromHexCoordinates(hexCoords);
			// X --> , Y |^
			float[] vertices = { mapCoords.x - 0.5F * width, mapCoords.y, mapCoords.x - 0.25F * width,
					mapCoords.y + 0.5F * height, mapCoords.x + 0.25F * width, mapCoords.y + 0.5F * height,
					mapCoords.x + 0.5F * width, mapCoords.y, mapCoords.x + 0.25F * width, mapCoords.y - 0.5F * height,
					mapCoords.x - 0.25F * width, mapCoords.y - 0.5F * height };
			EarClippingTriangulator triangulator = new EarClippingTriangulator();
			ShortArray triangleIndices = triangulator.computeTriangles(vertices);
			PolygonRegion polyReg = new PolygonRegion(textureRegion, vertices, triangleIndices.toArray());
			polySprites.add(new PolygonSprite(polyReg));
			// create content (units etc)
			MapObject tileContent = hexTileEntry.getValue().getContent();
			if (tileContent != null) {
				contents.put(new Vector2(mapCoords.x - HexMap.HEX_OUTER_RADIUS, mapCoords.y - HexMap.HEX_OUTER_RADIUS),
						new Animation<TextureRegion>(1F,
								FeudalTactics.textureAtlas.findRegions(tileContent.getSpriteName()), PlayMode.LOOP));
			}
		}
	}

	public void render(OrthographicCamera camera) {
		HashMap<Vector2, TextureRegion> frames = new HashMap<Vector2, TextureRegion>(); // current frame for each map
																						// object
		stateTime += Gdx.graphics.getDeltaTime();
		polySpriteBatch.setProjectionMatrix(camera.combined);
		for (Entry<Vector2, Animation<TextureRegion>> content : contents.entrySet()) {
			frames.put(content.getKey(), content.getValue().getKeyFrame(stateTime, true));
		}
		polySpriteBatch.begin();
		// draw all the tiles
		for (PolygonSprite polySprite : polySprites) {
			polySprite.draw(polySpriteBatch);
		}
		// draw all the contents
		float objectSize = SPRITE_SIZE_MULTIPLIER * hexMap.HEX_OUTER_RADIUS;
		float offset = (hexMap.HEX_OUTER_RADIUS-objectSize) / 2;
		for (Entry<Vector2, TextureRegion> currentFrame : frames.entrySet()) {
			polySpriteBatch.draw(currentFrame.getValue(), currentFrame.getKey().x - offset,
					currentFrame.getKey().y - offset, objectSize, objectSize);
		}
		polySpriteBatch.end();
	}

	public Vector2 getMapCoordinatesFromHexCoordinates(Vector2 hexCoords) {
		float x = 0.75F * width * hexCoords.x;
		float y = (float) (hexMap.HEX_OUTER_RADIUS
				* (Math.sqrt(3) / 2 * hexCoords.x + Math.sqrt(3) * (-hexCoords.y - hexCoords.x)));
		return new Vector2(x, y);
	}

	public void dispose() {
		polySpriteBatch.dispose();
	}

	public void resize() {
		polySpriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}
}
