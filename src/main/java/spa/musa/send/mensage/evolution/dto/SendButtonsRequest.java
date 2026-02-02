package spa.musa.send.mensage.evolution.dto;

import java.util.List;

/**
 * Request para enviar mensagem com botões
 */
public record SendButtonsRequest(
    String number,
    String title,
    String description,
    String footer,
    List<Button> buttons
) {
    public record Button(
        String type,      // reply
        String buttonText,
        String buttonId
    ) {
        public static Button reply(String text, String id) {
            return new Button("reply", text, id);
        }
    }

    public static SendButtonsRequest create(String number, String title, String description, List<Button> buttons) {
        return new SendButtonsRequest(number, title, description, null, buttons);
    }
}
