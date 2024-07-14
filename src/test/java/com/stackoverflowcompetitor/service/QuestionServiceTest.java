package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.Tag;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.repository.TagRepository;
import com.stackoverflowcompetitor.util.Constants;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private AuthenticatedUserDetails authenticatedUserDetails;

    @InjectMocks
    private QuestionService questionService;

    private User user;
    private Question question;
    private Tag tag;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        question = new Question();
        question.setId(1L);
        question.setTitle("Test Question");
        question.setContent("Test Content");

        tag = new Tag();
        tag.setId(1L);
        tag.setName("testtag");
    }

    @Test
    void testPostQuestion_Success() {
        List<Long> tagIds = List.of(1L);
        List<Tag> tags = List.of(tag);

        when(tagRepository.findAllById(tagIds)).thenReturn(tags);
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(questionRepository.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Question result = questionService.postQuestion(question, tagIds);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(tags, result.getTags());
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void testPostQuestion_InvalidContentLength() {
        List<Long> tagIds = List.of(1L);
        question.setContent(""); // Set invalid content

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            questionService.postQuestion(question, tagIds);
        });

        assertEquals("Content length must be between " + Constants.MIN_CONTENT_LENGTH + " and " + Constants.MAX_CONTENT_LENGTH + " characters", exception.getMessage());
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testPostQuestion_Error() {
        List<Long> tagIds = List.of(1L);
        when(tagRepository.findAllById(tagIds)).thenThrow(new RuntimeException("Error posting question"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            questionService.postQuestion(question, tagIds);
        });

        assertEquals("Error posting question", exception.getMessage());
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testGetTopVotedQuestions() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Question> page = new PageImpl<>(List.of(question));

        when(questionRepository.findTopVotedQuestions(pageable)).thenReturn(page);

        Page<Question> result = questionService.getTopVotedQuestions(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(questionRepository, times(1)).findTopVotedQuestions(pageable);
    }

    @Test
    void testFindByTagName() {
        String tagName = "testtag";
        List<Question> questions = List.of(question);

        when(questionRepository.findByTags_Name(tagName)).thenReturn(questions);

        List<Question> result = questionService.findByTagName(tagName);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(questionRepository, times(1)).findByTags_Name(tagName);
    }

    @Test
    void testPostQuestion_InvalidTitleLength() {
        List<Long> tagIds = List.of(1L);
        question.setTitle(""); // Set invalid title

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            questionService.postQuestion(question, tagIds);
        });

        assertEquals("Title length must be between " + Constants.MIN_TITLE_LENGTH + " and " + Constants.MAX_TITLE_LENGTH + " characters", exception.getMessage());
        verify(questionRepository, never()).save(any(Question.class));
    }


    @Test
    void testGetAllQuestions() {
        List<Question> questions = List.of(question);

        when(questionRepository.findAll()).thenReturn(questions);

        List<Question> result = questionService.getAllQuestions();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    void testSearchQuestions() {
        String searchTerm = "Test";
        List<Question> questions = List.of(question);

        when(questionRepository.searchQuestionsByTitleOrContent(searchTerm)).thenReturn(questions);

        List<Question> result = questionService.searchQuestions(searchTerm);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(questionRepository, times(1)).searchQuestionsByTitleOrContent(searchTerm);
    }

    @Test
    void testSearchQuestions_InvalidSearchTermLength() {
        String searchTerm = ""; // Set invalid search term

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            questionService.searchQuestions(searchTerm);
        });

        assertEquals("searchTerm length must be between " + Constants.MIN_SEARCH_STRING_LENGTH + " and " + Constants.MAX_SEARCH_STRING_LENGTH + " characters", exception.getMessage());
        verify(questionRepository, never()).searchQuestionsByTitleOrContent(anyString());
    }
}
