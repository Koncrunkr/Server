package ru.comgrid.server.controller.table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewChat implements Serializable{
    @NotEmpty
    private String name;
    @NotNull
    private int width;
    @NotNull
    private int height;
    @Schema(nullable = true)
    private MultipartFile avatarFile;
    @Schema(nullable = true)
    private String avatarLink;
}
