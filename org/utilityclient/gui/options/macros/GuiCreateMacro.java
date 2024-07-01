package org.utilityclient.gui.options.macros;

import net.minecraft.client.resources.I18n;
import org.utilityclient.macro.Macro;
import org.utilityclient.macro.MacroManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import java.io.IOException;

public class GuiCreateMacro extends GuiScreen {
    private final GuiScreen parentScreen;
    private String title;
    private GuiTextField nameInput;
    private GuiTextField messageInput;
    private int currentKeyCode = 0;
    private boolean listeningForInput;
    private boolean editMode = false;
    private Macro macro;

    /**
     * Create
     * @param parentScreenIn The parent screen
     */
    public GuiCreateMacro(GuiScreen parentScreenIn)
    {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Edit
     * @param parentScreenIn The parent screen
     * @param macroIn The macro, that should be edited.
     */
    public GuiCreateMacro(GuiScreen parentScreenIn, Macro macroIn) {
        this.parentScreen = parentScreenIn;
        editMode = true;
        macro = macroIn;
        currentKeyCode = macro.keyCode;
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        title = I18n.format("uc.options.macro.title");

        nameInput = new GuiTextField(1, fontRendererObj, width / 2 - 100, height / 2 - 66 +1, 124, 18);
        nameInput.setMaxStringLength(255);
        nameInput.setFocused(true);
        nameInput.setText(editMode ? macro.name : I18n.format("uc.options.macro.default.name"));

        messageInput = new GuiTextField(2, fontRendererObj, width / 2 - 100, height / 2 - 22 +1, 200, 18);
        messageInput.setMaxStringLength(100);
        messageInput.setFocused(false);
        messageInput.setText(editMode ? macro.message : I18n.format("uc.options.macro.default.message"));

        buttonList.add(new GuiButton(200, width / 2 - 100, this.height / 2 + 22, 100, 20, I18n.format("uc.options.macro.cancel")));
        buttonList.add(new GuiButton(201, width / 2, this.height / 2 + 22, 100, 20, I18n.format("uc.options.macro.save"), editMode));
        buttonList.add(new GuiButton(202, width / 2 + 25, height / 2 - 66, 75, 20, "None"));

        if(editMode) buttonList.get(2).displayString = Keyboard.getKeyName(macro.keyCode);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);

        fontRendererObj.drawStringWithShadow(I18n.format("uc.options.macro.name"), this.width / 2f - 100, height / 2f - 77, 16777215);
        fontRendererObj.drawStringWithShadow(I18n.format("uc.options.macro.message"), this.width / 2f - 100, height / 2f - 33, 16777215);
        nameInput.drawTextBox();
        messageInput.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void updateScreen()
    {
        nameInput.updateCursorCounter();
        messageInput.updateCursorCounter();
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
        }

        if (button.id == 201) {
            if (currentKeyCode != 0) {
                buttonList.get(1).enabled = true;
                try {
                    MacroManager.save(editMode ? macro.name : nameInput.getText(), new Macro(nameInput.getText(), messageInput.getText(), currentKeyCode));
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }

                if(editMode) {
                    try {
                        MacroManager.reload();
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }
                    this.mc.displayGuiScreen(new GuiMacroManager(((GuiMacroManager) parentScreen).parent));
                } else {
                    this.mc.displayGuiScreen(this.parentScreen);
                }
            }else {
                buttonList.get(1).enabled = false;
            }
        }

        if(button.id == 202) {
            listeningForInput = !listeningForInput;
            if (listeningForInput) button.displayString = "Press a key...";
            else button.displayString = Keyboard.getKeyName(currentKeyCode);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameInput.mouseClicked(mouseX, mouseY, mouseButton);
        messageInput.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if(listeningForInput) {
            onInput(keyCode);
        }else {
            nameInput.textboxKeyTyped(typedChar, keyCode);
            messageInput.textboxKeyTyped(typedChar, keyCode);
        }

    }

    public void onInput(int keyCode) {
        if (keyCode != 1) currentKeyCode = keyCode;
        listeningForInput = false;
        buttonList.get(2).displayString = Keyboard.getKeyName(currentKeyCode);
        buttonList.get(1).enabled = true;
    }
}
