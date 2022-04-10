package ru.comgrid.server.api.decoration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DecorationRequest{
	public Long chatId;
	@NotBlank
	public List<Long> cellUnionIds;
}
