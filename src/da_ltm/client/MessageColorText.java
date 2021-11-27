package da_ltm.client;
import java.awt.Color;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
public class MessageColorText {
    public static AttributeSet styleMessageColor(Color color){
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet colorMessage = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        colorMessage = styleContext.addAttribute(colorMessage, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        return colorMessage;
    }
}
