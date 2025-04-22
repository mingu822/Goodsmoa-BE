package com.goodsmoa.goodsmoa_BE.chat.Config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChatWebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        logger.info("✅ WebSocket 연결됨 [{}]", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // ✅ 세션에서 저장된 속성을 가져와서 인증 확인
        Object auth = session.getAttributes().get("auth");

        if (auth == null || "unauthenticated".equals(auth)) {
            session.sendMessage(new TextMessage("❌ 인증되지 않은 사용자입니다."));
            return; // 🔥 인증되지 않은 사용자는 메시지를 처리하지 않음
        }

        log.info("💬 인증된 사용자 메시지 수신 [{}]: {}", session.getId(), message.getPayload());

        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage("📢 메시지: " + message.getPayload()));
            }
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        ByteBuffer buffer = message.getPayload();
        logger.info("🖼️ 바이너리 데이터(이미지) 수신 [{}]: {} bytes", session.getId(), buffer.remaining());

        // 파일 저장 경로 (실제 프로젝트에서는 경로를 환경변수로 설정 가능)
        File file = new File("uploads/image_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(buffer.array());
            fileOutputStream.flush();
        }

        logger.info("✅ 이미지 저장 완료 [{}]: {}", session.getId(), file.getAbsolutePath());

        // 클라이언트에게 응답 메시지 전송
        if (session.isOpen()) {
            session.sendMessage(new TextMessage("이미지 저장 완료: " + file.getAbsolutePath()));
            logger.info("📤 저장 완료 메시지 전송 [{}]", session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        logger.info("❌ WebSocket 연결 종료 [{}]: {}", session.getId(), status);
    }


}
