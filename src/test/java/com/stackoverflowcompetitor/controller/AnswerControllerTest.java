package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.service.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AnswerControllerTest {

    @Mock
    private AnswerService answerService;

    @InjectMocks
    private AnswerController answerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(answerController).build();
    }

    @Test
    void testAnswerQuestion_Success() throws Exception {
        Answer answer = new Answer();
        answer.setId(1L);
        answer.setContent("Test content");

        when(answerService.answerQuestion(anyLong(), anyString(), any())).thenReturn(answer);

        MockMultipartFile media = new MockMultipartFile("media", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart("/answers/question/1")
                        .file(media)
                        .param("content", "Test content")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Test content")));

        verify(answerService, times(1)).answerQuestion(anyLong(), anyString(), any());
    }

    @Test
    void testAnswerQuestion_Failure() throws Exception {
        when(answerService.answerQuestion(anyLong(), anyString(), any())).thenThrow(new RuntimeException("Error"));

        MockMultipartFile media = new MockMultipartFile("media", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart("/answers/question/1")
                        .file(media)
                        .param("content", "Test content")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());

        verify(answerService, times(1)).answerQuestion(anyLong(), anyString(), any());
    }

    @Test
    void testAnswerToAnswer_Success() throws Exception {
        Answer answer = new Answer();
        answer.setId(1L);
        answer.setContent("Test reply");

        when(answerService.answerToAnswer(anyLong(), anyString(), any(), anyLong())).thenReturn(answer);

        MockMultipartFile media = new MockMultipartFile("media", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart("/answers/reply/1/1")
                        .file(media)
                        .param("content", "Test reply")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Test reply")));

        verify(answerService, times(1)).answerToAnswer(anyLong(), anyString(), any(), anyLong());
    }

    @Test
    void testAnswerToAnswer_Failure() throws Exception {
        when(answerService.answerToAnswer(anyLong(), anyString(), any(), anyLong())).thenThrow(new RuntimeException("Error"));

        MockMultipartFile media = new MockMultipartFile("media", "test.txt", "text/plain", "test content".getBytes());

        mockMvc.perform(multipart("/answers/reply/1/1")
                        .file(media)
                        .param("content", "Test reply")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());

        verify(answerService, times(1)).answerToAnswer(anyLong(), anyString(), any(), anyLong());
    }

    @Test
    void testSearchAnswers_Success() throws Exception {
        Answer answer = new Answer();
        answer.setId(1L);
        answer.setContent("Test answer");

        when(answerService.searchAnswers(anyString())).thenReturn(Collections.singletonList(answer));

        mockMvc.perform(get("/answers/search")
                        .param("searchTerm", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Test answer")));

        verify(answerService, times(1)).searchAnswers(anyString());
    }

    @Test
    void testSearchAnswers_Failure() throws Exception {
        when(answerService.searchAnswers(anyString())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/answers/search")
                        .param("searchTerm", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(answerService, times(1)).searchAnswers(anyString());
    }
}
