// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.renderer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper for accessing the texture atlas.
 */
@Singleton
public class TextureAtlasHelper {

    private static final String TILE_TEXTURE_NAME = "tile_bw";
    private static final String SPEARMAN_TEXTURE_NAME = "spearman";
    private static final String SHIELD_TEXTURE_NAME = "shield";
    private static final String WATER_TEXTURE_NAME = "water";
    private static final String BEACH_SAND_TEXTURE_NAME = "beach_sand";
    private static final String BEACH_WATER_TEXTURE_NAME = "beach_water";
    private static final String CAPITAL_TEXTURE_NAME = "capital";
    private static final String OAK_TREE_TEXTURE_NAME = "tree";
    private static final String PALM_TREE_TEXTURE_NAME = "palm_tree";
    private static final String CASTLE_TEXTURE_NAME = "castle";
    private static final String GRAVESTONE_TEXTURE_NAME = "gravestone";
    private static final String PEASANT_TEXTURE_NAME = "peasant";
    private static final String KNIGHT_TEXTURE_NAME = "knight";
    private static final String BARON_TEXTURE_NAME = "baron";

    private final TextureAtlas textureAtlas;

    private final Map<String, TextureRegion> textureRegionCache = new HashMap<>();
    private final Map<String, Animation<TextureRegion>> animationCache = new HashMap<>();

    @Getter
    private final Sprite tileSprite;
    @Getter
    private final Sprite spearmanSprite;
    @Getter
    private final Sprite shieldSprite;
    @Getter
    private final TextureRegion tileRegion;
    @Getter
    private final TextureRegion shieldRegion;
    @Getter
    private final Animation<TextureRegion> blinkingGravestoneAnimation;
    @Getter
    private final Animation<TextureRegion> blinkingOakTreeAnimation;
    @Getter
    private final Animation<TextureRegion> blinkingPalmTreeAnimation;
    @Getter
    private final Animation<TextureRegion> waterAnimation;
    @Getter
    private final Animation<TextureRegion> beachSandAnimation;
    @Getter
    private final Animation<TextureRegion> beachWaterAnimation;


    /**
     * Constructor.
     */
    @Inject
    public TextureAtlasHelper(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;

        tileSprite = textureAtlas.createSprite(TILE_TEXTURE_NAME);
        spearmanSprite = textureAtlas.createSprite(SPEARMAN_TEXTURE_NAME);
        shieldSprite = textureAtlas.createSprite(SHIELD_TEXTURE_NAME);
        tileRegion = textureAtlas.findRegion(TILE_TEXTURE_NAME);
        shieldRegion = textureAtlas.findRegion(SHIELD_TEXTURE_NAME);
        blinkingGravestoneAnimation = new Animation<>(1F,
            ImmutableList.of(textureAtlas.findRegion(GRAVESTONE_TEXTURE_NAME),
                new TextureAtlas.AtlasRegion(new Texture(0, 0,
                    Pixmap.Format.Alpha), 0, 0, 0, 0)).toArray(new TextureAtlas.AtlasRegion[0]));
        blinkingOakTreeAnimation = new Animation<>(1F,
            ImmutableList.of(textureAtlas.findRegion(OAK_TREE_TEXTURE_NAME),
                new TextureAtlas.AtlasRegion(new Texture(0, 0,
                    Pixmap.Format.Alpha), 0, 0, 0, 0)).toArray(new TextureAtlas.AtlasRegion[0]));
        blinkingPalmTreeAnimation = new Animation<>(1F,
            ImmutableList.of(textureAtlas.findRegion(PALM_TREE_TEXTURE_NAME),
                new TextureAtlas.AtlasRegion(new Texture(0, 0,
                    Pixmap.Format.Alpha), 0, 0, 0, 0)).toArray(new TextureAtlas.AtlasRegion[0]));
        waterAnimation = new Animation<>(1F,
            textureAtlas.findRegions(WATER_TEXTURE_NAME));
        beachSandAnimation = new Animation<>(1F,
            textureAtlas.findRegions(BEACH_SAND_TEXTURE_NAME));
        beachWaterAnimation = new Animation<>(1F,
            textureAtlas.findRegions(BEACH_WATER_TEXTURE_NAME));
    }

    /**
     * Finds and returns a texture region for the given tile content.
     */
    public TextureRegion findTextureRegionForTileContent(TileContent tileContent) {
        final String regionName = getRegionNameForTileContent(tileContent);
        return findRegionByName(regionName);
    }

    /**
     * Finds and returns an animation region for the given tile content.
     */
    public Animation<TextureRegion> findAnimationForTileContent(TileContent tileContent) {
        final String regionName = getRegionNameForTileContent(tileContent);
        return findAnimationByName(regionName);
    }

    /**
     * Finds and returns a sprite for the given tile content.
     */
    public Sprite createSpriteForTileContent(TileContent tileContent) {
        final String regionName = getRegionNameForTileContent(tileContent);
        return textureAtlas.createSprite(regionName);
    }

    private String getRegionNameForTileContent(TileContent tileContent) {
        final Class<? extends TileContent> tileContentClass = tileContent.getClass();
        if (Unit.class.isAssignableFrom(tileContentClass)) {
            return getRegionNameForUnit((Unit) tileContent);
        } else if (Capital.class.isAssignableFrom(tileContentClass)) {
            return CAPITAL_TEXTURE_NAME;
        } else if (Tree.class.isAssignableFrom(tileContentClass)) {
            return OAK_TREE_TEXTURE_NAME;
        } else if (PalmTree.class.isAssignableFrom(tileContentClass)) {
            return PALM_TREE_TEXTURE_NAME;
        } else if (Castle.class.isAssignableFrom(tileContentClass)) {
            return CASTLE_TEXTURE_NAME;
        } else if (Gravestone.class.isAssignableFrom(tileContentClass)) {
            return GRAVESTONE_TEXTURE_NAME;
        }
        throw new IllegalStateException("Unknown tile content class: " + tileContentClass);
    }

    private String getRegionNameForUnit(Unit unit) {
        switch (unit.getStrength()) {
            case 1:
                return PEASANT_TEXTURE_NAME;
            case 2:
                return TextureAtlasHelper.SPEARMAN_TEXTURE_NAME;
            case 3:
                return KNIGHT_TEXTURE_NAME;
            case 4:
                return BARON_TEXTURE_NAME;
            default:
                throw new IllegalStateException("Unknown unit of strength " + unit.getStrength());
        }
    }

    private Animation<TextureRegion> findAnimationByName(String name) {
        return animationCache.computeIfAbsent(name, nameParam -> new Animation<>(1F,
            textureAtlas.findRegions(nameParam)));
    }

    private TextureRegion findRegionByName(String name) {
        return textureRegionCache.computeIfAbsent(name, textureAtlas::findRegion);
    }

}
