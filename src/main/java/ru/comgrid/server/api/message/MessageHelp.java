package ru.comgrid.server.api.message;

import org.jetbrains.annotations.NotNull;


import java.math.BigDecimal;


public enum MessageHelp{;
	public static final String USER_DESTINATION = "/connection/user/";
	public static final String TABLE_DESTINATION = "/connection/table_message/";

	@NotNull
	public static String userDestination(BigDecimal personId){
		return USER_DESTINATION + personId;
	}

	@NotNull
	public static String tableDestination(Long chatId){
	    return TABLE_DESTINATION + chatId;
	}
}

