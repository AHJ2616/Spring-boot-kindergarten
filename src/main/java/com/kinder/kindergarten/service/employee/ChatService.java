package com.kinder.kindergarten.service.employee;

import com.kinder.kindergarten.DTO.employee.ChatMessageDTO;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.ChatMessage;
import com.kinder.kindergarten.entity.employee.ChatRoom;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.repository.employee.ChatMessageRepository;
import com.kinder.kindergarten.repository.employee.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public void saveMessage(ChatMessageDTO messageDTO) {
        Member sender = memberRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다."));
        Member receiver = memberRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));

        ChatRoom chatRoom = getOrCreateChatRoom(sender.getId(), receiver.getId());

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .chatRoom(chatRoom)
                .content(messageDTO.getContent())
                .build();

        chatMessageRepository.save(message);

        // 채팅방의 마지막 메시지 시간 업데이트
        chatRoom.setLastMessageTime(message.getSendTime());
        chatRoomRepository.save(chatRoom);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getChatHistory(Long userId1, Long userId2) {
        List<ChatMessage> messages = chatMessageRepository
                .findBySenderIdOrReceiverIdOrderBySendTimeDesc(userId1, userId2);

        return messages.stream()
                .map(message -> ChatMessageDTO.builder()
                        .id(message.getId())
                        .senderId(message.getSender().getId())
                        .senderName(message.getSender().getName())
                        .receiverId(message.getReceiver().getId())
                        .receiverName(message.getReceiver().getName())
                        .content(message.getContent())
                        .sendTime(message.getSendTime())
                        .isRead(message.isRead())
                        .build())
                .collect(Collectors.toList());
    }

    public ChatRoom getOrCreateChatRoom(Long member1Id, Long member2Id) {
        return chatRoomRepository.findByTwoParticipants(member1Id, member2Id)
                .orElseGet(() -> {
                    Member member1 = memberRepository.findById(member1Id)
                            .orElseThrow(() -> new RuntimeException("Member not found"));
                    Member member2 = memberRepository.findById(member2Id)
                            .orElseThrow(() -> new RuntimeException("Member not found"));

                    ChatRoom newRoom = ChatRoom.builder()
                            .participants(Arrays.asList(member1, member2))
                            .lastMessageTime(LocalDateTime.now())
                            .build();

                    return chatRoomRepository.save(newRoom);
                });
    }

    @Transactional
    public void markMessagesAsRead(Long chatRoomId, Long userId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomIdAndReceiverIdAndIsReadFalse(
                chatRoomId, userId);
        unreadMessages.forEach(message -> message.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);
    }

    @Transactional(readOnly = true)
    public Long getUnreadMessageCount(Long userId) {
        return chatMessageRepository.countByReceiverIdAndIsReadFalse(userId);
    }
}
