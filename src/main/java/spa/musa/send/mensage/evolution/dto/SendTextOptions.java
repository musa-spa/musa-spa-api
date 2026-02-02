package spa.musa.send.mensage.evolution.dto;

public record SendTextOptions(
    Integer delay,
    String presence,
    Boolean linkPreview
) {}
