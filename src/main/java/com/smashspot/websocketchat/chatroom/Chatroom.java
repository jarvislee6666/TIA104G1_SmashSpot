package com.smashspot.websocketchat.chatroom;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
@Getter
@Setter
public class Chatroom {
	@Id
	private String id;
	private String chatId;
	private String senderId;
	private String recipientId;

//	private Chatroom(Builder builder) {
//		this.chatId = builder.chatId;
//		this.senderId = builder.senderId;
//		this.recipientId = builder.recipientId;
//	}
//
//	public static Builder builder() {
//		return new Builder();
//	}
//
//	public static class Builder {
//        private String chatId;
//        private String senderId;
//        private String recipientId;
//
//        public Builder chatId(String chatId) {
//            this.chatId = chatId;
//            return this;
//        }
//
//        public Builder senderId(String senderId) {
//            this.senderId = senderId;
//            return this;
//        }
//
//        public Builder recipientId(String recipientId) {
//            this.recipientId = recipientId;
//            return this;
//        }
//
//        public Chatroom build() {
//            return new Chatroom(this);
//        }
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getChatId() {
//		return chatId;
//	}
//	public void setChatId(String chatId) {
//		this.chatId = chatId;
//	}
//	public String getSenderId() {
//		return senderId;
//	}
//	public void setSenderId(String senderId) {
//		this.senderId = senderId;
//	}
//	public String getRecipientId() {
//		return recipientId;
//	}
//	public void setRecipientId(String recipientId) {
//		this.recipientId = recipientId;
//	}
	
	

}
