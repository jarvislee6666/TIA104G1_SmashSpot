package com.smashspot.websocketchat.user;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class User {
	@Id
	private String nickName;
	private String fullName;
	private Status status;
//	public String getNickName() {
//		return nickName;
//	}
//	public void setNickName(String nickName) {
//		this.nickName = nickName;
//	}
//	public String getFullName() {
//		return fullName;
//	}
//	public void setFullName(String fullName) {
//		this.fullName = fullName;
//	}
//	public Status getStatus() {
//		return status;
//	}
//	public void setStatus(Status status) {
//		this.status = status;
//	}

	
}
