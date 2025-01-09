package com.smashspot.websocketchat.chat;

import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
	@Id
	private String id;
	private String chatId;
	private String senderId;
	private String recipientId;
	private String content;
	private Date timestamp;
	
}
