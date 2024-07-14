package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class QuestionControllerTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();
    }

    @Test
    void testPostQuestion_Success() throws Exception {
        Question question = new Question();
        question.setId(1L);
        question.setTitle("Test Question");

        List<Long> tagIds = Arrays.asList(1L, 2L);

        when(questionService.postQuestion(any(Question.class), anyList())).thenReturn(question);

        mockMvc.perform(post("/questions/postQuestion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Test Question\" }")
                        .param("tagIds", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Question"));

        verify(questionService, times(1)).postQuestion(any(Question.class), anyList());
    }

    @Test
    void testPostQuestion_Failure() throws Exception {
        when(questionService.postQuestion(any(Question.class), anyList())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/questions/postQuestion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Test Question\" }")
                        .param("tagIds", "1", "2"))
                .andExpect(status().isInternalServerError());

        verify(questionService, times(1)).postQuestion(any(Question.class), anyList());
    }

    @Test
    void testGetTopVotedQuestions_Success() throws Exception {
        Question question1 = new Question();
        question1.setId(1L);
        question1.setTitle("Question 1");

        Question question2 = new Question();
        question2.setId(2L);
        question2.setTitle("Question 2");

        List<Question> questionList = Arrays.asList(question1, question2);
        Page<Question> page = new PageImpl<>(questionList);

        when(questionService.getTopVotedQuestions(any())).thenReturn(page);

        mockMvc.perform(get("/questions/top-voted")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Question 1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Question 2"));

        verify(questionService, times(1)).getTopVotedQuestions(any());
    }

    @Test
    void testGetTopVotedQuestions_Failure() throws Exception {
        when(questionService.getTopVotedQuestions(any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/questions/top-voted")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isInternalServerError());

        verify(questionService, times(1)).getTopVotedQuestions(any());
    }

    @Test
    void testGetQuestionsByTag_Success() throws Exception {
        Question question1 = new Question();
        question1.setId(1L);
        question1.setTitle("Question 1");

        Question question2 = new Question();
        question2.setId(2L);
        question2.setTitle("Question 2");

        List<Question> questionList = Arrays.asList(question1, question2);

        when(questionService.findByTagName(anyString())).thenReturn(questionList);

        mockMvc.perform(get("/questions/by-tag")
                        .param("tag", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Question 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Question 2"));

        verify(questionService, times(1)).findByTagName(anyString());
    }

    @Test
    void testGetQuestionsByTag_Failure() throws Exception {
        when(questionService.findByTagName(anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/questions/by-tag")
                        .param("tag", "java"))
                .andExpect(status().isInternalServerError());

        verify(questionService, times(1)).findByTagName(anyString());
    }

    @Test
    void testGetAllQuestions_Success() throws Exception {
        Question question1 = new Question();
        question1.setId(1L);
        question1.setTitle("Question 1");

        Question question2 = new Question();
        question2.setId(2L);
        question2.setTitle("Question 2");

        List<Question> questionList = Arrays.asList(question1, question2);

        when(questionService.getAllQuestions()).thenReturn(questionList);

        mockMvc.perform(get("/questions/getAllQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Question 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Question 2"));

        verify(questionService, times(1)).getAllQuestions();
    }

    @Test
    void testGetAllQuestions_Failure() throws Exception {
        when(questionService.getAllQuestions()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/questions/getAllQuestions"))
                .andExpect(status().isInternalServerError());

        verify(questionService, times(1)).getAllQuestions();
    }

    @Test
    void testSearchQuestions_Success() throws Exception {
        Question question1 = new Question();
        question1.setId(1L);
        question1.setTitle("Question 1");

        Question question2 = new Question();
        question2.setId(2L);
        question2.setTitle("Question 2");

        List<Question> questionList = Arrays.asList(question1, question2);

        when(questionService.searchQuestions(anyString())).thenReturn(questionList);

        mockMvc.perform(get("/questions/search")
                        .param("searchTerm", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Question 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Question 2"));

        verify(questionService, times(1)).searchQuestions(anyString());
    }

    @Test
    void testSearchQuestions_Failure() throws Exception {
        when(questionService.searchQuestions(anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/questions/search")
                        .param("searchTerm", "java"))
                .andExpect(status().isInternalServerError());

        verify(questionService, times(1)).searchQuestions(anyString());
    }
}
