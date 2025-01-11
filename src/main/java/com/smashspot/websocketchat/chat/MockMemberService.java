package com.smashspot.websocketchat.chat;

import java.util.Optional;

import com.smashspot.member.model.MemberService;

public class MockMemberService extends MemberService {

    @Override
    public Optional<String> getMemberNameById(Integer memberId) {
    	System.out.println("Mock getMemberNameById called with: " + memberId);
        if (memberId == 1) {
            return Optional.of("JohnDoe");
        }
        return Optional.empty();
    }
}
