package ru.comgrid.server.api.decoration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.comgrid.server.api.user.AccessService;
import ru.comgrid.server.exception.IllegalAccessException;
import ru.comgrid.server.model.Decoration;
import ru.comgrid.server.model.Right;
import ru.comgrid.server.repository.DecorationRepository;

import java.util.List;

import static ru.comgrid.server.api.user.UserHelp.extractId;

@Controller
@RestController
@RequestMapping("/decoration")
public class DecorationController{
	private final DecorationRepository decorationRepository;
	private final AccessService accessService;

	public DecorationController(
		@Autowired DecorationRepository decorationRepository,
		@Autowired AccessService accessService
	){
		this.decorationRepository = decorationRepository;
		this.accessService = accessService;
	}

	@PostMapping("/list")
	public ResponseEntity<List<Decoration>> getDecorations(
		@AuthenticationPrincipal UserDetails user,
		@RequestBody DecorationRequest decorationRequest
	){
		if(!accessService.hasAccessTo(extractId(user), decorationRequest.getChatId(), Right.Read))
			throw new IllegalAccessException("chat.read_messages");

		return ResponseEntity.ok(
			decorationRepository.findAllByCellUnionIdInAndAndChatId(
				decorationRequest.getCellUnionIds(),
				decorationRequest.getChatId()
			)
		);
	}
}
