package ru.comgrid.server.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.io.File;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OpenFile {
    @Id
    String fileId;
    File image;
}
