// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * {@link Stage} that can display multiple slides that the user can go through.
 */
public class SlideStage extends ResizableResettableStage {

	private List<Table> slides;

	Set<Disposable> disposables = new HashSet<>();
	private Skin skin;
	private OrthographicCamera camera;
	private Table rootTable;
	private Table currentSlide;
	private TextButton backButton;
	private TextButton nextButton;
	private Container<Table> slideContainer = new Container<>();
	private Runnable finishedCallback;

	/**
	 * Constructor.
	 * 
	 * @param viewport viewport for the stage
	 * @param slides   slides that are displayed
	 * @param camera   camera to use
	 * @param skin     game skin
	 */
	public SlideStage(Viewport viewport, List<Slide> slides, OrthographicCamera camera, Skin skin) {
		this(viewport, slides, () -> {
		}, camera, skin);
	}

	/**
	 * Constructor.
	 * 
	 * @param viewport         viewport for the stage
	 * @param slides           slides that are displayed
	 * @param finishedCallback callback to be executed when the user is done
	 * @param camera           camera to use
	 * @param skin             game skin
	 */
	public SlideStage(Viewport viewport, List<Slide> slides, Runnable finishedCallback, OrthographicCamera camera,
			Skin skin) {
		super(viewport);
		if (slides.isEmpty()) {
			throw new IllegalArgumentException("at least one slide is required");
		}
		this.camera = camera;
		this.skin = skin;
		this.slides = slides.stream().map(Slide::getTable).collect(Collectors.toList());
		this.finishedCallback = finishedCallback;
		initUi(this.slides);
	}

	private void initUi(List<Table> slides) {
		backButton = new TextButton("", skin);
		backButton.setDisabled(true);
		backButton.setTouchable(Touchable.disabled);

		nextButton = new TextButton("", skin);

		currentSlide = slides.get(0);

		TextArea backgroundArea = new TextArea(null, skin);
		backgroundArea.setDisabled(true);

		slideContainer.fill();
		slideContainer.pad(20, 25, 20, 20);
		slideContainer.setActor(currentSlide);

		Stack slideAreaStack = new Stack(backgroundArea, slideContainer);

		ScrollPane scrollPane = new ScrollPane(slideAreaStack, skin);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setOverscroll(false, false);

		rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.defaults().minSize(0);
		rootTable.add(scrollPane).expand().fill().colspan(2);
		rootTable.row();
		rootTable.defaults().minHeight(100).pad(0).expandX().bottom().fillX();
		rootTable.add(backButton);
		rootTable.add(nextButton);

		this.addActor(rootTable);

		nextButton.addListener(new ExceptionLoggingChangeListener(() -> {
			int currentSlideIndex = slides.indexOf(currentSlide);
			if (slides.size() > currentSlideIndex + 1) {
				Table newSlide = slides.get(currentSlideIndex + 1);
				slideContainer.setActor(newSlide);
				currentSlide = newSlide;
				if (slides.size() == currentSlideIndex + 2) {
					nextButton.setText("Finish");
				}
				backButton.setTouchable(Touchable.enabled);
				backButton.setDisabled(false);
				backButton.setText("Back");
				camera.update();
			} else {
				finishedCallback.run();
			}
		}));

		backButton.addListener(new ExceptionLoggingChangeListener(() -> {
			int currentSlideIndex = slides.indexOf(currentSlide);
			if (currentSlideIndex > 0) {
				Table newSlide = slides.get(currentSlideIndex - 1);
				slideContainer.setActor(newSlide);
				currentSlide = newSlide;
				nextButton.setText("Next");
				if (currentSlideIndex == 1) {
					backButton.setTouchable(Touchable.disabled);
					backButton.setDisabled(true);
					backButton.setText("");
				}
			}
		}));
	}

	@Override
	public void reset() {
		backButton.setTouchable(Touchable.disabled);
		backButton.setDisabled(true);
		backButton.setText("");
		String nextButtonText = slides.size() == 1 ? "Finish" : "Next";
		nextButton.setText(nextButtonText);
		currentSlide = slides.get(0);
		slideContainer.setActor(currentSlide);
	}

	@Override
	public void updateOnResize(int width, int height) {
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
		rootTable.pack();
		slides.forEach(slide -> {
			slide.pack();
			slide.getChildren().forEach(child -> {
				if (ClassReflection.isAssignableFrom(Table.class, child.getClass())) {
					((Table) child).pack();
				}
			});
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
	}

	public void setFinishedCallback(Runnable finishedCallback) {
		this.finishedCallback = finishedCallback;
	}

}
