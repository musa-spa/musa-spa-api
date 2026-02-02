package spa.musa.send.mensage.evolution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request para enviar mídia (imagem, vídeo, documento)
 */
public record SendMediaRequest(
    String number,
    @JsonProperty("mediatype") String mediaType,  // image, video, document, audio
    String media,        // URL ou base64
    String caption,      // Legenda (opcional)
    String fileName,     // Nome do arquivo (para documentos)
    @JsonProperty("delay") Integer delayMs
) {
    public static SendMediaRequest image(String number, String mediaUrl, String caption) {
        return new SendMediaRequest(number, "image", mediaUrl, caption, null, 1000);
    }

    public static SendMediaRequest document(String number, String mediaUrl, String fileName) {
        return new SendMediaRequest(number, "document", mediaUrl, null, fileName, 1000);
    }

    public static SendMediaRequest video(String number, String mediaUrl, String caption) {
        return new SendMediaRequest(number, "video", mediaUrl, caption, null, 1000);
    }
}
