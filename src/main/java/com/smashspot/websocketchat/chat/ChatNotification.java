package com.smashspot.websocketchat.chat;

import java.util.Date;

import javax.persistence.Transient;

import com.smashspot.member.model.MemberVO;

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
public class ChatNotification {
	private String id;
	@Transient
	private MemberVO sender; // 從 MemberVO 中獲取相關資訊
	private String senderName;

	@Builder.Default
	private String recipientId = "Adm"; // 固定值 "Adm"
	private String content;
	
	 public String getSenderName() {
	        return sender != null ? sender.getName() : senderName;
	    }
	
}
