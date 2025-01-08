package com.smashspot.websocketchat.user;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {
	@Id
	private String nickName;
	private String fullName;
	private Status status;

}
