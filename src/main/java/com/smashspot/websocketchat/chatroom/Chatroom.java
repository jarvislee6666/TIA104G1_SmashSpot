package com.smashspot.websocketchat.chatroom;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
//@RedisHash("Chatroom")
public class Chatroom implements Serializable {

    @Id
    private String id;

    private String chatId;

    private MemberVO sender; // 從 MemberVO 中獲取相關資訊
    
    @JsonProperty
    private String senderName; // 明確定義 senderName 屬性，確保其可以被正常序列化
    
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
    
    
    @Override
    public String toString() {
        return "Chatroom{" +
                "id='" + id + '\'' +
                ", chatId='" + chatId + '\'' +
                ", sender={" +
                "memid=" + (sender != null ? sender.getMemid() : "0") +
                ", name='" + (sender != null ? sender.getName() : "Adm") + '\'' +
                '}' +
                ", recipientId='" + recipientId + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", unreadCount=" + unreadCount +
                '}';
    }
}