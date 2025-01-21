package com.smashspot.reservation.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReservationWebSocket {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 用於將更新後的「剩餘可預約資訊」(或其他需要的資料) 推送給訂閱 "/topic/reservationUpdate/{stadiumId}" 的所有人
     */
    public void broadcastReservationUpdate(Integer stadiumId, Object payload) {
        // payload 可以是 Map 或 DTO，內含更新後的各時段 leftover
        messagingTemplate.convertAndSend(
            "/topic/reservationUpdate/" + stadiumId,
            payload
        );
    }
}
