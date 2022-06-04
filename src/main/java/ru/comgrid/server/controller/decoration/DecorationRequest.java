package ru.comgrid.server.controller.decoration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
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
