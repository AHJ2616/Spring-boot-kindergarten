package com.kinder.kindergarten.controller.employee;

import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.DTO.employee.ChatMessageDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.MemberService;
import com.kinder.kindergarten.service.employee.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    private final MemberService memberService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessage) {
        chatService.saveMessage(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverId().toString(),
                "/queue/messages",
                chatMessage
        );
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDTO chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @GetMapping("/api/chat/history/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(
            @PathVariable Long userId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<ChatMessageDTO> chatHistory = chatService.getChatHistory(
                principalDetails.getMember().getId(),
                userId
        );
        return ResponseEntity.ok(chatHistory);
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "employee/chat";
    }

    @GetMapping("/api/chat/users")
    @ResponseBody
    public ResponseEntity<List<MemberDTO>> getChatUsers(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        try {
            List<MemberDTO> users = memberService.getAllChatableUsers(principalDetails.getMember().getId());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/chat/read/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        chatService.markMessagesAsRead(chatRoomId, principalDetails.getMember().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/chat/unread-count/{userId}")
    @ResponseBody
    public ResponseEntity<Long> getUnreadMessageCount(
            @PathVariable Long userId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long count = chatService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(count);
    }
}