package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.AnswerRepository;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.util.Constants;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AuthenticatedUserDetails authenticatedUserDetails;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private AnswerService answerService;

    private Question question;
    private User user;
    private Answer parentAnswer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        question = new Question();
        question.setId(1L);

        user = new User();
        user.setId(1L);

        parentAnswer = new Answer();
        parentAnswer.setId(1L);
        parentAnswer.setQuestion(question);
    }

    @Test
    void testAnswerQuestion_Success() throws IOException {
        String content = "Test answer content";
        MultipartFile media = mock(MultipartFile.class);
        String mediaUrl = "http://media.url";

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(mediaService.uploadFile(media)).thenReturn(mediaUrl);
        when(answerRepository.save(any(Answer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Answer result = answerService.answerQuestion(1L, content, media);

        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(question, result.getQuestion());
        assertEquals(user, result.getUser());
        assertEquals(mediaUrl, result.getMediaUrl());
    }

    @Test
    void testAnswerQuestion_InvalidContentLength() {
        String content = ""; // Invalid content length

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            answerService.answerQuestion(1L, content, null);
        });

        assertEquals("Content length must be between " + Constants.MIN_CONTENT_LENGTH + " and " + Constants.MAX_CONTENT_LENGTH + " characters", exception.getMessage());
    }

    @Test
    void testAnswerQuestion_QuestionNotFound() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            answerService.answerQuestion(1L, "Test content", null);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Question not found with id: 1", exception.getReason());
    }


    @Test
    void testAnswerToAnswer_Success() throws IOException {
        String content = "Test reply content";
        MultipartFile media = mock(MultipartFile.class);
        String mediaUrl = "http://media.url";

        when(answerRepository.findById(1L)).thenReturn(Optional.of(parentAnswer));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(mediaService.uploadFile(media)).thenReturn(mediaUrl);
        when(answerRepository.save(any(Answer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Answer result = answerService.answerToAnswer(1L, content, media, 1L);

        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(question, result.getQuestion());
        assertEquals(user, result.getUser());
        assertEquals(parentAnswer, result.getParentAnswer());
        assertEquals(mediaUrl, result.getMediaUrl());
    }

    @Test
    void testAnswerToAnswer_InvalidContentLength() {
        String content = ""; // Invalid content length

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            answerService.answerToAnswer(1L, content, null, 1L);
        });

        assertEquals("Content length must be between " + Constants.MIN_CONTENT_LENGTH + " and " + Constants.MAX_CONTENT_LENGTH + " characters", exception.getMessage());
    }

    @Test
    void testAnswerQuestion_MediaUploadIOException() throws IOException {
        String content = "Test answer content";
        MultipartFile media = mock(MultipartFile.class);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(mediaService.uploadFile(media)).thenThrow(new IOException("Error uploading media"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            answerService.answerQuestion(1L, content, media);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to upload media", exception.getReason());
        assertEquals("Error uploading media", exception.getCause().getMessage());
    }

    @Test
    void testAnswerToAnswer_AnswerNotFound() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            answerService.answerToAnswer(1L, "Test content", null, 1L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Answer not found with id: 1", exception.getReason());
    }

    @Test
    void testAnswerToAnswer_QuestionNotFound() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(parentAnswer));
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            answerService.answerToAnswer(1L, "Test content", null, 1L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Question not found with id: 1", exception.getReason());
    }

    @Test
    void testAnswerToAnswer_QuestionIdMismatch() {
        Question anotherQuestion = new Question();
        anotherQuestion.setId(2L);
        parentAnswer.setQuestion(anotherQuestion);

        when(answerRepository.findById(1L)).thenReturn(Optional.of(parentAnswer));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            answerService.answerToAnswer(1L, "Test content", null, 1L);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Question ID mismatch", exception.getReason());
    }

    @Test
    void testSearchAnswers_Success() {
        String searchTerm = "Test";
        when(answerRepository.searchAnswerByContent(searchTerm)).thenReturn(List.of(new Answer(), new Answer()));

        List<Answer> result = answerService.searchAnswers(searchTerm);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchAnswers_InvalidSearchTerm() {
        String searchTerm = ""; // Invalid search term

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            answerService.searchAnswers(searchTerm);
        });

        assertEquals("searchTerm length must be between " + Constants.MIN_SEARCH_STRING_LENGTH + " and " + Constants.MAX_SEARCH_STRING_LENGTH + " characters", exception.getMessage());
    }

}
