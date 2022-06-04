package ru.comgrid.server.controller.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangeRightsRequest{
    @NotNull long chatId;
    @Schema(defaultValue = "314159265358979323846")
    @NotEmpty String userId;
    @Schema(defaultValue = "3")
    @NotNull long rights;
}
