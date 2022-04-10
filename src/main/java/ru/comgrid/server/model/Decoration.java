package ru.comgrid.server.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Decoration{
	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private Long chatId;

	@Column(nullable = false)
	private Long cellUnionId;

	@Column(nullable = false)
	private String styles;
}

