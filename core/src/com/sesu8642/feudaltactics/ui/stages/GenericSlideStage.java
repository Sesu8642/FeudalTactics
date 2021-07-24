package com.sesu8642.feudaltactics.ui.stages;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GenericSlideStage extends Stage {

	private List<Widget> slides;

	Set<Disposable> disposables = new HashSet<Disposable>();
	private Skin skin;
	private OrthographicCamera camera;
	private Widget currentSlide;
	private TextButton backButton;
	private TextButton nextButton;

	private Container<Widget> slideContainer = new Container<Widget>();
	
	public GenericSlideStage(Viewport viewport, List<Widget> slides, Runnable finishedCallback,
			OrthographicCamera camera, Skin skin) {
		super(viewport);
		if (slides.isEmpty()) {
			throw new IllegalArgumentException("at least one slide is required");
		}
		this.camera = camera;
		this.skin = skin;
		this.slides = slides;
		initUI(slides, finishedCallback);
	}

	public GenericSlideStage(Viewport viewport, Batch batch, List<Widget> slides, Runnable finishedCallback,
			OrthographicCamera camera, Skin skin) {
		super(viewport, batch);
		if (slides.isEmpty()) {
			throw new IllegalArgumentException("at least one slide is required");
		}
		this.camera = camera;
		this.skin = skin;
		this.slides = slides;
		initUI(slides, finishedCallback);
	}

	private void initUI(List<Widget> slides, Runnable finishedCallback) {
		backButton = new TextButton("", skin);
		backButton.setDisabled(true);
		backButton.setTouchable(Touchable.disabled);
		nextButton = new TextButton("Next", skin);
		
		currentSlide = slides.get(0);
		//slideContainer.setActor(currentSlide);
		//slideContainer.fill();
				
		Label testLabel = new Label("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.", skin);
		testLabel.setWrap(true);
		testLabel.setColor(skin.getColor("black"));
		
		Texture logoTexture = new Texture(Gdx.files.internal("square_logo.png"));
		Image logo = new Image(logoTexture);
		
		Table widgetTable = new Table(skin);
		//widgetTable.debug();
		widgetTable.add(testLabel).fill();
		widgetTable.row();
		widgetTable.add(logo).fill().expand().prefWidth(0).maxWidth(1000).height(Value.percentWidth(1));
		
		TextArea backgroundArea = new TextArea(null, skin);
		backgroundArea.setDisabled(true);
				
		Container<Actor> slideContainer = new Container<Actor>(widgetTable);
		slideContainer.fill();
		slideContainer.pad(20, 25, 20, 25);

		Stack slideAreaStack = new Stack(backgroundArea, slideContainer);
		
		ScrollPane scrollPane = new ScrollPane(slideAreaStack, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, false);
		
		Table rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.defaults().minSize(0);
		rootTable.add(scrollPane).expand().fill().colspan(2);
		rootTable.row();
		rootTable.defaults().minHeight(100).pad(10);
		rootTable.add(backButton).expandX().bottom().fillX().pad(0);
		rootTable.add(nextButton).expandX().bottom().fillX().pad(0);

		this.addActor(rootTable);

		nextButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int currentSlideIndex = slides.indexOf(currentSlide);
				if (slides.size() > currentSlideIndex + 1) {
					Widget newSlide = slides.get(currentSlideIndex + 1);
					slideContainer.setActor(newSlide);
					currentSlide = newSlide;
					if (slides.size() == currentSlideIndex + 2) {
						nextButton.setText("Finish");
					}
					backButton.setTouchable(Touchable.enabled);
					backButton.setDisabled(false);
					backButton.setText("Back");
				} else {
					finishedCallback.run();
				}
			}
		});

		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int currentSlideIndex = slides.indexOf(currentSlide);
				if (currentSlideIndex > 0) {
					Widget newSlide = slides.get(currentSlideIndex - 1);
					slideContainer.setActor(newSlide);
					currentSlide = newSlide;
					nextButton.setText("Next");
					if (currentSlideIndex == 1) {
						backButton.setTouchable(Touchable.disabled);
						backButton.setDisabled(true);
						backButton.setText("");
					}
				}
			}
		});
	}

	public void reset() {
		backButton.setTouchable(Touchable.disabled);
		backButton.setDisabled(true);
		backButton.setText("");
		nextButton.setText("Next");
		System.out.println(slides == null);
		currentSlide = slides.get(0);
		slideContainer.setActor(currentSlide);
	}
	
	private void setFontScale(Float fontScale) {
		// TODO
	}

	public void updateOnResize(int width, int height) {
		setFontScale(height / 1000F);
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override
	public void draw() {
		super.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
	}
}
