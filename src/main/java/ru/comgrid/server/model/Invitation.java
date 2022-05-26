package ru.comgrid.server.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
@ToString
@Entity
@Getter
@Setter
public class Invitation{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private Long chatId;

	@Column(nullable = false, unique = true)
	private String invitationCode;

	public Invitation(Long chatId){
		this.chatId = chatId;
		this.invitationCode = UUID.randomUUID().toString();
	}
}
