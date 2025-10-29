// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import javax.inject.Inject;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
     * Creates a new dialog with a given message and action that is executed on confirmation.
     *
     * @param message message to display in the dialog
     * @param action  action to execute on confirmation
     * @return new dialog
     */
    public FeudalTacticsDialog createInformationDialog(String message, Runnable action) {
        FeudalTacticsDialog dialog = new FeudalTacticsDialog(skin) {

            @Override
            public void result(Object result) {
                if (Boolean.TRUE == result) {
                    action.run();
                    this.remove();
                }
            }

        };
        dialog.text(message + "\n").button("OK", true);
        return dialog;
    }

    /**
     * See {@link #addCopyButtonToDialog(Supplier, Dialog, String)} with a default copyButtonText.
     */
    public void addCopyButtonToDialog(Supplier<String> textSupplier, Dialog dialog) {
        CopyButton copyButton = ButtonFactory.createCopyButton("Copy", skin, true);
        copyButton.addListener(new ExceptionLoggingChangeListener(
            () -> Gdx.app.getClipboard().setContents(textSupplier.get())));
        // add in in a way that doesn't cause the dialog to hide automatically
        dialog.getButtonTable().add(copyButton);
    }

    /**
     * Adds a copy button to an existing dialog.
     *
     * @param textSupplier   supplier for the text that will be copied.
     * @param dialog         dialog to add the button to
     * @param copyButtonText label text for the button
     */
    public void addCopyButtonToDialog(Supplier<String> textSupplier, Dialog dialog, String copyButtonText) {
        CopyButton copyButton = ButtonFactory.createCopyButton(copyButtonText, skin, true);
        copyButton.addListener(new ExceptionLoggingChangeListener(
            () -> Gdx.app.getClipboard().setContents(textSupplier.get())));
        // add in in a way that doesn't cause the dialog to hide automatically
        dialog.getButtonTable().add(copyButton);
    }

    /**
     * Adds a text button to an existing dialog. When clicking the button, the dialog will not close.
     *
     * @param dialog     dialog to add the button to
     * @param buttonText label text for the button
     */
    public void addNonClosingTextButtonToDialog(Dialog dialog, String buttonText, Runnable listener) {
        TextButton button = ButtonFactory.createTextButton(buttonText, skin);
        button.addListener(new ExceptionLoggingChangeListener(listener));
        // add in in a way that doesn't cause the dialog to hide automatically
        dialog.getButtonTable().add(button);
    }

}
