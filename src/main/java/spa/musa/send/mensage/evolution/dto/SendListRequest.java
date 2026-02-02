package spa.musa.send.mensage.evolution.dto;

import java.util.List;

/**
 * Request para enviar lista/menu de opções
 */
public record SendListRequest(
    String number,
    String title,
    String description,
    String buttonText,
    String footerText,
    List<Section> sections
) {
    public record Section(
        String title,
        List<Row> rows
    ) {}

    public record Row(
        String title,
        String description,
        String rowId
    ) {}

    public static SendListRequest create(String number, String title, String description, 
                                         String buttonText, List<Section> sections) {
        return new SendListRequest(number, title, description, buttonText, null, sections);
    }
}
