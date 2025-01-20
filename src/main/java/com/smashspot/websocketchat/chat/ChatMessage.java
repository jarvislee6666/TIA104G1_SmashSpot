package com.smashspot.websocketchat.chat;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smashspot.member.model.MemberVO;
import com.smashspot.websocketchat.chatroom.Chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {

	@Id
	private String id; // 亂數
	
	private String chatId; // xxx_Adm

	private MemberVO sender; // 從 MemberVO 中獲取相關資訊

	@JsonProperty
	private String senderName; // 明確定義 senderName 屬性

	@Builder.Default
	private String recipientId = "Adm"; // 固定值 "Adm"
	
	private String content;
	
	private Date timestamp;
	
	private boolean read; // 表示訊息是否已讀

	public String getSenderName() {
		return sender != null ? sender.getName() : senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public String toString() {
		return "ChatMessage{" + "id='" + id + '\'' + 
				", chatId='" + chatId + '\'' + 
				", sender={" + 
				"memid="+ (sender != null ? sender.getMemid() : "null") + 
				", name='"+ (sender != null ? sender.getName() : "Unknown") + '\'' + 
				", mempic='" + (sender != null ? sender.getMempic() : "null") + '\'' +
				'}' + 
				", recipientId='" + recipientId + '\''+ 
				", content='" + content + '\'' + ", timestamp=" + timestamp + ", read=" + read + '}';
	}
}
