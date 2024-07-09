package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VoteControllerTest {

    @Mock
    private VoteService voteService;

    @InjectMocks
    private VoteController voteController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(voteController).build();
    }

    @Test
    void testVoteForQuestion_Success() throws Exception {
        Vote vote = new Vote();
        vote.setId(1L);
        vote.setUpvote(true);

        when(voteService.voteForQuestion(anyLong(), anyBoolean())).thenReturn(vote);

        mockMvc.perform(post("/votes/question/1/true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.upvote").value(true));

        verify(voteService, times(1)).voteForQuestion(anyLong(), anyBoolean());
    }

    @Test
    void testVoteForQuestion_Failure() throws Exception {
        when(voteService.voteForQuestion(anyLong(), anyBoolean())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/votes/question/1/true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(voteService, times(1)).voteForQuestion(anyLong(), anyBoolean());
    }

    @Test
    void testVoteForAnswer_Success() throws Exception {
        Vote vote = new Vote();
        vote.setId(1L);
        vote.setUpvote(true);

        when(voteService.voteForAnswer(anyLong(), anyBoolean())).thenReturn(vote);

        mockMvc.perform(post("/votes/answer/1/true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.upvote").value(true));

        verify(voteService, times(1)).voteForAnswer(anyLong(), anyBoolean());
    }

    @Test
    void testVoteForAnswer_Failure() throws Exception {
        when(voteService.voteForAnswer(anyLong(), anyBoolean())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/votes/answer/1/true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(voteService, times(1)).voteForAnswer(anyLong(), anyBoolean());
    }
}
