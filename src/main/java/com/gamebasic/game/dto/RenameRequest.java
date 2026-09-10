package com.gamebasic.game.dto;

import com.gamebasic.runcard.dto.RunCardRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;


@Getter
public class RenameRequest {
    @NotBlank
    @Size(min = 2, max = 12)
    private String playerName;
}
