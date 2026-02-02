package spa.musa.send.mensage.evolution.dto;

public record SendTextRequest(
    String number,
    String text,
    SendTextOptions options
) {
    public SendTextRequest(String number, String text) {
        this(number, text, new SendTextOptions(1200, "composing", false));
    }
}
