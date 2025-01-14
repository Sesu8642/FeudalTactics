// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * Factory for creating dialogs.
 */
public class DialogFactory {

    private final Skin skin;

    @Inject
    public DialogFactory(Skin skin) {
        this.skin = skin;
    }

    /**
     * Creates a new dialog. Text and buttons can be added to it after it is
     * created.
     *
     * @param action action to execute once the dialog is confirmed
     * @return the created dialog
     */
    public FeudalTacticsDialog createDialog(Consumer<Object> action) {
        return new FeudalTacticsDialog(skin) {

            @Override
            public void result(Object result) {
                action.accept(result);
                this.remove();
            }

        };
    }

    /**
     * See {@link #createConfirmDialog(String, Runnable, Runnable)}, without action on cancel.
     */
    public FeudalTacticsDialog createConfirmDialog(String message, Runnable actionOnConfirm) {
        return createConfirmDialog(message, actionOnConfirm, () -> {
        });
    }

    /**
     * Creates a new dialog for having the user confirm something or cancel. With a given message and action that is
     * executed on confirmation.
     *
     * @param message         message to display in the dialog
     * @param actionOnConfirm action to execute on confirmation
     * @param actionOnCancel  action to execute on cancel
     * @return new dialog
     */
    public FeudalTacticsDialog createConfirmDialog(String message, Runnable actionOnConfirm, Runnable actionOnCancel) {
        FeudalTacticsDialog dialog = new FeudalTacticsDialog(skin) {

            @Override
            public void result(Object result) {
                if (Boolean.TRUE == result) {
                    actionOnConfirm.run();
                } else {
                    actionOnCancel.run();
                }
                this.remove();
            }

        };
        dialog.text(message);
        dialog.button("OK", true);
        dialog.button("Cancel", false);
        return dialog;
    }

    /**
     * Creates a new dialog with a given message, a copy button and action that is
     * executed on confirmation.
     *
     * @param message message to display in the dialog
     * @param action  action to execute on confirmation
     * @return new dialog
     */
    public FeudalTacticsDialog createInformationDialogWithCopyButton(String message, Runnable action) {
        FeudalTacticsDialog dialog = new FeudalTacticsDialog(skin) {

            @Override
            public void result(Object result) {
                if (Boolean.TRUE == result) {
                    action.run();
                    this.remove();
                }
            }

        };
        CopyButton copyButton = new CopyButton("Copy", skin, true);
        copyButton.addListener(new ExceptionLoggingChangeListener(
                () -> Gdx.app.getClipboard().setContents(message)));
        dialog.text(message + "\n").button("OK", true);
        // add in in a way that doesn't cause the dialog to hide automatically
        dialog.getButtonTable().add(copyButton);
        return dialog;
    }

}
