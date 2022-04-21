package ru.comgrid.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class MessageId implements Serializable{
	private Long chatId;
	private Integer x;
	private Integer y;
}
