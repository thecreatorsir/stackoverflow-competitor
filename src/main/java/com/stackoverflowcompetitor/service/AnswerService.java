package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.AnswerRepository;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Answer answerQuestion(Long questionId, Answer answer) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionId));
        answer.setQuestion(question);
//        User user = new User();
//        user.setPassword("temp");
//        user.setUsername("temp");
//        answer.setUser(user);
        return answerRepository.save(answer);
    }

    public Answer answerToAnswer(Long answerId, Answer reply, Long questionID) {
        Answer parentAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Answer not found with id: " + answerId));

        if (!parentAnswer.getQuestion().getId().equals(questionID)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Question id mismatch");
        }

        Question question = questionRepository.findById(questionID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with id: " + questionID));//        User user = new User();
//        user.setPassword("temp");
//        user.setUsername("temp");
//        reply.setUser(user);
        reply.setQuestion(question);
        reply.setParentAnswer(parentAnswer);
        return answerRepository.save(reply);
    }
}
