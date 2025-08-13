package rentals.jwt_auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rentals.jwt_auth.dto.MessageRequest;
import rentals.jwt_auth.model.Message;
import rentals.jwt_auth.model.User;
import rentals.jwt_auth.repository.MessageRepository;
import rentals.jwt_auth.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Message> getAll() {
        return messageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Message> getById(Long id) {
        return messageRepository.findById(id);
    }

    public Message create(MessageRequest req, String senderEmail) {
        Message m = new Message();
        if (req.getMessage() != null) m.setMessage(req.getMessage());
        if (req.getRentalId() != null) m.setRentalId(req.getRentalId());
        if (senderEmail != null && !senderEmail.isBlank()) {
            Optional<User> sender = userRepository.findByEmail(senderEmail);
            sender.ifPresent(user -> m.setUserId(user.getId()));
        }
        return messageRepository.save(m);
    }

    public Optional<Message> update(Long id, MessageRequest req) {
        return messageRepository.findById(id).map(existing -> {
            if (req.getMessage() != null) existing.setMessage(req.getMessage());
            if (req.getRentalId() != null) existing.setRentalId(req.getRentalId());
            return messageRepository.save(existing);
        });
    }

    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    public void deleteAll() {
        messageRepository.deleteAll();
    }
}
