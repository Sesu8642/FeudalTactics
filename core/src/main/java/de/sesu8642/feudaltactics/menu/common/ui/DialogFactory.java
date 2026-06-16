// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Factory for creating dialogs.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DialogFactory {

    private final Skin skin;
    private final PlatformInsetsProvider platformInsetsProvider;

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
        final FeudalTacticsDialog dialog = new FeudalTacticsDialog(skin) {

            @Override
            public void result(Object result) {
                if (Boolean.TRUE == result) {
                    actionOnConfirm.run();
                } else {
                    actionOnCancel.run();
                }
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
        final FeudalTacticsDialog dialog = new FeudalTacticsDialog(skin) {

            @Override
            public void result(Object result) {
                if (Boolean.TRUE == result) {
                    action.run();
                }
            }

        };
        dialog.text(message + "\n").button("OK", true);
        return dialog;
    }

    /**
     * See {@link #addCopyButtonToDialog(Supplier, FeudalTacticsDialog, String)} with a default copyButtonText.
     */
    public void addCopyButtonToDialog(Supplier<String> textSupplier, FeudalTacticsDialog dialog) {
        final CopyButton copyButton = ButtonFactory.createCopyButton("Copy", skin, true);
        copyButton.addListener(new ExceptionLoggingChangeListener(
            () -> Gdx.app.getClipboard().setContents(textSupplier.get())));
        // add in a way that doesn't cause the dialog to hide automatically
        dialog.getButtonGroup().addActor(copyButton);
    }

    /**
     * Adds a copy button to an existing dialog.
     *
     * @param textSupplier   supplier for the text that will be copied.
     * @param dialog         dialog to add the button to
     * @param copyButtonText label text for the button
     */
    public void addCopyButtonToDialog(Supplier<String> textSupplier, FeudalTacticsDialog dialog,
                                      String copyButtonText) {
        final CopyButton copyButton = ButtonFactory.createCopyButton(copyButtonText, skin, true);
        copyButton.addListener(new ExceptionLoggingChangeListener(
            () -> Gdx.app.getClipboard().setContents(textSupplier.get())));
        // add in a way that doesn't cause the dialog to hide automatically
        dialog.getButtonGroup().addActor(copyButton);
    }

    /**
     * Adds a text button to an existing dialog. When clicking the button, the dialog will not close.
     *
     * @param dialog     dialog to add the button to
     * @param buttonText label text for the button
     */
    public void addNonClosingTextButtonToDialog(FeudalTacticsDialog dialog, String buttonText, Runnable listener) {
        final TextButton button = ButtonFactory.createTextButton(buttonText, skin);
        button.addListener(new ExceptionLoggingChangeListener(listener));
        // add in a way that doesn't cause the dialog to hide automatically
        dialog.getButtonGroup().addActor(button);
    }

    /**
     * Adds a select box input to an existing dialog.
     *
     * @param dialog         dialog to add the select box to
     * @param selectBoxItems items to be selctable
     * @return the newly created select box
     */
    public <T> InsetsRespectingSelectBox<T> addSelectBoxToDialog(FeudalTacticsDialog dialog, List<T> selectBoxItems) {
        InsetsRespectingSelectBox<T> selectBox = new InsetsRespectingSelectBox<>(skin, platformInsetsProvider);
        selectBox.setItems((T[]) selectBoxItems.toArray());
        dialog.getContentTable().add(selectBox).left();
        return selectBox;
    }

}
