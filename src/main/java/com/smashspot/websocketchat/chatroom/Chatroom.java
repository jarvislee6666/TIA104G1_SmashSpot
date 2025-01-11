package com.smashspot.websocketchat.chatroom;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smashspot.member.model.MemberVO;

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
@Entity
public class Chatroom implements Serializable {

    @Id
    private String id;

    private String chatId;

//    @Transient
    private MemberVO sender; // 從 MemberVO 中獲取相關資訊
    
    @JsonProperty
    private String senderName; // 明確定義 senderName 屬性
    
    @Builder.Default
    private String recipientId = "Adm"; // 固定值 "Adm"
    
    private String lastMessage;
    
    private int unreadCount;
    

    public String getSenderName() {
        return sender != null ? sender.getName() : senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}