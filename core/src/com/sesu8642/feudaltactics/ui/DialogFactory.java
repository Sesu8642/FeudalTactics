package com.sesu8642.feudaltactics.ui;

import java.util.function.Consumer;

import javax.inject.Inject;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DialogFactory {

	private Skin skin;

	@Inject
	public DialogFactory(Skin skin) {
		this.skin = skin;
	}
	
	public FeudalTacticsDialog createDialog(Consumer<Object> action) {
		return new FeudalTacticsDialog(skin) {
			public void result(Object result) {
				action.accept(result);
				this.remove();
			}
		};
	}
	
	public ConfirmDialog createConfirmDialog(String message, Runnable action) {
		return new ConfirmDialog(message, action, skin);
	}
	
}
