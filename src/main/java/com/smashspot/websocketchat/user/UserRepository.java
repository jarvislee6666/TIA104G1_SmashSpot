package com.smashspot.websocketchat.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

	List<User> findAllByStatus(Status status);
	
}