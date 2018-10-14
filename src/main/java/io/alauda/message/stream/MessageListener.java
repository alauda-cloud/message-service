package io.alauda.message.stream;

import io.alauda.message.domain.Message;
import io.alauda.message.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@EnableBinding(LoggerEventSource.class)
public class MessageListener {

    @Autowired
    MessageRepository messageRepository;

    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @StreamListener(LoggerEventSource.MESSAGE_QUEUE)
    public void input(Message message){
        messageRepository.save(message);
        if(message.getUserId()!=null){
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(message.getUserId()),"/queue/messages",message
                    .getContent());
        }else{
            simpMessagingTemplate.convertAndSend(String.format("/topic/project/%s/logs",message.getProjectId()),message
                    .getContent());
        }
    }
}
