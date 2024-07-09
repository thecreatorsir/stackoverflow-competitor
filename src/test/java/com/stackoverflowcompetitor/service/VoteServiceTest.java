package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.common.AuthenticatedUserDetails;
import com.stackoverflowcompetitor.model.Answer;
import com.stackoverflowcompetitor.model.Question;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.model.Vote;
import com.stackoverflowcompetitor.repository.AnswerRepository;
import com.stackoverflowcompetitor.repository.QuestionRepository;
import com.stackoverflowcompetitor.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private AuthenticatedUserDetails authenticatedUserDetails;

    @InjectMocks
    private VoteService voteService;

    private User user;
    private Question question;
    private Answer answer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        question = new Question();
        question.setId(1L);
        question.setTitle("Test Question");

        answer = new Answer();
        answer.setId(1L);
        answer.setContent("Test Answer");
    }

    @Test
    void testVoteForQuestion_NewVote() {
        when(questionRepository.findById(1L)).thenReturn(java.util.Optional.of(question));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(voteRepository.findByUserAndQuestion(user, question)).thenReturn(null);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vote result = voteService.voteForQuestion(1L, true);

        assertNotNull(result);
        assertTrue(result.isUpvote());
        assertEquals(question, result.getQuestion());
        assertEquals(user, result.getUser());
    }

    @Test
    void testVoteForQuestion_ExistingVote_Reset() {
        Vote existingVote = new Vote();
        existingVote.setUpvote(true);
        existingVote.setQuestion(question);
        existingVote.setUser(user);

        when(questionRepository.findById(1L)).thenReturn(java.util.Optional.of(question));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(voteRepository.findByUserAndQuestion(user, question)).thenReturn(existingVote);

        Vote result = voteService.voteForQuestion(1L, true);

        assertNull(result);
        verify(voteRepository, times(1)).delete(existingVote);
    }

    @Test
    void testVoteForQuestion_ExistingVote_Change() {
        Vote existingVote = new Vote();
        existingVote.setUpvote(false);
        existingVote.setQuestion(question);
        existingVote.setUser(user);

        when(questionRepository.findById(1L)).thenReturn(java.util.Optional.of(question));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(voteRepository.findByUserAndQuestion(user, question)).thenReturn(existingVote);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vote result = voteService.voteForQuestion(1L, true);

        assertNotNull(result);
        assertTrue(result.isUpvote());
        verify(voteRepository, times(1)).save(existingVote);
    }

    @Test
    void testVoteForQuestion_QuestionNotFound() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            voteService.voteForQuestion(1L, true);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void testVoteForAnswer_NewVote() {
        when(answerRepository.findById(1L)).thenReturn(java.util.Optional.of(answer));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(voteRepository.findByUserAndAnswer(user, answer)).thenReturn(null);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vote result = voteService.voteForAnswer(1L, true);

        assertNotNull(result);
        assertTrue(result.isUpvote());
        assertEquals(answer, result.getAnswer());
        assertEquals(user, result.getUser());
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void testVoteForAnswer_ExistingVote_Reset() {
        Vote existingVote = new Vote();
        existingVote.setUpvote(true);
        existingVote.setAnswer(answer);
        existingVote.setUser(user);

        when(answerRepository.findById(1L)).thenReturn(java.util.Optional.of(answer));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(voteRepository.findByUserAndAnswer(user, answer)).thenReturn(existingVote);

        Vote result = voteService.voteForAnswer(1L, true);

        assertNull(result);
        verify(voteRepository, times(1)).delete(existingVote);
    }

    @Test
    void testVoteForAnswer_ExistingVote_Change() {
        Vote existingVote = new Vote();
        existingVote.setUpvote(false);
        existingVote.setAnswer(answer);
        existingVote.setUser(user);

        when(answerRepository.findById(1L)).thenReturn(java.util.Optional.of(answer));
        when(authenticatedUserDetails.getAuthenticatedUser()).thenReturn(user);
        when(voteRepository.findByUserAndAnswer(user, answer)).thenReturn(existingVote);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vote result = voteService.voteForAnswer(1L, true);

        assertNotNull(result);
        assertTrue(result.isUpvote());
        verify(voteRepository, times(1)).save(existingVote);
    }

    @Test
    void testVoteForAnswer_AnswerNotFound() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            voteService.voteForAnswer(1L, true);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(voteRepository, never()).save(any(Vote.class));
    }
}
