package rentals.jwt_auth.service;

import java.util.List;

import org.springframework.stereotype.Service;
import rentals.jwt_auth.dto.MessageRequest;
import rentals.jwt_auth.model.Message;
import rentals.jwt_auth.repository.MessageRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getMessagesByUserId(Long userId) {
        return messageRepository.findByUserId(userId);
    }


    public Message sendMessage(MessageRequest request, Long userId) {
        Message message = new Message();
        message.setUserId(userId);
        message.setRentalId(request.getRentalId());
        message.setMessage(request.getMessage());

        return messageRepository.save(message);
    }
}
